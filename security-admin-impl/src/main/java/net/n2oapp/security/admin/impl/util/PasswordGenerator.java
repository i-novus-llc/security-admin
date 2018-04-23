package net.n2oapp.security.admin.impl.util;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Генератор паролей
 * https://github.com/spring-projects/spring-security-oauth/blob/master/spring-security-oauth2/src/main/java/org/springframework/security/oauth2/common/util/RandomValueStringGenerator.java
 */
public class PasswordGenerator {
    public static final String LOWERCASE_CHARACTERS = "abcdefghijklmnopqrstuvwxyz";
    public static final String UPPERCASE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String ALPHABETICAL_CHARACTERS = UPPERCASE_CHARACTERS + LOWERCASE_CHARACTERS;
    public static final String DIGIT_CHARACTERS = "1234567890";
    public static final String DEFAULT_CHARACTERS = DIGIT_CHARACTERS + ALPHABETICAL_CHARACTERS;

    private Random random = new SecureRandom();
    private int length = 6;
    private String characters = DEFAULT_CHARACTERS;

    /**
     * Сгенерировать новый пароль
     *
     * @return Пароль
     */
    public String generate() {
        byte[] verifierBytes = new byte[length];
        random.nextBytes(verifierBytes);
        return getAuthorizationCodeString(verifierBytes, characters);
    }

    protected String getAuthorizationCodeString(byte[] verifierBytes, String characters) {
        char[] chars = new char[verifierBytes.length];
        char[] codec = characters.toCharArray();
        for (int i = 0; i < verifierBytes.length; i++) {
            chars[i] = codec[((verifierBytes[i] & 0xFF) % codec.length)];
        }
        return new String(chars);
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public void setCharacters(String characters) {
        this.characters = characters;
    }

    public void setLength(int length) {
        this.length = length;
    }
}

