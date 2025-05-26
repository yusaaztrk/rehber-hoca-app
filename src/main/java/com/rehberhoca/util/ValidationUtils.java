package com.rehberhoca.util;

import java.util.regex.Pattern;

public class ValidationUtils {

    // Email regex pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    // Türkiye telefon numarası regex pattern
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^(\\+90|0)?[5][0-9]{9}$"
    );

    // Email validasyonu
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    // Telefon numarası validasyonu
    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return true; // Telefon opsiyonel
        }

        // Boşluk, tire ve parantezleri temizle
        String cleanPhone = phone.replaceAll("[\\s()-]", "");
        return PHONE_PATTERN.matcher(cleanPhone).matches();
    }

    // İsim validasyonu
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        String trimmedName = name.trim();
        return trimmedName.length() >= 2 && trimmedName.length() <= 100;
    }

    // Program adı validasyonu
    public static boolean isValidProgramName(String programName) {
        if (programName == null || programName.trim().isEmpty()) {
            return false;
        }

        String trimmedName = programName.trim();
        return trimmedName.length() >= 3 && trimmedName.length() <= 200;
    }

    // Süre validasyonu (hafta cinsinden)
    public static boolean isValidDuration(Integer duration) {
        if (duration == null) {
            return true; // Opsiyonel
        }
        return duration > 0 && duration <= 104; // Maksimum 2 yıl
    }

    // Pozitif sayı kontrolü
    public static boolean isPositiveNumber(String numberStr) {
        if (numberStr == null || numberStr.trim().isEmpty()) {
            return false;
        }

        try {
            int number = Integer.parseInt(numberStr.trim());
            return number > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Telefon numarasını formatla
    public static String formatPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return phone;
        }

        String cleanPhone = phone.replaceAll("[\\s()-]", "");

        if (cleanPhone.startsWith("0") && cleanPhone.length() == 11) {
            // 05321234567 -> 0532 123 4567
            return cleanPhone.substring(0, 4) + " " +
                   cleanPhone.substring(4, 7) + " " +
                   cleanPhone.substring(7);
        } else if (cleanPhone.startsWith("+90") && cleanPhone.length() == 13) {
            // +905321234567 -> +90 532 123 4567
            return cleanPhone.substring(0, 3) + " " +
                   cleanPhone.substring(3, 6) + " " +
                   cleanPhone.substring(6, 9) + " " +
                   cleanPhone.substring(9);
        }

        return phone; // Formatlanamadıysa olduğu gibi döndür
    }

    // Email formatını temizle
    public static String cleanEmail(String email) {
        if (email == null) {
            return null;
        }
        return email.trim().toLowerCase();
    }

    // İsim formatını düzenle
    public static String formatName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return name;
        }

        // Baş harfleri büyük yap
        String[] words = name.trim().toLowerCase().split("\\s+");
        StringBuilder formatted = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            if (i > 0) {
                formatted.append(" ");
            }

            if (words[i].length() > 0) {
                formatted.append(Character.toUpperCase(words[i].charAt(0)));
                if (words[i].length() > 1) {
                    formatted.append(words[i].substring(1));
                }
            }
        }

        return formatted.toString();
    }

    // Ad soyad ayırma metodu
    public static NameParts splitFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return new NameParts("", "");
        }

        String[] words = fullName.trim().split("\\s+");

        if (words.length == 1) {
            // Sadece bir kelime varsa, ad olarak kabul et
            return new NameParts(words[0], "");
        } else if (words.length == 2) {
            // İki kelime varsa, ilki ad, ikincisi soyad
            return new NameParts(words[0], words[1]);
        } else {
            // İkiden fazla kelime varsa, son kelime soyad, geri kalanı ad
            StringBuilder firstName = new StringBuilder();
            for (int i = 0; i < words.length - 1; i++) {
                if (i > 0) {
                    firstName.append(" ");
                }
                firstName.append(words[i]);
            }
            return new NameParts(firstName.toString(), words[words.length - 1]);
        }
    }

    // Ad soyad parçaları için yardımcı sınıf
    public static class NameParts {
        private final String firstName;
        private final String lastName;

        public NameParts(String firstName, String lastName) {
            this.firstName = firstName != null ? firstName : "";
            this.lastName = lastName != null ? lastName : "";
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getFullName() {
            if (firstName.isEmpty() && lastName.isEmpty()) {
                return "";
            } else if (firstName.isEmpty()) {
                return lastName;
            } else if (lastName.isEmpty()) {
                return firstName;
            } else {
                return firstName + " " + lastName;
            }
        }
    }

    // Metin uzunluğu kontrolü
    public static boolean isValidLength(String text, int minLength, int maxLength) {
        if (text == null) {
            return minLength == 0;
        }

        int length = text.trim().length();
        return length >= minLength && length <= maxLength;
    }

    // Null veya boş string kontrolü
    public static boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    // Null değilse ve boş değilse true döner
    public static boolean isNotEmpty(String text) {
        return !isEmpty(text);
    }

    // Sayısal string kontrolü
    public static boolean isNumeric(String str) {
        if (isEmpty(str)) {
            return false;
        }

        try {
            Integer.parseInt(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Tüm öğrenci verilerini validate et
    public static ValidationResult validateStudent(String name, String email, String phone) {
        ValidationResult result = new ValidationResult();

        if (!isValidName(name)) {
            result.addError("Ad Soyad", "Ad Soyad alanı 2-100 karakter arasında olmalıdır.");
        }

        if (!isValidEmail(email)) {
            result.addError("E-posta", "Geçerli bir e-posta adresi giriniz.");
        }

        if (isNotEmpty(phone) && !isValidPhoneNumber(phone)) {
            result.addError("Telefon", "Geçerli bir telefon numarası giriniz (örn: 0532 123 4567).");
        }

        return result;
    }

    // Tüm program verilerini validate et
    public static ValidationResult validateProgram(String name, String description, Integer duration) {
        ValidationResult result = new ValidationResult();

        if (!isValidProgramName(name)) {
            result.addError("Program Adı", "Program adı 3-200 karakter arasında olmalıdır.");
        }

        if (isNotEmpty(description) && !isValidLength(description, 0, 1000)) {
            result.addError("Açıklama", "Açıklama maksimum 1000 karakter olabilir.");
        }

        if (duration != null && !isValidDuration(duration)) {
            result.addError("Süre", "Süre 1-104 hafta arasında olmalıdır.");
        }

        return result;
    }

    // Validation sonucu için yardımcı sınıf
    public static class ValidationResult {
        private StringBuilder errors = new StringBuilder();
        private boolean valid = true;

        public void addError(String field, String message) {
            if (errors.length() > 0) {
                errors.append("\n");
            }
            errors.append("• ").append(field).append(": ").append(message);
            valid = false;
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorMessages() {
            return errors.toString();
        }
    }
}