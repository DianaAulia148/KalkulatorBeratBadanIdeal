package util;

import java.util.Locale;

/**
 * Utility class untuk menyimpan session user yang sedang login dan preferensi bahasa.
 */
public class Session {
    // Menyimpan username user yang sedang login
    private static String currentUsername;

    // Menyimpan locale bahasa yang dipilih user (default: Indonesia)
    private static Locale currentLocale = new Locale("id", "ID");

    /**
     * Set username dari user yang login.
     * @param username Username yang sedang login
     */
    public static void setCurrentUsername(String username) {
        if (username != null && !username.trim().isEmpty()) {
            currentUsername = username;
        }
    }

    /**
     * Ambil username dari user yang sedang login.
     * @return Username atau null jika belum login
     */
    public static String getCurrentUsername() {
        return currentUsername;
    }

    /**
     * Hapus session saat logout.
     */
    public static void clear() {
        currentUsername = null;
        currentLocale = new Locale("id", "ID"); // Reset ke default Indonesia
    }

    /**
     * Cek apakah user sudah login.
     * @return true jika sudah login
     */
    public static boolean isLoggedIn() {
        return currentUsername != null;
    }

    /**
     * Set locale bahasa yang dipilih user.
     * @param locale Locale yang dipilih
     */
    public static void setCurrentLocale(Locale locale) {
        if (locale != null) {
            currentLocale = locale;
        }
    }

    /**
     * Ambil locale bahasa saat ini.
     * @return Locale yang digunakan
     */
    public static Locale getCurrentLocale() {
        return currentLocale;
    }
}
