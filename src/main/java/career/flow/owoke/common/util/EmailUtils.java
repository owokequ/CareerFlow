package career.flow.owoke.common.util;

import java.util.Locale;

public final class EmailUtils {

    private EmailUtils() {
    }

    public static String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
