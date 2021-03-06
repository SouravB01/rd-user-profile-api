package uk.gov.hmcts.reform.userprofileapi.domain;

import static uk.gov.hmcts.reform.userprofileapi.util.IdamStatusResolver.resolveStatusAndReturnMessage;

import java.util.Optional;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class IdamRegistrationInfo {

    private HttpStatus idamRegistrationResponse;
    private String statusMessage;
    private ResponseEntity response;

    public IdamRegistrationInfo(HttpStatus httpStatusResponse, Optional<ResponseEntity> response) {
        if (response.isPresent()) {
            this.response = response.get();
        }
        init(httpStatusResponse);
    }

    public IdamRegistrationInfo(HttpStatus httpStatus) {
        init(httpStatus);
    }


    public boolean isSuccessFromIdam() {
        return idamRegistrationResponse.is2xxSuccessful();
    }

    public boolean isDuplicateUser() {
        return HttpStatus.CONFLICT == idamRegistrationResponse;
    }

    private void init(HttpStatus httpStatus) {
        this.idamRegistrationResponse = httpStatus;
        this.statusMessage = resolveStatusAndReturnMessage(httpStatus);
    }
}
