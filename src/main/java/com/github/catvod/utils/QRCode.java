package com.github.catvod.utils;

import cn.hutool.core.codec.Base64;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayInputStream;
import java.util.EnumMap;
import java.util.Map;

public class QRCode {

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    public static BufferedImage createBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int[] pixels = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = matrix.get(x, y) ? BLACK : WHITE;
            }
        }
//        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bufferedImage;
    }

    public static BufferedImage getBitmap(String contents, int size, int margin) {
        try {
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, margin);
            return createBufferedImage(new MultiFormatWriter().encode(contents, BarcodeFormat.QR_CODE, Swings.dp2px(size), Swings.dp2px(size), hints));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BufferedImage base64StringToImage(String strBase64) {
        try {
            byte[] arr = Base64.decode(strBase64);
            return ImageIO.read(new ByteArrayInputStream(arr));
        } catch (Exception ex) {
            return null;
        }
    }
}
