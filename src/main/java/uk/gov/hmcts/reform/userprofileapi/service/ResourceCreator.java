package uk.gov.hmcts.reform.userprofileapi.service;

import uk.gov.hmcts.reform.userprofileapi.client.RequestData;
import uk.gov.hmcts.reform.userprofileapi.domain.entities.UserProfile;

public interface ResourceCreator<T extends RequestData> {

    UserProfile create(T profileData);

}