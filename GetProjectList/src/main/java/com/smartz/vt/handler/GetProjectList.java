package com.smartz.vt.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.smartz.vt.domain.ApiGatewayProxyResponse;
import com.smartz.vt.domain.Project;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

public class GetProjectList implements RequestHandler<APIGatewayProxyRequestEvent, ApiGatewayProxyResponse> {

    public GetProjectList() {}
    
    public ApiGatewayProxyResponse handleRequest(APIGatewayProxyRequestEvent gatewayEvent, Context context) {
    	context.getLogger().log("userId: " + gatewayEvent.getQueryStringParameters().get("userId"));
    	
        ApiGatewayProxyResponse response = null;
        String userId = null;
        String projectList = null;
        JSONObject responseJson = null;
    	
        try {
        	userId = gatewayEvent.getQueryStringParameters().get("userId");
        	
        	projectList = getProjectList(userId);
        	System.out.println("projectList resp:" + projectList);
        	responseJson = new JSONObject(projectList);
        	
        	Map<String, String> headers = new HashMap();
            headers.put("Content-Type", "application/json");
            
        	response = new ApiGatewayProxyResponse(responseJson.getInt("status"), headers, responseJson.getString("data"));
            
        } catch (Exception e) {
            e.printStackTrace();
            //context.getLogger().log(String.format(
              //  "Error getting object %s from bucket %s. Make sure they exist and"));
            //throw e;
        }
        return response;
	}

    private String getProjectList(String userId) {
    	JSONObject projectJson = null;
    	JSONArray projectList = new JSONArray();
    	JSONObject response = new JSONObject();
    	JSONObject errMesg = new JSONObject();
    	String data = null;
    	int status = 500;

		final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();
		
		try {
			System.out.println("useId==" + userId);
			
			if (userId == null || userId.equals("")) {
				errMesg.put("errorMesg", "Invalid Request");
				data = errMesg.toString();
				status = 400;
			} 
			else {
				DynamoDBMapper mapper = new DynamoDBMapper(ddb);
				
				Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		        eav.put(":user_id", new AttributeValue().withS(userId));
				
				DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
			            .withFilterExpression("UserID = :user_id").withExpressionAttributeValues(eav);
				
				List<Project> scanResult = mapper.scan(Project.class, scanExpression);
				System.out.println("scanResult size::"+ scanResult.size());
				
				for (Project project : scanResult) {
		            System.out.println(project.getUserID() + "::" + project.getMetaData() + "::" + project.getCreatedOn() + "::" + project.getProjectName());
		            projectJson = new JSONObject(project);
		            projectList.put(projectJson);
		        }
				
				System.out.println("projectArray Len::" + projectList.length());
				
				if (projectList.length() == 0) {
					data = projectList.toString();
					status = 204;
				} else {
					data = projectList.toString();
					status = 200;
				}
			}
			
		} catch (Exception e) {
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