package com.smartz.vt.qrcode.service;

/**
 * Created by umesh.pai on 09/22/2018.
 */
public interface QRCodeService
{	
	byte[] generateQRCode(String data, int width, int height);
}
