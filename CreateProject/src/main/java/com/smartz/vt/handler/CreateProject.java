package com.smartz.vt.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.smartz.vt.domain.ApiGatewayProxyResponse;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.AmazonServiceException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CreateProject implements RequestHandler<APIGatewayProxyRequestEvent, ApiGatewayProxyResponse> {

    public CreateProject() {}
    
    public ApiGatewayProxyResponse handleRequest(APIGatewayProxyRequestEvent gatewayEvent, Context context) {
        context.getLogger().log("Method: " + gatewayEvent.getHttpMethod());
        context.getLogger().log("Body: " + gatewayEvent.getBody());
        
        ApiGatewayProxyResponse response = null;
		String resp = null;
		String projectDetails = null;
		JSONObject responseJson = null;
		
        try {
        	projectDetails = gatewayEvent.getBody();
        	
        	resp = createProject(projectDetails);
        	System.out.println("resp:" + resp);
			responseJson = new JSONObject(resp);
        	
        	Map<String, String> headers = new HashMap();
            headers.put("Content-Type", "application/json");
            
			response = new ApiGatewayProxyResponse(responseJson.getInt("status"), headers, responseJson.getString("data"));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
	}

    private String createProject(String projectDetails) {
    	
    	JSONObject response = new JSONObject();
    	JSONObject projectJson = null;
    	String tableName = "Projects";
    	
    	JSONObject errMesg = new JSONObject();
    	String data = null;
    	int status = 500;
		
		try {
			projectJson = new JSONObject(projectDetails);
			System.out.println("projectJson::" + projectJson);
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			dateFormat.setTimeZone(TimeZone.getTimeZone("Australia/Sydney"));
			String createdOn =  dateFormat.format(new Date());
			System.out.println("createdOn::" + createdOn);
			
			AttributeValue attValue = new AttributeValue();
			attValue.setN(String.valueOf(projectJson.getInt("projectId")));
			
			HashMap<String,AttributeValue> itemValues = new HashMap<String,AttributeValue>();
			itemValues.put("ProjectID", attValue);
			itemValues.put("MetaData", new AttributeValue(projectJson.getString("metaData")));
			itemValues.put("ProjectName", new AttributeValue(projectJson.getString("projectName")));
			itemValues.put("UserID", new AttributeValue(projectJson.getString("userId")));
			itemValues.put("CreatedOn", new AttributeValue(createdOn));
						
			final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();
			
			PutItemResult putItemResult = ddb.putItem(tableName, itemValues);
			System.out.println("putItemResult::" + putItemResult.toString());

			data = putItemResult.toString();
			status = 200;
			
		} catch (ResourceNotFoundException e) {
			System.out.println("Wrong table name");
			e.printStackTrace();
			errMesg.put("errorMesg", "Server Failure");
			data = errMesg.toString();
			status = 500;
			
        } catch (AmazonServiceException e) {
        	System.out.println("Unexpected Error");
        	e.printStackTrace();
			errMesg.put("errorMesg", "Server Failure");
			data = errMesg.toString();
			status = 500;
        }
    	
		response.put("status", status);
		response.put("data", data);
    	return response.toString();
    }
}