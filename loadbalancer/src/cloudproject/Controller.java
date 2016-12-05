package cloudproject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

@Path("/")
public class Controller {
	
	static String server1="http://localhost:8081/cloudproject/dashboard/";
	static String server2="http://localhost:8082/cloudproject/dashboard/";
	static int requestNumber=0;
	static int adminRequests=0;
	static int vendorRequests=0;
	static int server1Requests=0;
	static int server2Requests=0;
	static ArrayList<String> recentRequests = new ArrayList<String>(1000);
	
	@Path("status")
	@GET
	@Produces("application/json")
	public String getOnStatus(@QueryParam("user") String user, 
			@QueryParam("role") String role) throws Exception {
		return restForwarderGet("status?user="+user+"&role="+role);
	}
	
	@Path("sensor")
	@GET
	@Produces("application/json")
	public String getSensorNumber(
			@QueryParam("user") String user, 
			@QueryParam("role") String role) throws Exception{ 
		return restForwarderGet("sensor?user="+user+"&role="+role);
	}
	
	@Path("login")
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public String login(String user) throws Exception {
		return restForwarderPost("login",user);
	}	
	
	@Path("map")
	@GET
	@Produces("application/json")
	public String getMap(@QueryParam("user") String user, @QueryParam("role") String role) throws Exception {
		return restForwarderGet("map?user="+user+"&role="+role);
	}
	
	@Path("routes")
	@GET
	@Produces("application/json")
	public String routes(@QueryParam("user") String user, @QueryParam("role") String role) throws Exception {
		return restForwarderGet("routes?user="+user+"&role="+role);
	}
	
	@Path("billamount")
	@GET
	@Produces("application/json")
	public String getBill(@QueryParam("user") String user) throws Exception {
		return restForwarderGet("billamount?user="+user);
	}
	
	@Path("adminbill")
	@GET
	@Produces("application/json")
	public String adminBill() throws Exception {
		return restForwarderGet("adminbill");
	}
	
	@Path("requests")
	@GET
	@Produces("application/json")
	public String getRequests() throws Exception {
		return "{\"total\":\""+requestNumber+"\", \"admin\":\""+adminRequests+"\","
				+ "\"vendor\":\""+vendorRequests+"\","
						+ "\"user\":\""+(requestNumber-(adminRequests+vendorRequests))+"\","
								+ "\"server1\":\""+server1Requests+"\","
										+ "\"server2\":\""+server2Requests+"\","
												+ "\"server1_url\":\""+server1+"\","
														+ "\"server2_url\":\""+server2+"\"}";
	}
	
	@Path("recent")
	@GET
	@Produces("application/json")
	public ArrayList<String> getRecentRequests() throws Exception {
		return recentRequests;
	}
	
	public static String restForwarderGet(String url) throws Exception{
		url = collectStats(url);
		
		String output = null;
		Client client = Client.create();
		WebResource webResource = client.resource(url);
		ClientResponse response = webResource.accept("application/json")
				.get(ClientResponse.class);
		output = response.getEntity(String.class);
		return output;
	}

	private static String collectStats(String url) {
		requestNumber ++;
		if(url.contains("admin")){
			adminRequests++;
		}else if(url.contains("vendor")){
			vendorRequests++;
		}
		if(requestNumber%2==0){
			url = server1+url;
			server1Requests++;
		}else{
			url = server2+url;
			server2Requests++;
		}
		recentRequests.add(url);
		return url;
	}
	
	public static String restForwarderPost(String url, String input) throws Exception{
		url = collectStats(url);
		String output = null;
		Client client = Client.create();
		WebResource webResource = client.resource(url);
		ClientResponse response = webResource.type("application/json")
				   .post(ClientResponse.class, input);
		output = response.getEntity(String.class);
		return output;
	}
	
	
	
}