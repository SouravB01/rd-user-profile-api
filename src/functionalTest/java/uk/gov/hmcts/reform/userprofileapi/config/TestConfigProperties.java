package uk.gov.hmcts.reform.userprofileapi.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class TestConfigProperties {

    @Value("${test.url}")
    public String baseTestUrl;

    @Value("${generated.user.email.pattern}")
    public String generatedUserEmailPattern;

    @Value("${test.user.password}")
    public String testUserPassword;

    @Value("${idam.api.url}")
    public String idamApiUrl;

}