package com.smartz.vt.qrcode.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.smartz.vt.qrcode.service.QRCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

/**
 * Created by umesh.pai on 09/22/2018.
 */

@RestController
public class QRCodeController {
	
	@Autowired
	private QRCodeService QRService;

	@RequestMapping(value = "/v1/qrcode", method = RequestMethod.POST, produces = MediaType.IMAGE_PNG_VALUE)
	public byte[] generateQRCode(@RequestBody String request) throws IOException
	{	
		JSONObject jsonReq = new JSONObject(request.toString());
		
		String data = jsonReq.getString("data");
		int width = jsonReq.getInt("width");
		int height = jsonReq.getInt("height");
		System.out.println("data=" + data + " width=" + width + " height=" + height);
		
		byte[] pngData = QRService.generateQRCode(data, width, height);
		System.out.println("matrix::" + pngData);
	
		return pngData;
	}
	
	@RequestMapping(value = "/v1/qrcode", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
	public byte[] getQRCode(@RequestParam("data") String data, @RequestParam("width") int width, @RequestParam("height") int height)
	{	
	
		byte[] pngData = QRService.generateQRCode(data, width, height);
		System.out.println("matrix::" + pngData);
		List<String> list = new ArrayList<String>();
		list.add("Accept-Encoding");
		list.add("User-Agent");
		
		final HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.IMAGE_PNG);
	    headers.setVary(list);
	    
	    return pngData;
	}
	
}
