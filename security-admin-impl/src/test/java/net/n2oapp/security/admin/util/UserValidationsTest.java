package net.n2oapp.security.admin.util;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.impl.util.UserValidations;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserValidationsTest {

    @Test
    public void snilsValidationTest() {
        UserValidations userValidations = new UserValidations();
        String[] snilsArray = new String[4];
        snilsArray[0] = "112-233-445 95";
        snilsArray[1] = "112-283-455 21";
        snilsArray[2] = "100-283-455 00";
        snilsArray[3] = "001-001-997 69";
        Arrays.stream(snilsArray, 0, snilsArray.length).forEach(userValidations::checkSnils);

        //некорректные
        snilsArray[0] = "201-0i1-997 45";
        snilsArray[1] = "112-233-445 96";
        snilsArray[2] = "112-283-455 22";
        snilsArray[3] = "100-283-455 01";

        try {
            Arrays.stream(snilsArray).forEach(userValidations::checkSnils);
        } catch (UserException ex) {

        }
    }

    @Test
    public void EmailUniqValidationTest() {
        UserValidations userValidations = new UserValidations();
        User user = new User();
        user.setId(1);
        userValidations.checkEmailUniq(1, user);
        Throwable thrown = catchThrowable(() -> userValidations.checkEmailUniq(2, user));
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.uniqueEmail", thrown.getMessage());
    }

    @Test
    public void UserNameUniqValidationTest() {
        UserValidations userValidations = new UserValidations();
        User user = new User();
        user.setId(1);
        userValidations.checkUsernameUniq(1, user);
        Throwable thrown = catchThrowable(() -> userValidations.checkUsernameUniq(2, user));
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.uniqueUsername", thrown.getMessage());
    }
}
