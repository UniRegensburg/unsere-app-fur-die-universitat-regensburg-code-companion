package app;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Creates QR-Code
 */
public class QRCodeGenerator {

    /**
     * Gets PlainText, width and height as input and returns byte Array with imageData.
     * Byte Array can be converted in Image
     * @param text > Data which should be converted
     * @param width > of QRCode Image
     * @param height > of QRCode Image
     * @return
     * @throws WriterException
     * @throws IOException
     */
    public byte[] getQRCodeImage(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();

        return pngData;
    }
}
