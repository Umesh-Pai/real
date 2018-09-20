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

public class GetMetadata implements RequestHandler<APIGatewayProxyRequestEvent, ApiGatewayProxyResponse> {

    public GetMetadata() {}
    
    public ApiGatewayProxyResponse handleRequest(APIGatewayProxyRequestEvent gatewayEvent, Context context) {
    	context.getLogger().log("projectId: " + gatewayEvent.getPathParameters().get("projectId"));
        ApiGatewayProxyResponse response = null;
        String projectId = null;
        String metadata = null;
        JSONObject responseJson = null;
    	
        try {
        	projectId = gatewayEvent.getPathParameters().get("projectId");
        	
        	metadata = getMetadata(projectId);
        	System.out.println("metadata resp:" + metadata);
        	responseJson = new JSONObject(metadata);
        	
        	Map<String, String> headers = new HashMap();
            headers.put("Content-Type", "application/json");
            headers.put("Access-Control-Allow-Origin", "*");
            //headers.put("access-control-allow-origin", "*");
            
        	response = new ApiGatewayProxyResponse(responseJson.getInt("status"), headers, responseJson.getString("data"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
	}

    private String getMetadata(String projectId) {
    	JSONObject metadata = new JSONObject();
    	String tableName = "Projects";
    	String primaryColumn = "ProjectID";
    	JSONObject response = new JSONObject();
    	JSONObject errMesg = new JSONObject();
    	String data = null;
    	int status = 500;
		
		System.out.println("In getMetadata projectId::" + projectId);
		
		try {
			
			if (projectId == null || projectId.equals("")) {
				errMesg.put("errorMesg", "Invalid Request");
				data = errMesg.toString();
				status = 400;
			} 
			else {
				AttributeValue attValue = new AttributeValue();
				attValue.setN(projectId);
				
				HashMap<String,AttributeValue> primaryKey = new HashMap<String,AttributeValue>();
				primaryKey.put(primaryColumn, attValue);
				
				GetItemRequest request = new GetItemRequest().withKey(primaryKey).withTableName(tableName);

				final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();
				Map<String,AttributeValue> item = ddb.getItem(request).getItem();
				
				if (item != null) {
					if (item.containsKey("MetaData")) {
						
						metadata.put("metadata", item.get("MetaData").getS());
						//data = item.get("MetaData").getS();
						//response.put("MetaData", metadata);
						data = metadata.toString();
						System.out.println("MetaData::" + data);
						status = 200;
					}
					else {
						System.out.println("metadata is null");
						errMesg.put("errorMesg", "Metadata doesn't exist for Project Id " + projectId);
						data = errMesg.toString();
						status = 400;
					}
	            }
				else {
					System.out.println("item is null");
					errMesg.put("errorMesg", "Project Id doesn't exist");
					data = errMesg.toString();
					status = 400;
				}
			}
			
		} catch (Exception e) {
			System.out.println("Error");
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