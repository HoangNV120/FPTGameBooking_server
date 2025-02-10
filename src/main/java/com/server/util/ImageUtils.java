package com.server.util;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;

public class ImageUtils {
    public static BufferedImage overlayIcon(BufferedImage qrImage, String iconPath) throws Exception {
        BufferedImage icon = ImageIO.read(new File(iconPath));
        int iconWidth = qrImage.getWidth() / 5;
        int iconHeight = qrImage.getHeight() / 5;
        int centerX = (qrImage.getWidth() - iconWidth) / 2;
        int centerY = (qrImage.getHeight() - iconHeight) / 2;

        Graphics2D graphics = qrImage.createGraphics();
        graphics.drawImage(icon.getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH), centerX, centerY, null);
        graphics.dispose();
        return qrImage;
    }

    public static void saveImage(BufferedImage image, String filePath) throws Exception {
        ImageIO.write(image, "PNG", new File(filePath));
    }

    public static BufferedImage extractFirstFrameFromGif(String gifPath) throws Exception {
        File gifFile = new File(gifPath);
        ImageInputStream inputStream = ImageIO.createImageInputStream(gifFile);
        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("gif");

        if (readers.hasNext()) {
            ImageReader reader = readers.next();
            reader.setInput(inputStream);
            return reader.read(0); // Đọc khung đầu tiên
        }
        return null;
    }
}
