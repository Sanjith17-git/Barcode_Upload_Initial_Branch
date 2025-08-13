package com.zifo.gsk.barcodegeneration.utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import lombok.experimental.UtilityClass;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;
import net.sourceforge.barbecue.output.OutputException;

@UtilityClass
public class BarcodeGeneratorUtils {

	/**
	 * Generate a Code128 1D barcode and return it as a Base64 PNG string.
	 * 
	 * @throws BarcodeException
	 */
	public static String generate1DBarcodeBase64(String sampleId, String format)
			throws OutputException, IOException, BarcodeException {

		// 1. Build the Barcode object
		Barcode barcode = BarcodeFactory.createCode128B(sampleId);
		barcode.setBarHeight(60);

		// 2. Render to an AWT Image
		Image awtImage = BarcodeImageHandler.getImage(barcode);

		// 3. Convert to BufferedImage
		BufferedImage buffered = new BufferedImage(awtImage.getWidth(null), awtImage.getHeight(null),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = buffered.createGraphics();
		g2d.drawImage(awtImage, 0, 0, null);
		g2d.dispose();

		// 4. Write to ByteArray and Base64-encode
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			ImageIO.write(buffered, format, baos);
			return Base64.getEncoder().encodeToString(baos.toByteArray());
		}
	}

	/**
	 * Generate a QR code and return it as a Base64 PNG string.
	 */
	public static String generateQRCodeBase64(String text, String format) throws WriterException, IOException {

		// 1. Encode the text to a BitMatrix
		BitMatrix matrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, 200, 200);

		// 2. Convert BitMatrix to BufferedImage
		BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(matrix);

		// 3. Write to ByteArray and Base64-encode
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			ImageIO.write(qrImage, format, baos);
			return Base64.getEncoder().encodeToString(baos.toByteArray());
		}
	}
}