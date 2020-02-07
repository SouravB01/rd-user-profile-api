package uk.gov.hmcts.reform.userprofileapi.domain.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.EnumSet;
import org.junit.Test;

public class IdentifierNameTest {

    @Test
    public void should_return_correct_string_values() {
        assertThat(IdentifierName.EMAIL.toString()).isEqualTo("EMAIL");
        assertThat(IdentifierName.UUID.toString()).isEqualTo("UUID");
        assertThat(IdentifierName.UUID_LIST.toString()).isEqualTo("UUID_LIST");
    }

    @Test
    public void should_return_correct_enum_from_string() {
        assertThat(IdentifierName.valueOf("EMAIL")).isEqualTo(IdentifierName.EMAIL);
        assertThat(IdentifierName.valueOf("UUID")).isEqualTo(IdentifierName.UUID);
        assertThat(IdentifierName.valueOf("UUID_LIST")).isEqualTo(IdentifierName.UUID_LIST);
    }

    @Test
    public void checkEnums() {
        assertTrue(returnTrueIfValid(IdentifierName.EMAIL));
        assertTrue(returnTrueIfValid(IdentifierName.UUID));
        assertTrue(returnTrueIfValid(IdentifierName.UUID_LIST));
        assertFalse(returnTrueIfValid(null));
    }

    private boolean returnTrueIfValid(IdentifierName identifierName) {
        return EnumSet.allOf(IdentifierName.class).contains(identifierName);
    }
}
