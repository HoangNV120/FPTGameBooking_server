package com.server.config.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.server.constants.QrConstants;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class QrCodeGenerator {
    public static BufferedImage generateQrCode(String content) throws Exception {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, QrConstants.WIDTH, QrConstants.HEIGHT, hints);

        BufferedImage qrImage = new BufferedImage(QrConstants.WIDTH, QrConstants.HEIGHT, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < QrConstants.WIDTH; x++) {
            for (int y = 0; y < QrConstants.HEIGHT; y++) {
                qrImage.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
            }
        }
        return qrImage;
    }
}
