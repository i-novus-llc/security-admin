package net.n2oapp.security.admin.util;

import java.util.Random;

/**
 * Утилитный класс для генерации пароля
 */
public class PasswordUtil {
    static public String generatePassword() {
        char[] characters = new char[8];
        Random rnd = new Random();

        for (int i = 0; i < characters.length; i++) {
            if (rnd.nextInt(3) > 1) {
                characters[i] = generateDigit(rnd);
            } else {
                characters[i] = generateCharacter(rnd);
            }
        }
        return new String(characters);
    }

    static private char generateDigit(Random rnd) {
        return String.valueOf(rnd.nextInt(10)).charAt(0);
    }

    static private char generateCharacter(Random rnd) {
        Character chr = (char) (97 + rnd.nextInt(26));
        if (rnd.nextInt(100) % 2 == 0) {
            chr = chr.toString().toUpperCase().charAt(0);
        }
        return chr;
    }
}
