package net.n2oapp.security.admin.util.modifier;

import net.n2oapp.security.admin.util.PasswordGenerator;
import net.n2oapp.security.admin.util.PasswordModifier;

public class MinNonLetterModifier implements PasswordModifier {
    private int minNotLiteralsNumber;

    public MinNonLetterModifier(int minNotLiteralsNumber) {
        this.minNotLiteralsNumber = minNotLiteralsNumber;
    }

    private static final char[] domain = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '!', '$', '#', '%'};

    @Override
    public String modify(String password) {
        final int numberToInsert = numberToInsert(password);
        if (numberToInsert <= 0)
            return password;
        StringBuilder sb = new StringBuilder(password);
        for (int i = 0; i < numberToInsert; i++) {
            final char ch = domain[PasswordGenerator.RANDOM.nextInt(domain.length)];
            PasswordGenerator.insertAtRandomIndex(sb, ch);
        }
        return sb.toString();
    }

    private int numberToInsert(String password) {
        int count = 0;
        for (char c : password.toCharArray()) {
            for (char domainCh : domain) {
                if (c == domainCh)
                    count++;
            }
        }
        return minNotLiteralsNumber - count;
    }
}
