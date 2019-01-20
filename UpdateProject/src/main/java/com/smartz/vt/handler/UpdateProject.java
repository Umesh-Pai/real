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
import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;
import com.amazonaws.AmazonServiceException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class UpdateProject implements RequestHandler<APIGatewayProxyRequestEvent, ApiGatewayProxyResponse> {

    public UpdateProject() {}
    
    public ApiGatewayProxyResponse handleRequest(APIGatewayProxyRequestEvent gatewayEvent, Context context) {
        context.getLogger().log("Body: " + gatewayEvent.getBody());
        
        ApiGatewayProxyResponse response = null;
		String resp = null;
		String projectId = null;
		String projectDetails = null;
		JSONObject responseJson = null;
		
        try {
        	projectId = gatewayEvent.getPathParameters().get("projectId");
        	projectDetails = gatewayEvent.getBody();
        	
        	resp = updateProject(projectId, projectDetails);
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

    private String updateProject(String projectId, String projectDetails) {
    	
    	JSONObject response = new JSONObject();
    	JSONObject projectJson = null;
    	String tableName = "Projects";
    	
    	JSONObject errMesg = new JSONObject();
    	String data = null;
    	int status = 500;
    	JSONObject jsonObj = new JSONObject();
		
		try {
			
			if (projectId == null || projectId.equals("") || projectDetails == null || projectDetails.equals("")) {
				errMesg.put("errorMesg", "Invalid Request");
				data = errMesg.toString();
				status = 400;
			} 
			else {
				projectJson = new JSONObject(projectDetails);
				System.out.println("projectJson::" + projectJson);
				System.out.println("projectId::" + projectId);
				
				
				
				
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				dateFormat.setTimeZone(TimeZone.getTimeZone("Australia/Sydney"));
				String updatedOn =  dateFormat.format(new Date());
				
				
				HashMap<String,AttributeValue> itemKey = new HashMap<String,AttributeValue>();
				itemKey.put("ProjectID", new AttributeValue(projectId));
				
				HashMap<String,AttributeValueUpdate> updatedValues = new HashMap<String,AttributeValueUpdate>();
				
				if( projectJson.has("status") ) {
					updatedValues.put("Status", new AttributeValueUpdate(new AttributeValue(projectJson.getString("status")), AttributeAction.PUT));
				}
				if( projectJson.has("metaData") ) {
					updatedValues.put("MetaData", new AttributeValueUpdate(new AttributeValue(projectJson.getString("metaData")), AttributeAction.PUT));
				}
				if( projectJson.has("projectName") ) {
					updatedValues.put("ProjectName", new AttributeValueUpdate(new AttributeValue(projectJson.getString("projectName")), AttributeAction.PUT));
				}
				
				updatedValues.put("UpdatedOn", new AttributeValueUpdate(new AttributeValue(updatedOn), AttributeAction.PUT));
							
				final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();
				
				ddb.updateItem(tableName, itemKey, updatedValues);

				jsonObj.put("StatusMesg", "Project " + projectId + " updated successfully");
				data = jsonObj.toString();
				status = 200;
			}
			
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