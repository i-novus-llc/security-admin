package net.n2oapp.security.admin.util.modifier;

import net.n2oapp.security.admin.util.PasswordModifier;
import net.n2oapp.security.admin.util.PasswordUtil;
import org.springframework.util.Assert;


public class MinLengthModifier implements PasswordModifier {
    private int minLength;

    public MinLengthModifier(int minLength) {
        this.minLength = minLength;
    }

    @Override
    public String modify(String password) {
        StringBuilder result = new StringBuilder(password);
        while (minLength > result.length()) {
            final String additional = PasswordUtil.generatePassword();
            Assert.hasLength(additional);
            final int numberToInsert = minLength - result.length() < additional.length() ? minLength - result
                    .length() : additional.length();
            result.append(additional.substring(0, numberToInsert));
        }
        return result.toString();
    }
}