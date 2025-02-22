package org.example;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.nio.charset.StandardCharsets;
public class Main {
    public static void main(String[] args) {
        String outputPdfPath = "medical_prescription.pdf"; // مسیر خروجی PDF

        // خواندن فایل HTML از resources
        try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("medical_prescription.html")) {
            if (inputStream == null) {
                System.err.println("فایل medical_prescription.html پیدا نشد!");
                return;
            }

            // خواندن محتوای HTML به‌عنوان رشته
            String htmlContent = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));

            // مسیر تصویر
            String imagePath2 = "/jom.png";
            String base64Image;

            try (InputStream imageStream = Main.class.getResourceAsStream(imagePath2)) {
                if (imageStream == null) {
                    throw new IOException("تصویر پیدا نشد: " + imagePath2);
                }
                BufferedImage image = ImageIO.read(imageStream);
                base64Image = convertImageToBase64(image);
            }

            // جایگذاری مقادیر در HTML
            Map<String, String> params = new HashMap<>();
            params.put("imagePath2", "data:image/jpeg;base64," + base64Image);

            for (Map.Entry<String, String> entry : params.entrySet()) {
                htmlContent = htmlContent.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }

            // تنظیمات راست‌چین برای متون فارسی
            ConverterProperties properties = new ConverterProperties();
            properties.setCharset("UTF-8");
            properties.setBaseUri(Main.class.getClassLoader().getResource("").toExternalForm()); // تنظیم مسیر پایه

            // ایجاد فایل PDF
            try (PdfWriter writer = new PdfWriter(outputPdfPath);
                 PdfDocument pdfDoc = new PdfDocument(writer)) {
                HtmlConverter.convertToPdf(htmlContent, pdfDoc, properties);
            }

            System.out.println("✅ PDF با موفقیت تولید شد: " + outputPdfPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String convertImageToBase64(BufferedImage image) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        }
    }
}