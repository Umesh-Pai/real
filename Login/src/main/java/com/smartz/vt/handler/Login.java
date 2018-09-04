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
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.AmazonServiceException;

public class Login implements RequestHandler<APIGatewayProxyRequestEvent, ApiGatewayProxyResponse> {

    public Login() {}
    
    public ApiGatewayProxyResponse handleRequest(APIGatewayProxyRequestEvent gatewayEvent, Context context) {
        context.getLogger().log("Method: " + gatewayEvent.getHttpMethod());
        context.getLogger().log("Body: " + gatewayEvent.getBody());
        
        ApiGatewayProxyResponse response = null;
		String resp = null;
		String userCredentials = null;
		
        try {
        	userCredentials = gatewayEvent.getBody();
        	
        	resp = loginUser(userCredentials);
        	System.out.println("login resp:" + resp);
        	
        	Map<String, String> headers = new HashMap();
            headers.put("Content-Type", "application/json");
            
        	response = new ApiGatewayProxyResponse(200, headers, resp);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
	}

    private String loginUser(String userCredentials) {
    	
    	JSONObject response = new JSONObject();
    	JSONObject credentialsJson = null;
    	String tableName = "Users";
    	String primaryColumn = "UserID";
    	String projection = "Password";
    	String userId = null;
    	String password = null;
    	String passwordinDB = null;
		
		System.out.println("In loginUser userCredentials::" + userCredentials);

		try {
			
			credentialsJson = new JSONObject(userCredentials);
			System.out.println("credentialsJson::" + credentialsJson);
			userId = credentialsJson.getString("userId");
			password = credentialsJson.getString("password");
			
			AttributeValue attValue = new AttributeValue();
			attValue.setN(userId);
			
			HashMap<String,AttributeValue> primaryKey = new HashMap<String,AttributeValue>();
			primaryKey.put(primaryColumn, attValue);
			
			GetItemRequest request = new GetItemRequest().withKey(primaryKey).withTableName(tableName).withProjectionExpression(projection);

			final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();
			
			Map<String,AttributeValue> item = ddb.getItem(request).getItem();
			
			if (item != null) {
				if (item.containsKey("Password")) {
					passwordinDB = item.get("Password").getS();
					System.out.println("Password::" + passwordinDB);
					
					if (passwordinDB.equals(password)) {
						response.put("Status", "Success");
					} else {
						System.out.println("wrong password");
						response.put("Error", "Invalid User Id or Password");
					}
					
				}
				else {
					System.out.println("password is null");
					response.put("Error", "Invalid User Id or Password");
				}
            }
			else {
				System.out.println("item is null");
				response.put("Error", "Invalid User Id or Password");
			}
			
			
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