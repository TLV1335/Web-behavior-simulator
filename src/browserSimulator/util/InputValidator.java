package browserSimulator.util;

import java.util.regex.Pattern;

public class InputValidator {
    private static final Pattern URL_PATTERN = Pattern.compile(
        "^(https?://)?[a-zA-Z0-9][a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(/.*)?$"
    );

    private static final Pattern MENU_PATTERN = Pattern.compile(
        "^[a-zA-Z]+(\\s+.*)?$"
    );

    public static boolean isValidUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        return URL_PATTERN.matcher(url.trim()).matches();
    }

    public static String normalizeUrl(String url) {
        if (url == null) return null;
        url = url.trim();

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }

        return url;
    }

    public static boolean isValidMenuInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        return MENU_PATTERN.matcher(input.trim()).matches();
    }

    public static boolean isValidTabIndex(int index, int size) {
        return index >= 0 && index < size;
    }

    public static boolean isValidPositiveInteger(String input) {
        try {
            int value = Integer.parseInt(input);
            return value >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidSearchKeyword(String keyword) {
        return keyword != null && !keyword.trim().isEmpty();
    }
}
