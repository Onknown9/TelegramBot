package com.example.Bot.service;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
public class TesseractService {
    public String extractTextFromImage(MultipartFile file) throws IOException, TesseractException {
        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage image = ImageIO.read(inputStream);

            ITesseract tessInstance = new Tesseract();
            tessInstance.setLanguage("rus");
            tessInstance.setDatapath("src/main/resources/tesseractData"); // Ensure this path is correct and points to the tessdata directory

            return tessInstance.doOCR(image);
        }
    }

    public String extractText(File file) throws Exception {
        try (InputStream inputStream = new FileInputStream(file)) {
            // Convert File to MultipartFile
            MultipartFile multipartFile = new MockMultipartFile(file.getName(), inputStream);

            // Perform OCR using TesseractService
            TesseractService tesseractService = new TesseractService();
            return tesseractService.extractTextFromImage(multipartFile);
        } catch (IOException e) {
            throw new Exception("Error processing file", e);
        }
    }
}
