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
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import java.util.Iterator;

public class GetProjectList implements RequestHandler<APIGatewayProxyRequestEvent, ApiGatewayProxyResponse> {

    public GetProjectList() {}
    
    public ApiGatewayProxyResponse handleRequest(APIGatewayProxyRequestEvent gatewayEvent, Context context) {
    	context.getLogger().log("userId: " + gatewayEvent.getPathParameters().get("userId"));
        ApiGatewayProxyResponse response = null;
        String userId = null;
        String projectList = null;
    	
        try {
        	userId = gatewayEvent.getPathParameters().get("userId");
        	
        	projectList = getProjectList(userId);
        	System.out.println("projectList resp:" + projectList);
        	
        	Map<String, String> headers = new HashMap();
            headers.put("Content-Type", "application/json");
            
        	response = new ApiGatewayProxyResponse(200, headers, projectList);
            
        } catch (Exception e) {
            e.printStackTrace();
            //context.getLogger().log(String.format(
              //  "Error getting object %s from bucket %s. Make sure they exist and"));
            //throw e;
        }
        return response;
	}

    private String getProjectList(String userId) {
    	JSONObject response = new JSONObject();
    	String tableName = "Projects";

		final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();
		
		try {
			DynamoDB dynamoDB = new DynamoDB(ddb);
			Table table = dynamoDB.getTable(tableName);
			
			QuerySpec spec = new QuerySpec().withKeyConditionExpression("UserID = :user_id")
		            .withValueMap(new ValueMap().withString(":user_id", userId));
			
			ItemCollection<QueryOutcome> items = table.query(spec);

	        System.out.println("items:" + items.toString());

	        Iterator<Item> iterator = items.iterator();
	        
	        while (iterator.hasNext()) {
	            System.out.println(iterator.next().toJSONPretty());
	        }
			
			
		} catch (Exception e) {
			System.out.println("Error");
			response.put("Error", "Server Failure");
			e.printStackTrace();
        }
    	
    	return response.toString();
    }
}