package util;

public class BMICalculatorUtil {

    // Menggunakan generic bounded Number agar bisa menerima Integer, Double, dll
    public static <T extends Number> double calculateBMI(T weightKg, T heightCm) {
        double heightM = heightCm.doubleValue() / 100.0;
        return weightKg.doubleValue() / (heightM * heightM);
    }

    public static String getStatus(double bmi) {
        if (bmi < 18.5) {
            return "Kurus";
        } else if (bmi < 25.0) {
            return "Normal";
        } else if (bmi < 30.0) {
            return "Gemuk";
        } else {
            return "Obesitas";
        }
    }

    public static String getCategoryLabel(double bmi) {
        if (bmi < 18.5) {
            return "Berat Badan Kurang";
        } else if (bmi < 25.0) {
            return "Berat Badan Normal";
        } else if (bmi < 30.0) {
            return "Kelebihan Berat Badan";
        } else {
            return "Obesitas";
        }
    }

    public static String getAdvice(double bmi) {
        if (bmi < 18.5) {
            return "Makan lebih banyak makanan bergizi dan tingkatkan asupan karbo.";
        } else if (bmi < 25.0) {
            return "Pertahankan pola makan dan gaya hidup sehat.";
        } else if (bmi < 30.0) {
            return "Kurangi makanan tinggi lemak dan tingkatkan aktivitas fisik.";
        } else {
            return "Segera konsultasikan dengan dokter atau ahli gizi.";
        }
    }
}
