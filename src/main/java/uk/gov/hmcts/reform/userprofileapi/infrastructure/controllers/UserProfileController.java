package uk.gov.hmcts.reform.userprofileapi.infrastructure.controllers;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static uk.gov.hmcts.reform.userprofileapi.infrastructure.clients.IdentifierName.EMAIL;
import static uk.gov.hmcts.reform.userprofileapi.infrastructure.clients.IdentifierName.UUID;

import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.userprofileapi.domain.service.UserProfileService;
import uk.gov.hmcts.reform.userprofileapi.infrastructure.clients.CreateUserProfileData;
import uk.gov.hmcts.reform.userprofileapi.infrastructure.clients.CreateUserProfileResponse;
import uk.gov.hmcts.reform.userprofileapi.infrastructure.clients.GetUserProfileResponse;
import uk.gov.hmcts.reform.userprofileapi.infrastructure.clients.GetUserProfileWithRolesResponse;
import uk.gov.hmcts.reform.userprofileapi.infrastructure.clients.RequestData;
import uk.gov.hmcts.reform.userprofileapi.infrastructure.clients.UserProfileIdentifier;

@Api(
    value = "/v1/userprofile",
    consumes = APPLICATION_JSON_UTF8_VALUE,
    produces = APPLICATION_JSON_UTF8_VALUE
)

@RequestMapping(
    path = "/v1/userprofile",
    consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
    produces = MediaType.APPLICATION_JSON_UTF8_VALUE
)

@Slf4j
@RestController
public class UserProfileController {

    private UserProfileService<RequestData> userProfileService;

    public UserProfileController(UserProfileService<RequestData> userProfileService) {
        this.userProfileService = userProfileService;
    }

    @ApiOperation("Create a User Profile")
    @ApiResponses({
        @ApiResponse(
            code = 201,
            message = "Create a User Profile using request body",
            response = CreateUserProfileResponse.class
        ),
        @ApiResponse(
            code = 400,
            message = "Bad Request",
            response = String.class
        ),
        @ApiResponse(
            code = 500,
            message = "Internal Server Error",
            response = String.class
        )
    })

    @PostMapping(
        consumes = APPLICATION_JSON_UTF8_VALUE,
        produces = APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public ResponseEntity<CreateUserProfileResponse> createUserProfile(@Valid @RequestBody CreateUserProfileData createUserProfileData) {
        log.info("Creating new User Profile");

        requireNonNull(createUserProfileData, "createUserProfileData cannot be null");

        CreateUserProfileResponse resource = userProfileService.create(createUserProfileData);
        log.info("idamid:" + resource.getIdamId() + "idamRegistrationResponse:" + resource.getIdamRegistrationResponse());
        return ResponseEntity.status(HttpStatus.CREATED).body(resource);

    }

    @ApiOperation("Retrieves user profile data by id")
    @ApiResponses({
        @ApiResponse(
            code = 200,
            message = "Representation of a user profile data",
            response = String.class
        ),
        @ApiResponse(
            code = 400,
            message = "Bad Request",
            response = String.class
        ),
        @ApiResponse(
            code = 404,
            message = "Not Found",
            response = String.class
        ),
        @ApiResponse(
            code = 500,
            message = "Internal Server Error",
            response = String.class
        )
    })
    @GetMapping(
        path = "/{id}/roles",
        consumes = APPLICATION_JSON_UTF8_VALUE,
        produces = APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public ResponseEntity<GetUserProfileWithRolesResponse> getUserProfileById(@PathVariable String id) {
        log.info("Getting user profile with id: {}", id);

        requireNonNull(id, "id cannot be null");

        return ResponseEntity.ok(
            userProfileService.retrieve(
                new UserProfileIdentifier(UUID, id)
            )
        );
    }

    @ApiOperation("Retrieves user profile queried by email")
    @ApiParam(name = "email", required = true)

    @ApiResponses({
        @ApiResponse(
            code = 200,
            message = "Representation of a user profile data",
            response = String.class
        ),
        @ApiResponse(
            code = 400,
            message = "Bad Request",
            response = String.class
        ),
        @ApiResponse(
            code = 404,
            message = "Not Found",
            response = String.class
        ),
        @ApiResponse(
            code = 500,
            message = "Internal Server Error",
            response = String.class
        )
    })
    @GetMapping(
            path = "/roles",
            params = "email",
            consumes = APPLICATION_JSON_UTF8_VALUE,
            produces = APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public ResponseEntity<GetUserProfileWithRolesResponse> getUserProfileByEmail(@RequestParam String email) {
        log.info("Getting user profile with email: {}", email);

        requireNonNull(email, "email cannot be null");

        return ResponseEntity.ok(
            userProfileService.retrieve(
                new UserProfileIdentifier(EMAIL, email)
            )
        );
    }
}
