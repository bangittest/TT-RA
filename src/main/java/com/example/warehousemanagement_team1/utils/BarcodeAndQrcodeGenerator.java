package com.example.warehousemanagement_team1.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Component
public class BarcodeAndQrcodeGenerator {
    @Value("${barcode-path}")
    private String barcodePath;
    @Value("${qrcode-path}")
    private String qrcodePath;

    public String generateCode128BarcodeImage(String barcodeText) throws IOException {
        Code128Writer barcodeWriter = new Code128Writer();
        BitMatrix bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.CODE_128, 70, 40);

        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
        return saveBufferedImageAsFile(trimWhitespace(image), barcodePath);
    }

    public String generateQRCodeImage(String qrCodeText) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, 148, 148);

        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
        return saveBufferedImageAsFile(trimWhitespace(image), qrcodePath);
    }

    public String saveBufferedImageAsFile(BufferedImage image, String filePath) throws IOException {
        File outputFile = new File(filePath);
        ImageIO.write(image, "png", outputFile);
        return filePath;
    }

    private BufferedImage trimWhitespace(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int minX = width;
        int minY = height;
        int maxX = -1;
        int maxY = -1;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y);
                if (pixel != -1) { // Kiểm tra xem pixel có màu trắng không
                    if (x < minX) minX = x;
                    if (x > maxX) maxX = x;
                    if (y < minY) minY = y;
                    if (y > maxY) maxY = y;
                }
            }
        }

        return image.getSubimage(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }
}
