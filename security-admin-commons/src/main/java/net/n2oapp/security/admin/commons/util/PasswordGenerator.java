package net.n2oapp.security.admin.commons.util;

import org.springframework.beans.factory.annotation.Value;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Генератор временных паролей
 */
public class PasswordGenerator {
    private static final String LOWERCASE_CHARACTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGIT_CHARACTERS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "@%+\\/'!#$^?:,(){}[]~-_.";
    private static final String ALL_CHARACTERS = LOWERCASE_CHARACTERS + UPPERCASE_CHARACTERS + DIGIT_CHARACTERS + SPECIAL_CHARACTERS;

    private Random random = new SecureRandom();

    @Value("${access.password.length}")
    private int length;

    @Value("${access.password.numbers-required}")
    private boolean numbersRequired;

    @Value("${access.password.lower-case-required}")
    private boolean lowerCaseRequired;

    @Value("${access.password.upper-case-required}")
    private boolean upperCaseRequired;

    @Value("${access.password.special-symbols-required}")
    private boolean specialSymbolsRequired;


    /**
     * Генерация пароля
     * @return Пароль, сгенерированный согласно установленной политике паролей
     */
    public String generate() {
        ArrayList<Character> password = new ArrayList<>(length);

        if (numbersRequired) {
            password.add(DIGIT_CHARACTERS.charAt(random.nextInt(DIGIT_CHARACTERS.length())));
        }
        if (lowerCaseRequired) {
            password.add(LOWERCASE_CHARACTERS.charAt(random.nextInt(LOWERCASE_CHARACTERS.length())));
        }
        if (upperCaseRequired) {
            password.add(UPPERCASE_CHARACTERS.charAt(random.nextInt(UPPERCASE_CHARACTERS.length())));
        }
        if (specialSymbolsRequired) {
            password.add(SPECIAL_CHARACTERS.charAt(random.nextInt(SPECIAL_CHARACTERS.length())));
        }
        for (int i = 0, count = length - password.size(); i < count; i++) {
            password.add(ALL_CHARACTERS.charAt(random.nextInt(ALL_CHARACTERS.length())));
        }

        Collections.shuffle(password, random);
        return password.stream().map(String::valueOf).collect(Collectors.joining());
    }
}

