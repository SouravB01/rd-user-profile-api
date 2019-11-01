package uk.gov.hmcts.reform.userprofileapi.service.impl;

import feign.FeignException;
import feign.Response;
import feign.RetryableException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import uk.gov.hmcts.reform.userprofileapi.controller.advice.InvalidRequest;
import uk.gov.hmcts.reform.userprofileapi.controller.request.UpdateUserDetails;
import uk.gov.hmcts.reform.userprofileapi.controller.response.AttributeResponse;
import uk.gov.hmcts.reform.userprofileapi.controller.response.RoleAdditionResponse;
import uk.gov.hmcts.reform.userprofileapi.controller.response.RoleDeletionResponse;
import uk.gov.hmcts.reform.userprofileapi.controller.response.UserProfileResponse;
import uk.gov.hmcts.reform.userprofileapi.controller.response.UserProfileRolesResponse;
import uk.gov.hmcts.reform.userprofileapi.domain.entities.UserProfile;
import uk.gov.hmcts.reform.userprofileapi.domain.enums.IdamStatus;
import uk.gov.hmcts.reform.userprofileapi.domain.enums.ResponseSource;
import uk.gov.hmcts.reform.userprofileapi.domain.enums.UserProfileField;
import uk.gov.hmcts.reform.userprofileapi.domain.feign.IdamFeignClient;
import uk.gov.hmcts.reform.userprofileapi.exception.ResourceNotFoundException;
import uk.gov.hmcts.reform.userprofileapi.repository.UserProfileRepository;
import uk.gov.hmcts.reform.userprofileapi.resource.UpdateUserProfileData;
import uk.gov.hmcts.reform.userprofileapi.service.AuditService;
import uk.gov.hmcts.reform.userprofileapi.service.IdamService;
import uk.gov.hmcts.reform.userprofileapi.service.ResourceUpdator;
import uk.gov.hmcts.reform.userprofileapi.service.ValidationService;
import uk.gov.hmcts.reform.userprofileapi.util.JsonFeignResponseHelper;
import uk.gov.hmcts.reform.userprofileapi.util.UserProfileMapper;

@Service
@Slf4j
public class UserProfileUpdator implements ResourceUpdator<UpdateUserProfileData> {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private IdamFeignClient idamClient;

    @Autowired
    private IdamService idamService;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private AuditService auditService;

    @Override
    public AttributeResponse update(UpdateUserProfileData updateUserProfileData, String userId, String origin) {

        AttributeResponse attributeResponse = new AttributeResponse(HttpStatus.OK);
        boolean isExuiUpdate = validationService.isExuiUpdateRequest(origin);
        ResponseSource source = (StringUtils.isEmpty(origin) || !isExuiUpdate) ? ResponseSource.SYNC : ResponseSource.API;

        UserProfile userProfile = validationService.validateUpdate(updateUserProfileData, userId, source);

        if (isExuiUpdate) {
            attributeResponse = updateSidamAndUserProfile(updateUserProfileData, userProfile, source, userId);
        } else {
            UserProfileMapper.mapUpdatableFields(updateUserProfileData, userProfile, false);
            doPersistUserProfile(userProfile, source);
        }
        return attributeResponse;
    }

    public AttributeResponse updateSidamAndUserProfile(UpdateUserProfileData updateUserProfileData, UserProfile userProfile, ResponseSource source, String userId) {
        validationService.isValidForUserDetailUpdate(updateUserProfileData, userProfile, source);
        UpdateUserDetails updateUserDetails = UserProfileMapper.mapIdamUpdateStatusRequest(updateUserProfileData);
        AttributeResponse attributeResponse = idamService.updateUserDetails(updateUserDetails, userId);
        if((HttpStatus.valueOf(attributeResponse.getIdamStatusCode()).is2xxSuccessful())) {
            UserProfileMapper.mapUpdatableFields(updateUserProfileData, userProfile, true);
            doPersistUserProfile(userProfile, source);
        }
        return attributeResponse;
    }


    private void doPersistUserProfile(UserProfile userProfile, ResponseSource responseSource) {
        UserProfile result = null;
        HttpStatus status = HttpStatus.OK;
        try {
            result = userProfileRepository.save(userProfile);
        } catch (Exception ex) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        auditService.persistAudit(status, result, responseSource);
        if (!status.is2xxSuccessful()) {
            throw new RuntimeException("Error while persisting user profile");
        }
    }


    @Override
    public UserProfileRolesResponse updateRoles(UpdateUserProfileData profileData, String userId) {
        UserProfileRolesResponse userProfileResponse = new UserProfileRolesResponse();
        UserProfile userProfile = validateUserStatus(userId);
        if (!CollectionUtils.isEmpty(profileData.getRolesAdd())) {
            log.info("Add idam roles for userId :" + userId);
            RoleAdditionResponse addRolesResponse;
            HttpStatus httpStatus;
            try (Response response = idamClient.addUserRoles(profileData.getRolesAdd(), userId)) {
                httpStatus = JsonFeignResponseHelper.toResponseEntity(response, Optional.empty()).getStatusCode();
                addRolesResponse = new RoleAdditionResponse(httpStatus);
            } catch (FeignException ex) {
                httpStatus = getHttpStatusFromFeignException(ex);
                auditService.persistAudit(httpStatus, userProfile, ResponseSource.API);
                addRolesResponse = new RoleAdditionResponse(httpStatus);
            }
            userProfileResponse.setRoleAdditionResponse(addRolesResponse);
        }

        if (!CollectionUtils.isEmpty(profileData.getRolesDelete())) {
            log.info("Delete idam roles for userId :" + userId);
            List<RoleDeletionResponse> roleDeletionResponse = new ArrayList<>();
            profileData.getRolesDelete().forEach(role -> roleDeletionResponse.add(deleteRolesInIdam(userId, role.getName(), userProfile)));
            userProfileResponse.setRoleDeletionResponse(roleDeletionResponse);
        }
        return userProfileResponse;
    }

    private RoleDeletionResponse deleteRolesInIdam(String userId, String roleName, UserProfile userProfile) {
        HttpStatus httpStatus;
        try (Response response = idamClient.deleteUserRole(userId, roleName)) {
            httpStatus = JsonFeignResponseHelper.toResponseEntity(response, Optional.empty()).getStatusCode();
        } catch (FeignException ex) {
            httpStatus = getHttpStatusFromFeignException(ex);
            auditService.persistAudit(httpStatus, userProfile, ResponseSource.API);
        }
        return new RoleDeletionResponse(roleName, httpStatus);
    }

    private HttpStatus getHttpStatusFromFeignException(FeignException ex) {
        return (ex instanceof RetryableException)
                ? HttpStatus.INTERNAL_SERVER_ERROR
                : HttpStatus.valueOf(ex.status());
    }

    private UserProfile validateUserStatus(String userId) {
        Optional<UserProfile> userProfileOptional = userProfileRepository.findByIdamId(userId);
        if (!userProfileOptional.isPresent()) {
            throw new ResourceNotFoundException("could not find user profile for userId: or status is not active " + userId);
        } else if (!IdamStatus.ACTIVE.equals(userProfileOptional.get().getStatus())) {
            throw new InvalidRequest("UserId status is not active");
        }
        return userProfileOptional.get();
    }

}

