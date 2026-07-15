package docx;

import java.io.*;
import java.util.regex.*;

public class FindSections {
    public static void main(String[] args) {
        String txtPath = "docx/SRS.txt";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(txtPath), "UTF-8"))) {
            String line;
            int lineNumber = 0;
            System.out.println("--- CÁC TIÊU ĐỀ CHÍNH TRONG TÀI LIỆU SRS ---");
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String trimmed = line.trim();
                // Match lines starting with "PHẦN", or numbers like "3.1", "4.", "Chương", "Bảng"
                if (trimmed.startsWith("PHẦN") || 
                    trimmed.startsWith("Phần") ||
                    Pattern.matches("^[0-9]+\\.[0-9]*\\.?\\s+.*", trimmed) ||
                    trimmed.toLowerCase().contains("cơ sở dữ liệu") ||
                    trimmed.toLowerCase().contains("database") ||
                    trimmed.toLowerCase().contains("erd") ||
                    trimmed.toLowerCase().contains("bảng ")) {
                    
                    if (trimmed.length() < 100) {
                        System.out.printf("Dòng %d: %s%n", lineNumber, trimmed);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
