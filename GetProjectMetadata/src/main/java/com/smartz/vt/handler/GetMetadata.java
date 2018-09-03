package com.smartz.vt.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.smartz.vt.domain.ApiGatewayProxyResponse;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import java.util.Set;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;

public class GetMetadata implements RequestHandler<APIGatewayProxyRequestEvent, ApiGatewayProxyResponse> {

    public GetMetadata() {}
    
    public ApiGatewayProxyResponse handleRequest(APIGatewayProxyRequestEvent gatewayEvent, Context context) {
    	context.getLogger().log("projectId: " + gatewayEvent.getPathParameters().get("projectId"));
        context.getLogger().log("Method: " + gatewayEvent.getHttpMethod());
        context.getLogger().log("Path: " + gatewayEvent.getPath());
        ApiGatewayProxyResponse response = null;
        String projectId = null;
        String metadata = null;
    	
        try {
        	projectId = gatewayEvent.getPathParameters().get("projectId");
        	
        	metadata = getMetadata(projectId);
        	System.out.println("metadata resp:" + metadata);
        	
        	Map<String, String> headers = new HashMap();
            headers.put("Content-Type", "application/json");
            
        	response = new ApiGatewayProxyResponse(200, headers, metadata);
            
        } catch (Exception e) {
            e.printStackTrace();
            //context.getLogger().log(String.format(
              //  "Error getting object %s from bucket %s. Make sure they exist and"));
            //throw e;
        }
        return response;
	}

    private String getMetadata(String projectId) {
    	String metadata = null;
    	String tableName = "Projects";
    	String primaryColumn = "ProjectID";
    	JSONObject response = new JSONObject();
		
		System.out.println("In getMetadata projectId::" + projectId);
		
		AttributeValue attValue = new AttributeValue();
		attValue.setN(projectId);
		
		HashMap<String,AttributeValue> primaryKey = new HashMap<String,AttributeValue>();
		primaryKey.put(primaryColumn, attValue);
		
		GetItemRequest request = new GetItemRequest().withKey(primaryKey).withTableName(tableName);

		final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();
		
		try {
			Map<String,AttributeValue> item = ddb.getItem(request).getItem();
			
			if (item != null) {
				if (item.containsKey("MetaData")) {
					metadata = item.get("MetaData").getS();
					System.out.println("MetaData::" + metadata);
					response.put("MetaData", metadata);
				}
				else {
					System.out.println("metadata is null");
					response.put("Error", "Metadata doesn't exist for Project Id " + projectId);
				}
            }
			else {
				System.out.println("item is null");
				response.put("Error", "Project Id doesn't exist");
			}
			
		} catch (Exception e) {
			System.out.println("Error");
			response.put("Error", "Server Failure");
			e.printStackTrace();
        }
    	
    	return response.toString();
    }
}