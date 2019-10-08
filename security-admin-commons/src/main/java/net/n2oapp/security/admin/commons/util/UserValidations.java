package net.n2oapp.security.admin.commons.util;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Валидации пользователя
 */
@Component
@PropertySource("classpath:validation.properties")
public class UserValidations {
    @Value("${sec.validation.username:true}")
    private Boolean validationUsername;

    @Value("${sec.validation.password.length}")
    private Integer validationPasswordLength;

    @Value("${sec.validation.password.upper.case.letters}")
    private Boolean validationPasswordUpperCaseLetters;

    @Value("${sec.validation.password.lower.case.letters}")
    private Boolean validationPasswordLowerCaseLetters;

    @Value("${sec.validation.password.numbers}")
    private Boolean validationPasswordNumbers;

    @Value("${sec.validation.password.special.symbols}")
    private Boolean validationPasswordSpecialSymbols;

    /**
     * Валидация на уникальность имени пользователя
     */
    public void checkUsernameUniq(Integer id, User foundUser) {
        Boolean result = id == null ? foundUser == null : ((foundUser == null) || (foundUser.getId().equals(id)));
        if (!result)
            throw new UserException("exception.uniqueUsername");
    }

    /**
     * Валидация на ввод имени пользователя согласно формату
     */
    public void checkUsername(String username) {
        if (!validationUsername)
            return;
        String regexp = "^[a-zA-Z][a-zA-Z0-9]+$";
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(username);
        if (!matcher.matches())
            throw new UserException("exception.wrongUsername");
    }

    /**
     * Валидация на ввод email согласно формату
     */
    public void checkEmail(String email) {
        String regexp = "[A-Za-z0-9!#$%&\'*+/=?^_`{|}~-]+(?:\\.[A-Za-z0-9!#$%&\'*+/=?^_`{|}~-]+)*@(?:[A-Za-z0-9]" +
                "(?:[A-Za-z0-9-]*[A-Za-z0-9])?\\.)+[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?";
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches())
            throw new UserException("exception.wrongEmail");
    }

    /**
     * Валидация на ввод пароля согласно формату
     */
    public void checkPassword(String password, String passwordCheck, Integer id) {
        if (password.length() < validationPasswordLength)
            throw new UserException("exception.passwordLength");
        String regexp;
        Pattern pattern;
        Matcher matcher;
        if (validationPasswordUpperCaseLetters) {
            regexp = "^(?=.*[A-Z])(?=\\S+$).*$";
            pattern = Pattern.compile(regexp);
            matcher = pattern.matcher(password);
            if (!matcher.matches())
                throw new UserException("exception.uppercaseLetters");
        }
        if (validationPasswordLowerCaseLetters) {
            regexp = "^(?=.*[a-z])(?=\\S+$).*$";
            pattern = Pattern.compile(regexp);
            matcher = pattern.matcher(password);
            if (!matcher.matches())
                throw new UserException("exception.lowercaseLetters");
        }
        if (validationPasswordNumbers) {
            regexp = "^(?=.*[0-9])(?=\\S+$).*$";
            pattern = Pattern.compile(regexp);
            matcher = pattern.matcher(password);
            if (!matcher.matches())
                throw new UserException("exception.numbers");
        }
        if (validationPasswordSpecialSymbols) {
            regexp = "(?=.*[@#$%^&+=])(?=\\S+$).*$";
            pattern = Pattern.compile(regexp);
            matcher = pattern.matcher(password);
            if (!matcher.matches())
                throw new UserException("exception.specialSymbols");
        }
        if (((id == null) || (passwordCheck != null)) && (!password.equals(passwordCheck))) {
            throw new UserException("exception.passwordsMatch");
        }
    }

    public void checkSnils(String snils) {
        if (snils.length() != 14)
            throw new UserException("exception.incorrectSnilsFormat");

        if (snils.charAt(3) != '-' || snils.charAt(7) != '-' || snils.charAt(11) != ' ')
            throw new UserException("exception.incorrectSnilsFormat");

        String snilsNoCheckSum = snils.substring(0, 3) + snils.substring(4, 7) + snils.substring(8, 11);
        // 001-001-998
        if (Integer.parseInt(snilsNoCheckSum) > 1001998) {
            int sum = 0;
            int multiplier = 9;
            for (int i = 0; i < 9; i++) {
                sum = sum + (multiplier - i) * Integer.parseInt(snilsNoCheckSum.substring(i, i + 1));
            }

            int checksum = Integer.parseInt(snils.substring(12, 14));
            if (sum > 101)
                sum = sum % 101;
            if (sum < 100) {
                if (!(sum == checksum))
                    throw new UserException("exception.incorrectSnilsFormat");
            }
            if (sum == 100 || sum == 101) {
                if (!(0 == checksum))
                    throw new UserException("exception.incorrectSnilsFormat");
            }
        }
    }
}
