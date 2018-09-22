package com.smartz.vt.qrcode.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.smartz.vt.qrcode.service.QRCodeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

/**
 * Created by umesh.pai on 09/19/2018.
 */

@RestController
public class QRCodeController {
	
	@Autowired
	private QRCodeService QRService;

	@RequestMapping(value = "/v1/qrcode", method = RequestMethod.POST, produces = MediaType.IMAGE_PNG_VALUE)
	public byte[] getAccessDetails(@RequestBody String request) throws IOException
	{	
		JSONObject jsonReq = new JSONObject(request.toString());
		
		String data = jsonReq.getString("data");
		int width = jsonReq.getInt("width");
		int height = jsonReq.getInt("height");
		System.out.println("data=" + data + " width=" + width + " height=" + height);
		
		BitMatrix matrix = QRService.generateQRCode(data, width, height);
		System.out.println("matrix::" + matrix);
		
		ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();
	
		return pngData;
	}
	
	@RequestMapping(value = "/v2/qrcode", method = RequestMethod.POST, produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<BitMatrix> getAccessDetails1(@RequestBody String request)
	{	
		JSONObject jsonReq = new JSONObject(request.toString());
		
		String data = jsonReq.getString("data");
		int width = jsonReq.getInt("width");
		int height = jsonReq.getInt("height");
		System.out.println("data===" + data + " width===" + width + " height===" + height);
		
		BitMatrix matrix = QRService.generateQRCode(data, width, height);
		System.out.println("matrix::////" + matrix);
		List<String> list = new ArrayList<String>();
		list.add("Accept-Encoding");
		list.add("User-Agent");
		
		final HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.IMAGE_PNG);
	    headers.setVary(list);
	    
	
		//return matrix;
		return new ResponseEntity<BitMatrix>(matrix, headers, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/v3/qrcode", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<BitMatrix> getAccessDetails2()
	{	
		//JSONObject jsonReq = new JSONObject();
		
		//String data = jsonReq.getString("data");
		//int width = jsonReq.getInt("width");
		//int height = jsonReq.getInt("height");
		//System.out.println("data===" + data + " width===" + width + " height===" + height);
		
		BitMatrix matrix = QRService.generateQRCode("test", 4, 4);
		System.out.println("matrix::////" + matrix);
		List<String> list = new ArrayList<String>();
		list.add("Accept-Encoding");
		list.add("User-Agent");
		
		final HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.IMAGE_PNG);
	    headers.setVary(list);
	    
	
		//return matrix;
		return new ResponseEntity<BitMatrix>(matrix, headers, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/v4/qrcode", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<byte[]> getAccessDetails3() throws IOException
	{	
		BitMatrix matrix = QRService.generateQRCode("test", 4, 4);
		
		ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();
		
		System.out.println("matrix::==///" + matrix);
		List<String> list = new ArrayList<String>();
		list.add("Accept-Encoding");
		list.add("User-Agent");
		
		final HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.IMAGE_PNG);
	    headers.setVary(list);
	
		//return matrix;
		return new ResponseEntity<byte[]>(pngData, headers, HttpStatus.OK);
	}
	
	
}
