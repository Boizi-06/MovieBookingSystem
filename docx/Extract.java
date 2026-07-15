package docx;

import java.io.*;
import java.util.zip.*;
import java.util.regex.*;

public class Extract {
    public static void main(String[] args) {
        String docxPath = "docx/SRS.docx";
        String txtPath = "docx/SRS.txt";
        try (ZipFile zipFile = new ZipFile(docxPath)) {
            ZipEntry entry = zipFile.getEntry("word/document.xml");
            if (entry == null) {
                System.err.println("word/document.xml not found in zip");
                return;
            }
            try (InputStream is = zipFile.getInputStream(entry);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(txtPath), "UTF-8"))) {
                
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                
                String xml = sb.toString();
                // Regex to find paragraph blocks <w:p>...</w:p>
                Pattern pPattern = Pattern.compile("<w:p\\b[^>]*>(.*?)</w:p>");
                Pattern tPattern = Pattern.compile("<w:t\\b[^>]*>(.*?)</w:t>");
                
                Matcher pMatcher = pPattern.matcher(xml);
                while (pMatcher.find()) {
                    String pContent = pMatcher.group(1);
                    Matcher tMatcher = tPattern.matcher(pContent);
                    StringBuilder pText = new StringBuilder();
                    while (tMatcher.find()) {
                        pText.append(tMatcher.group(1));
                    }
                    if (pText.length() > 0) {
                        // XML entity decoding for basic characters
                        String text = pText.toString()
                                .replace("&amp;", "&")
                                .replace("&lt;", "<")
                                .replace("&gt;", ">")
                                .replace("&quot;", "\"")
                                .replace("&apos;", "'");
                        writer.write(text);
                    }
                    writer.newLine();
                }
                System.out.println("Success: Extracted docx to text!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
