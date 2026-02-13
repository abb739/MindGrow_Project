package org.example.utils;

import java.util.regex.Pattern;

public class InputValidator {

    // RFC 5322 Email Regex (Simplified)
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
    );

    // Tunisian Phone Number (8 digits) or general 8-15 digits
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{8}$");

    // Name: Letters, spaces, hyphens, apostrophes only. No numbers.
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-zÀ-ÖØ-öø-ÿ \\-']+$");

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && NAME_PATTERN.matcher(name).matches();
    }

    public static boolean isStrongPassword(String password) {
        // Min 6 chars
        return password != null && password.length() >= 6;
        // Ideally: 8 chars, mixed case, numbers, etc. But for now matching current requirement.
    }
}
