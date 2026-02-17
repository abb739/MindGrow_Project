package org.example.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.util.regex.Pattern;

public class ValidationUtils {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    // Checks if a string is null or empty
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    // Validates email format
    public static boolean isValidEmail(String email) {
        return !isEmpty(email) && EMAIL_PATTERN.matcher(email).matches();
    }

    // Validates phone number (must be 8 digits for this context, adjust as needed)
    public static boolean isValidPhone(String phone) {
        return !isEmpty(phone) && phone.matches("\\d{8}");
    }

    // Validates CIN (must be 8 digits)
    public static boolean isValidCIN(String cin) {
        return !isEmpty(cin) && cin.matches("\\d{8}");
    }

    // Validates if a string is a positive integer
    public static boolean isPositiveInteger(String value) {
        if (isEmpty(value))
            return false;
        try {
            int i = Integer.parseInt(value);
            return i > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Displays an error alert
    public static void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
