package com.smartz.vt.qrcode.service;

import com.google.zxing.common.BitMatrix;

/**
 * Created by umesh.pai on 01/16/2018.
 */
public interface QRCodeService
{	
	BitMatrix generateQRCode(String data, int width, int height);
}
