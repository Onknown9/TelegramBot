package com.example.Bot.service;
import net.sourceforge.tess4j.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class TesseractPDFService implements ITesseract {
    public String extractText(File file) throws Exception {
        try (InputStream inputStream = new FileInputStream(file)) {
            // Convert File to MultipartFile
            MultipartFile multipartFile = new MockMultipartFile(file.getName(), inputStream);

            try (PDDocument document = PDDocument.load(multipartFile.getInputStream())) {
                PDFTextStripper textStripper = new PDFTextStripper();
                String pdfText = textStripper.getText(document);

                // Extract images and perform OCR
                List<String> imagesWithText = extractImagesAndPerformOCR(document);

                // Combine the extracted text from both text and images
                StringBuilder combinedText = new StringBuilder(pdfText);
                for (String imageText : imagesWithText) {
                    combinedText.append(imageText);
                }

                return combinedText.toString();
            }
        } catch (IOException e) {
            return "Неверный тип файла";
        }
    }

    private List<String> extractImagesAndPerformOCR(PDDocument document) throws IOException {
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        List<String> imagesWithText = new ArrayList<>();

        for (int pageIndex = 0; pageIndex < document.getNumberOfPages(); pageIndex++) {
            BufferedImage image = pdfRenderer.renderImageWithDPI(pageIndex, 300);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", byteArrayOutputStream);

            ITesseract tessInstance = new TesseractPDFService();
            tessInstance.setDatapath("src/main/resources/tessdata"); // Ensure this path is correct and points to the tessdata directory

            String imageText;
            try {
                imageText = tessInstance.doOCR(ImageIO.read(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())));
            } catch (TesseractException e) {
                throw new IOException("Error performing OCR on image", e);
            }

            imagesWithText.add(imageText);
        }

        return imagesWithText;
    }

    @Override
    public String doOCR(File file) throws TesseractException {
        return null;
    }

    @Override
    public String doOCR(File file, Rectangle rectangle) throws TesseractException {
        return null;
    }

    @Override
    public String doOCR(File file, List<Rectangle> list) throws TesseractException {
        return null;
    }

    @Override
    public String doOCR(BufferedImage bufferedImage) throws TesseractException {
        return null;
    }

    @Override
    public String doOCR(BufferedImage bufferedImage, Rectangle rectangle) throws TesseractException {
        return null;
    }

    @Override
    public String doOCR(BufferedImage bufferedImage, String s, List<Rectangle> list) throws TesseractException {
        return null;
    }

    @Override
    public String doOCR(List<IIOImage> list, Rectangle rectangle) throws TesseractException {
        return null;
    }

    @Override
    public String doOCR(List<IIOImage> list, String s, Rectangle rectangle) throws TesseractException {
        return null;
    }

    @Override
    public String doOCR(List<IIOImage> list, String s, List<List<Rectangle>> list1) throws TesseractException {
        return null;
    }

    @Override
    public String doOCR(int i, int i1, ByteBuffer byteBuffer, Rectangle rectangle, int i2) throws TesseractException {
        return null;
    }

    @Override
    public String doOCR(int i, int i1, ByteBuffer byteBuffer, String s, Rectangle rectangle, int i2) throws TesseractException {
        return null;
    }

    @Override
    public String doOCR(int i, int i1, ByteBuffer byteBuffer, int i2, String s, List<Rectangle> list) throws TesseractException {
        return null;
    }

    @Override
    public void setDatapath(String s) {

    }

    @Override
    public void setLanguage(String s) {

    }

    @Override
    public void setOcrEngineMode(int i) {

    }

    @Override
    public void setPageSegMode(int i) {

    }

    @Override
    public void setTessVariable(String s, String s1) {

    }

    @Override
    public void setVariable(String s, String s1) {

    }

    @Override
    public void setConfigs(List<String> list) {

    }

    @Override
    public void createDocuments(String s, String s1, List<RenderedFormat> list) throws TesseractException {

    }

    @Override
    public void createDocuments(String[] strings, String[] strings1, List<RenderedFormat> list) throws TesseractException {

    }

    @Override
    public OCRResult createDocumentsWithResults(BufferedImage bufferedImage, String s, String s1, List<RenderedFormat> list, int i) throws TesseractException {
        return null;
    }

    @Override
    public List<OCRResult> createDocumentsWithResults(BufferedImage[] bufferedImages, String[] strings, String[] strings1, List<RenderedFormat> list, int i) throws TesseractException {
        return null;
    }

    @Override
    public OCRResult createDocumentsWithResults(String s, String s1, List<RenderedFormat> list, int i) throws TesseractException {
        return null;
    }

    @Override
    public List<OCRResult> createDocumentsWithResults(String[] strings, String[] strings1, List<RenderedFormat> list, int i) throws TesseractException {
        return null;
    }

    @Override
    public List<Rectangle> getSegmentedRegions(BufferedImage bufferedImage, int i) throws TesseractException {
        return null;
    }

    @Override
    public List<Word> getWords(BufferedImage bufferedImage, int i) {
        return null;
    }

    @Override
    public List<Word> getWords(List<BufferedImage> list, int i) {
        return null;
    }
}
