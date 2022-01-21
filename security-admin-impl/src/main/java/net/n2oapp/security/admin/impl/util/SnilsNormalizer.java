package net.n2oapp.security.admin.impl.util;

import java.util.Optional;

import static java.util.Objects.nonNull;

public class SnilsNormalizer {

    private static final String SNILS_FORMAT = "%s-%s-%s %s";

    private SnilsNormalizer() {
        // don't instantiate
    }

    /**
     * Раскладывает снилс по формату ___-___-___ --
     */
    public static Optional<String> structure(final String str) {

        if (nonNull(str)) {
            return Optional.of(String.format(SNILS_FORMAT,
                    str.substring(0, 3),
                    str.substring(3, 6),
                    str.substring(6, 9),
                    str.substring(9)));
        } else

            return Optional.empty();
    }

    public static Optional<String> normalize(final String str) {
        if (nonNull(str)) {
            return structure(
                    str.replace(" ", "")
                            .replace("-", "")
            );
        } else
            return Optional.empty();
    }
}

