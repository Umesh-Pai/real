package com.smartz.vt.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.smartz.vt.domain.ApiGatewayProxyResponse;

//import java.util.HashMap;
//import java.util.Map;
//import org.json.JSONObject;

public class GetMetadata implements RequestHandler<APIGatewayProxyRequestEvent, ApiGatewayProxyResponse> {

    public GetMetadata() {}
    
    public ApiGatewayProxyResponse handleRequest(APIGatewayProxyRequestEvent gatewayEvent, Context context) {
    	context.getLogger().log("Id: " + gatewayEvent.getPathParameters().get("id"));
        context.getLogger().log("Method: " + gatewayEvent.getHttpMethod());
        context.getLogger().log("Path: " + gatewayEvent.getPath());
        context.getLogger().log("Content: " + gatewayEvent.getHeaders().get("Content-Type"));
    	
		return null;
	}

    /*@Override
    public ApiGatewayProxyResponse handleRequest1(APIGatewayProxyRequestEvent gatewayEvent, Context context) {
        context.getLogger().log("Id: " + gatewayEvent.getPathParameters().get("id"));
        context.getLogger().log("Method: " + gatewayEvent.getHttpMethod());
        context.getLogger().log("Path: " + gatewayEvent.getPath());
        //context.getLogger().log("Content: " + gatewayEvent.getHeaders().get("Content-Type"));
        
        String empId = null;
        try {
        	
        	JSONObject body = new JSONObject();
        	
        	Map<String, String> headers = new HashMap();
            headers.put("Content-Type", "application/json");
            
        	ApiGatewayProxyResponse response = new ApiGatewayProxyResponse(200, headers, body.toString());
        	
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            //context.getLogger().log(String.format(
              //  "Error getting object %s from bucket %s. Make sure they exist and"));
            throw e;
        }
    } */

}