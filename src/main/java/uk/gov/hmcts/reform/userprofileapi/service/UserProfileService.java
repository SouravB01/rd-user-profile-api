package uk.gov.hmcts.reform.userprofileapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.userprofileapi.client.CreateUserProfileResponse;
import uk.gov.hmcts.reform.userprofileapi.client.GetUserProfileResponse;
import uk.gov.hmcts.reform.userprofileapi.client.GetUserProfileWithRolesResponse;
import uk.gov.hmcts.reform.userprofileapi.client.RequestData;

@Service
public class UserProfileService<T extends RequestData> {

    @Autowired
    private ResourceCreator<T> resourceCreator;
    @Autowired
    private ResourceRetriever<T> resourceRetriever;
    @Autowired
    private ResourceUpdator<T> resourceUpdator;

    public CreateUserProfileResponse create(T requestData) {
        return new CreateUserProfileResponse(resourceCreator.create(requestData));
    }

    public GetUserProfileWithRolesResponse retrieveWithRoles(T requestData) {
        return new GetUserProfileWithRolesResponse(resourceRetriever.retrieve(requestData, true));
    }

    public GetUserProfileResponse retrieve(T requestData) {
        return new GetUserProfileResponse(resourceRetriever.retrieve(requestData, false));
    }

    public void update(T updateData, String userId) {
        resourceUpdator.update(updateData, resourceRetriever, userId);
    }

}