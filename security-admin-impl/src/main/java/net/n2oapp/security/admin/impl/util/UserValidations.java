package net.n2oapp.security.admin.impl.util;

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

    @Value("${access.password.length}")
    private Integer validationPasswordLength;

    @Value("${access.password.upper-case-required}")
    private Boolean validationPasswordUpperCaseLetters;

    @Value("${access.password.lower-case-required}")
    private Boolean validationPasswordLowerCaseLetters;

    @Value("${access.password.numbers-required}")
    private Boolean validationPasswordNumbers;

    @Value("${access.password.special-symbols-required}")
    private Boolean validationPasswordSpecialSymbols;

    @Value("${access.user.username.regexp}")
    private String usernameRegexp;

    @Value("${access.user.email.regexp}")
    private String emailRegexp;

    @Value("${access.email-as-username:false}")
    private Boolean emailAsUsername;

    /**
     * Валидация на уникальность имени пользователя
     */
    public void checkUsernameUniq(Integer id, User foundUser) {
        if (Boolean.TRUE.equals(anotherUserExist(id, foundUser)))
            throw new UserException("exception.uniqueUsername");
    }

    /**
     * Валидация на уникальность email пользователя
     */
    public void checkEmailUniq(Integer id, User foundUser) {
        if (Boolean.TRUE.equals(anotherUserExist(id, foundUser)))
            throw new UserException("exception.uniqueEmail");
    }

    /**
     * Валидация на ввод имени пользователя согласно формату
     */
    public void checkUsername(String username) {
        Pattern pattern = Pattern.compile(usernameRegexp);
        Matcher matcher = pattern.matcher(username);
        if (!matcher.matches())
            throw new UserException("exception.wrongUsername");
    }

    /**
     * Валидация на ввод email согласно формату
     */
    public void checkEmail(String email) {
        Pattern pattern = Pattern.compile(emailRegexp);
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
        checkPasswordMatches(true, password, "[0-9a-zA-Z@%\\+\\/'\\!#\\\\$\\^\\?:,\\(\\)\\{\\}\\[\\]~\\-_\\.]+", "exception.wrongSymbols");
        checkPasswordMatches(validationPasswordUpperCaseLetters, password, ".*[A-Z].*", "exception.uppercaseLetters");
        checkPasswordMatches(validationPasswordLowerCaseLetters, password, ".*[a-z].*", "exception.lowercaseLetters");
        checkPasswordMatches(validationPasswordNumbers, password, ".*[0-9].*", "exception.numbers");
        checkPasswordMatches(validationPasswordSpecialSymbols, password, ".*[@%\\+\\/'\\!#\\\\$\\^\\?:,\\(\\)\\{\\}\\[\\]~\\-_\\.].*", "exception.specialSymbols");
        if (((id == null) || (passwordCheck != null)) && (!password.equals(passwordCheck))) {
            throw new UserException("exception.passwordsMatch");
        }
    }

    private void checkPasswordMatches(boolean enabled, String password, String regexp, String errorCode) {
        if (enabled && !Pattern.matches(regexp, password))
            throw new UserException(errorCode);
    }

    public void checkSnils(String snils) {
        if (snils.length() != 14)
            throw new UserException("exception.incorrectSnilsFormat");

        if (snils.charAt(3) != '-' || snils.charAt(7) != '-' || snils.charAt(11) != ' ')
            throw new UserException("exception.incorrectSnilsFormat");

        String snilsNoCheckSum = snils.substring(0, 3) + snils.substring(4, 7) + snils.substring(8, 11);
        // контрольное число проверяется только для номеров больше 001-001-998
        if (snilsNoCheckSum.compareTo("001001998") <= 0)
            return;
        try {
            if (calculateSnilsChecksum(snilsNoCheckSum) != Integer.parseInt(snils.substring(12, 14)))
                throw new UserException("exception.incorrectSnilsFormat");
        } catch (NumberFormatException e) {
            throw new UserException("exception.incorrectSnilsFormat");
        }
    }

    private int calculateSnilsChecksum(String snilsNoCheckSum) {
        int sum = 0;
        int multiplier = 9;
        for (int i = 0; i < 9; i++) {
            sum = sum + (multiplier - i) * Integer.parseInt(snilsNoCheckSum.substring(i, i + 1));
        }
        if (sum > 101)
            sum = sum % 101;
        return sum < 100 ? sum : 0;
    }

    private Boolean anotherUserExist(Integer id, User foundUser) {
        return !(id == null ? foundUser == null : ((foundUser == null) || (foundUser.getId().equals(id))));
    }
}
