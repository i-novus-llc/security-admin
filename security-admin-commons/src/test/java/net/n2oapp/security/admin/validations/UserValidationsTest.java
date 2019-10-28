package net.n2oapp.security.admin.validations;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.commons.util.UserValidations;
import org.junit.Test;

import java.util.Arrays;

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
            Arrays.stream(snilsArray).forEach(s -> userValidations.checkSnils(s));
        } catch (UserException ex) {

        }
    }
}
