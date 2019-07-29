package uk.gov.hmcts.reform.userprofileapi.util;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.userprofileapi.domain.IdamRolesInfo;
import uk.gov.hmcts.reform.userprofileapi.service.IdamStatus;

@SuppressWarnings("HideUtilityClassConstructor")
public final class IdamStatusResolver {

    public static final String OK = "11 OK";
    public static final String ACCEPTED = "12 User Registration accepted";
    public static final String INVALID_REQUEST = "13 Required parameters or one of request field is missing or invalid";
    public static final String MISSING_TOKEN = "14 Missing Bearer Token";
    public static final String TOKEN_EXPIRED = "15 Bearer token is expired, or it doesn’t have the ‘create-user’ scope";
    public static final String NOT_FOUND = "16 Resource not found";
    public static final String USER_EXISTS = "17 User with this email already exists";
    public static final String UNKNOWN = "18 Unknown error from Idam";

    public static final String ACTIVE = "ACTIVE";
    public static final String PENDING = "PENDING";
    public static final String LOCKED = "LOCKED";


    public static String resolveStatusAndReturnMessage(HttpStatus httpStatus) {
        switch (httpStatus) {
            case OK: return OK;
            case CREATED: return ACCEPTED;
            case BAD_REQUEST: return INVALID_REQUEST;
            case UNAUTHORIZED: return MISSING_TOKEN;
            case FORBIDDEN: return TOKEN_EXPIRED;
            case NOT_FOUND: return NOT_FOUND;
            case CONFLICT: return USER_EXISTS;
            default:
                return UNKNOWN;
        }
    }

    public static IdamStatus resolveIdamStatus(Map<Map<String, Boolean>, IdamStatus> statusResolver, IdamRolesInfo idamRolesInfo) {

        Map<String, Boolean> statusMap = new HashMap<String, Boolean>();
        statusMap.put(ACTIVE, idamRolesInfo.getActive());
        statusMap.put(PENDING, idamRolesInfo.getPending());
        statusMap.put(LOCKED, idamRolesInfo.getLocked());

        return statusResolver.get(statusMap) != null ? statusResolver.get(statusMap) : null;
    }
}
