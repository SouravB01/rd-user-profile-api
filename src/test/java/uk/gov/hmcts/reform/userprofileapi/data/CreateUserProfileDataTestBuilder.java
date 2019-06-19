package uk.gov.hmcts.reform.userprofileapi.data;

import org.apache.commons.lang.RandomStringUtils;
import uk.gov.hmcts.reform.userprofileapi.domain.LanguagePreference;
import uk.gov.hmcts.reform.userprofileapi.domain.UserCategory;
import uk.gov.hmcts.reform.userprofileapi.domain.UserType;
import uk.gov.hmcts.reform.userprofileapi.infrastructure.clients.CreateUserProfileData;

import java.util.ArrayList;
import java.util.List;

public class CreateUserProfileDataTestBuilder {

    private CreateUserProfileDataTestBuilder() {
        //not meant to be instantiated.
    }

    public static CreateUserProfileData buildCreateUserProfileData() {
        return new CreateUserProfileData(
            buildRandomEmail(),
            RandomStringUtils.randomAlphabetic(20),
            RandomStringUtils.randomAlphabetic(20),
            LanguagePreference.EN.toString(),
            UserCategory.PROFESSIONAL.toString(),
            UserType.INTERNAL.toString(),
            getIdamRolesJson());
    }

    public static CreateUserProfileData buildCreateUserProfileDataMandatoryFieldsOnly() {
        return new CreateUserProfileData(
            buildRandomEmail(),
            RandomStringUtils.randomAlphabetic(20),
            RandomStringUtils.randomAlphabetic(20),
            LanguagePreference.EN.toString(),
            UserCategory.PROFESSIONAL.toString(),
            UserType.INTERNAL.toString(),
            getIdamRolesJson());
    }

    private static String buildRandomEmail() {
        return RandomStringUtils.randomAlphanumeric(20) + "@somewhere.com";
    }

    public static List<String> getIdamRolesJson() {
        List<String> roles = new ArrayList<String>();
        roles.add("pui-user-manager");
        return roles;
    }

}
