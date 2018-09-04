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
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.AmazonServiceException;

public class RegisterUser implements RequestHandler<APIGatewayProxyRequestEvent, ApiGatewayProxyResponse> {

    public RegisterUser() {}
    
    public ApiGatewayProxyResponse handleRequest(APIGatewayProxyRequestEvent gatewayEvent, Context context) {
        context.getLogger().log("Method: " + gatewayEvent.getHttpMethod());
        context.getLogger().log("Body: " + gatewayEvent.getBody());
        
        ApiGatewayProxyResponse response = null;
		String resp = null;
		String userDetails = null;
		
        try {
        	userDetails = gatewayEvent.getBody();
        	
        	resp = registerUser(userDetails);
        	System.out.println("register resp:" + resp);
        	
        	Map<String, String> headers = new HashMap();
            headers.put("Content-Type", "application/json");
            
        	response = new ApiGatewayProxyResponse(200, headers, resp);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
	}

    private String registerUser(String userDetails) {
    	
    	JSONObject response = new JSONObject();
    	JSONObject userJson = null;
    	String tableName = "Users";
		
		System.out.println("In registersrUser userDetails::" + userDetails);

		try {
			
			userJson = new JSONObject(userDetails);
			System.out.println("userJson::" + userJson);
			
			HashMap<String,AttributeValue> itemValues = new HashMap<String,AttributeValue>();
			itemValues.put("UserID", new AttributeValue(userJson.getString("userId")));
			itemValues.put("Password", new AttributeValue(userJson.getString("password")));
			itemValues.put("FirstName", new AttributeValue(userJson.getString("firstName")));
			itemValues.put("LastName", new AttributeValue(userJson.getString("lastName")));
			itemValues.put("Organization", new AttributeValue(userJson.getString("organization")));
			itemValues.put("Status", new AttributeValue("Active"));
			
			final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();
			
			ddb.putItem(tableName, itemValues);
			response.put("Status", "success");
			
		} catch (ResourceNotFoundException e) {
			System.out.println("Wrong table name");
			response.put("Error", "Server Failure");
        } catch (AmazonServiceException e) {
        	System.out.println("Unexpected Error");
			response.put("Error", "Server Failure");
        }
    	
    	return response.toString(); 
    }
}