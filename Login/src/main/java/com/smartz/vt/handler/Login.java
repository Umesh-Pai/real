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
        context.getLogger().log("Body: " + gatewayEvent.getBody());
        
        ApiGatewayProxyResponse response = null;
		String resp = null;
		String userCredentials = null;
		JSONObject responseJson = null;
		
        try {
        	userCredentials = gatewayEvent.getBody();
        	
        	resp = loginUser(userCredentials);
        	System.out.println("login resp:" + resp);
			responseJson = new JSONObject(resp);
        	
        	Map<String, String> headers = new HashMap();
            headers.put("Content-Type", "application/json");
            
			response = new ApiGatewayProxyResponse(responseJson.getInt("status"), headers, responseJson.getString("data"));
            
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
    	
    	JSONObject errMesg = new JSONObject();
    	String data = null;
    	int status = 500;
		
		System.out.println("In loginUser userCredentials::" + userCredentials);

		try {
			
			if (userCredentials == null || userCredentials.equals("")) {
				errMesg.put("errorMesg", "Invalid Request");
				data = errMesg.toString();
				status = 400;
			} 
			else {
				credentialsJson = new JSONObject(userCredentials);
				System.out.println("credentialsJson::" + credentialsJson);
				userId = credentialsJson.getString("userId");
				password = credentialsJson.getString("password");
				
				AttributeValue attValue = new AttributeValue();
				attValue.setS(userId);
				
				HashMap<String,AttributeValue> primaryKey = new HashMap<String,AttributeValue>();
				primaryKey.put(primaryColumn, attValue);
				
				GetItemRequest request = new GetItemRequest().withKey(primaryKey).withTableName(tableName).withProjectionExpression(projection);
				final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();
				Map<String,AttributeValue> item = ddb.getItem(request).getItem();
				
				if (item != null) {
					System.out.println("item::" + item.toString());
					if (item.containsKey("Password")) {
						passwordinDB = item.get("Password").getS();
						System.out.println("Password::" + passwordinDB);
						
						if (passwordinDB.equals(password)) {
							System.out.println("Success::");
							data = new JSONObject().put("message", "success").toString();
							status = 200;
						} else {
							System.out.println("wrong password");
							//response.put("Error", "Invalid User Id or Password");
							errMesg.put("errorMesg", "Invalid User Id or Password");
							data = errMesg.toString();
							status = 401;
						}
					}
					else {
						System.out.println("password is null");
						errMesg.put("errorMesg", "Invalid User Id or Password");
						data = errMesg.toString();
						status = 401;
					}
	            }
				else {
					System.out.println("item is null");
					errMesg.put("errorMesg", "Invalid User Id or Password");
					data = errMesg.toString();
					status = 401;
				}
			}
			
		} catch (ResourceNotFoundException e) {
			System.out.println("Wrong table name");
			//response.put("Error", "Server Failure");
			errMesg.put("errorMesg", "Invalid User Id or Password");
			data = errMesg.toString();
			status = 401;
        } catch (AmazonServiceException e) {
        	System.out.println("Unexpected Error");
			//response.put("Error", "Server Failure");
        	errMesg.put("errorMesg", "Server Failure");
			data = errMesg.toString();
			status = 500;
        }
    	
		response.put("status", status);
		response.put("data", data);
    	return response.toString();
    }
}