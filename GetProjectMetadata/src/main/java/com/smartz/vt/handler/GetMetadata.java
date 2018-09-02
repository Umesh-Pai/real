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
        //context.getLogger().log("Content: " + gatewayEvent.getHeaders().get("Content-Type"));
        ApiGatewayProxyResponse response = null;
    	
        try {
        	JSONObject body = new JSONObject();
        	
        	getMetadata();
        	
        	Map<String, String> headers = new HashMap();
            headers.put("Content-Type", "application/json");
            
        	response = new ApiGatewayProxyResponse(200, headers, body.toString());
        	
            
        } catch (Exception e) {
            e.printStackTrace();
            //context.getLogger().log(String.format(
              //  "Error getting object %s from bucket %s. Make sure they exist and"));
            //throw e;
        }
        return response;
	}

    private String getMetadata() {
    	String metadata = null;
    	String tableName = "Projects";
		String projectId = "2468";
		
		System.out.println("In getMetadata");
		HashMap<String,AttributeValue> primaryKey = new HashMap<String,AttributeValue>();
		primaryKey.put("ProjectID", new AttributeValue(projectId));
		
		GetItemRequest request = new GetItemRequest().withKey(primaryKey).withTableName(tableName);

		final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();
		
		try {
			Map<String,AttributeValue> item = ddb.getItem(request).getItem();
			System.out.println("size::" + item.size());
			if (item != null) {
                Set<String> keys = item.keySet();
                for (String key : keys) {
                    System.out.format("Key::", key + " Value::" + item.get(key).toString());
                }
            }
			
		} catch (Exception e) {
			e.printStackTrace();
            System.err.println(e.getMessage());
        }
    	
    	return metadata;
    }
}