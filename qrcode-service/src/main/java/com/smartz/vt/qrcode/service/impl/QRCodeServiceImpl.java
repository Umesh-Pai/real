package com.smartz.vt.qrcode.service.impl;


import org.springframework.stereotype.Service;

import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.smartz.vt.qrcode.service.QRCodeService;
import com.google.zxing.BarcodeFormat;

/**
 * Created by umesh.pai on 09/19/2018.
 */

@Service
public class QRCodeServiceImpl implements QRCodeService{
	
	public BitMatrix generateQRCode(String data, int width, int height) {
		
		BitMatrix bitMatrix = null;
		
		try {
			
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
		    bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height);
		    System.out.println("Got bitMatrix");
			
		} catch (Exception e) {
			System.out.println("Exception::" + e.getMessage());
			//response.put("Message", "Internal Server Error");
		} 
		return bitMatrix;
	}
	
}
	