package net.n2oapp.security.admin.util;

import net.n2oapp.security.admin.util.modifier.MinLengthModifier;
import net.n2oapp.security.admin.util.modifier.MinNonLetterModifier;
import net.n2oapp.security.admin.util.modifier.MinUpperModifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

import java.util.Random;

import static net.n2oapp.properties.StaticProperties.getProperty;

public class PasswordGenerator {
    public static final Random RANDOM = new Random();

    private PasswordEncoder passwordEncoder;

    public PasswordGenerator(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public static char generateUpper() {
        return (char) (65 + RANDOM.nextInt(26));
    }

    public static void insertAtRandomIndex(StringBuilder stringBuilder, char ch) {
        Assert.isTrue(stringBuilder.length() > 0);
        final int index = RANDOM.nextInt(stringBuilder.length());
        stringBuilder.insert(index, ch);
    }


    public String generate() {
        String result = PasswordUtil.generatePassword();
        Assert.hasLength(result);
        for (PasswordModifier modifier : getModifiers()) {
            result = modifier.modify(result);
        }
        return result;
    }

    public String encode(String password) {
        return passwordEncoder.encode(password);
    }


    private PasswordModifier[] getModifiers() {
        return new PasswordModifier[]{
                new MinUpperModifier(Integer.valueOf(getProperty("sec.password.minUppercase"))),
                new MinNonLetterModifier(Integer.valueOf(getProperty("sec.password.minNonLetter"))),
                new MinLengthModifier(Integer.valueOf((getProperty("sec.password.minLength"))))};
    }


}

