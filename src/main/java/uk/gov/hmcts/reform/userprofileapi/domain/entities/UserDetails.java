package uk.gov.hmcts.reform.userprofileapi.domain.entities;

import java.util.List;

public interface UserDetails {

    String getAccessToken();

    String getId();

    List<String> getRoles();

    String getEmailAddress();

    String getForename();

    String getSurname();
}
