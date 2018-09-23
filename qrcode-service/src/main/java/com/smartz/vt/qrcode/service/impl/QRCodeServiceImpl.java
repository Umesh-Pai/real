package com.smartz.vt.qrcode.service.impl;


import java.io.ByteArrayOutputStream;
import org.springframework.stereotype.Service;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.smartz.vt.qrcode.service.QRCodeService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;

/**
 * Created by umesh.pai on 09/22/2018.
 */

@Service
public class QRCodeServiceImpl implements QRCodeService{
	
	public byte[] generateQRCode(String data, int width, int height) {
		
		BitMatrix bitMatrix = null;
		byte[] pngData = null;
		try {
			
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
		    bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height);
		    
		    ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
	        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
	        pngData = pngOutputStream.toByteArray();
		    
		    System.out.println("Got bitMatrix");
			
		} catch (Exception e) {
			System.out.println("Exception::" + e.getMessage());
		} 
		return pngData;
	}
	
}
	