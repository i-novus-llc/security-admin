package net.n2oapp.security.admin.commons.util;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Проверка корректности работы генератора временных паролей
 */
@RunWith(SpringRunner.class)
public class PasswordGeneratorTest {

    private PasswordGenerator passwordGenerator;

    @Before
    public void init() {
        passwordGenerator = new PasswordGenerator();
        passwordGenerator.setLength(8);
    }

    // Проверка, что длина пароля совпадает с заданной
    @Test
    public void passwordLengthTest() {
        assertEquals(8, passwordGenerator.generate().length());
    }

    // Проверка, что пароль содержит строчные буквы
    @Test
    public void passwordContainsLowerCaseTest() {
        passwordGenerator.setLowerCaseRequired(true);
        assertTrue(passwordGenerator.generate().matches(".*[a-z].*"));
    }

    // Проверка, что пароль содержит прописные буквы
    @Test
    public void passwordContainsUpperCaseTest() {
        passwordGenerator.setUpperCaseRequired(true);
        assertTrue(passwordGenerator.generate().matches(".*[A-Z].*"));
    }

    // Проверка, что пароль содержит цифры
    @Test
    public void passwordContainsNumbersTest() {
        passwordGenerator.setNumbersRequired(true);
        assertTrue(passwordGenerator.generate().matches(".*[0-9].*"));
    }

    // Проверка, что пароль содержит спецсимволы
    @Test
    public void passwordContainsSpecialSymbolsTest() {
        passwordGenerator.setSpecialSymbolsRequired(true);
        assertTrue(passwordGenerator.generate().matches(".*[@%\\+\\/'\\!#\\\\$\\^\\?:,\\(\\)\\{\\}\\[\\]~\\-_\\.].*"));
    }

    // Проверка, что пароль не содержит невалидные символы
    @Test
    public void passwordContainsOnlyValidCharactersTest() {
        passwordGenerator.setLowerCaseRequired(true);
        passwordGenerator.setUpperCaseRequired(true);
        passwordGenerator.setNumbersRequired(true);
        passwordGenerator.setSpecialSymbolsRequired(true);
        assertTrue(passwordGenerator.generate().matches("[0-9a-zA-Z@%\\+\\/'\\!#\\\\$\\^\\?:,\\(\\)\\{\\}\\[\\]~\\-_\\.]+"));
    }
}
