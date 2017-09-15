package net.n2oapp.security.admin.util.modifier;

import net.n2oapp.security.admin.util.PasswordGenerator;
import net.n2oapp.security.admin.util.PasswordModifier;

public class MinUpperModifier implements PasswordModifier {
    private int minUppercaseNumber;

    public MinUpperModifier(int minUppercaseNumber) {
        this.minUppercaseNumber = minUppercaseNumber;
    }

    @Override
    public String modify(String password) {
        final int numberToInsert = numberToInsert(password);
        if (numberToInsert <= 0)
            return password;
        StringBuilder sb = new StringBuilder(password);
        for (int i = 0; i < numberToInsert; i++) {
            PasswordGenerator.insertAtRandomIndex(sb, PasswordGenerator.generateUpper());
        }
        return sb.toString();
    }

    private int numberToInsert(String password) {
        int count = 0;
        for (char c : password.toCharArray()) {
            if ('A' <= c && c <= 'Z')
                count++;
        }
        return minUppercaseNumber - count;
    }
}
