package sample.webserver.generic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sample.webserver.Helper;
import sample.webserver.RegisteredResource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


@Path("/")
public class Welcome {
	public static Logger logger = LoggerFactory.getLogger(Welcome.class);
	
	private String addEntry(RegisteredResource resource){
		String ret = "<tr>";
		ret+="<td><A HREF="+resource.getPath() + ">" + resource.getPath() + "</A></td>" + "<td>"+resource.getMethod()  + "</td>" + "<td>"+resource.getConsumes()  + "</td>" + "<td>"+resource.getProduces() + "</td>";
		ret+="</tr>";
		return ret;
	}
	private String getHeader(){
		return "<tr><th>path</th><th>method</th>" + "<th>consumes</th>" + "<th>produces</th></tr>";
	}
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response defaultResponse(@Context Application application, 
									@Context HttpServletRequest request){

		String ret = "<H1>Welcome to a sample Web Server</H1>";
		ret+="<table>";		
		ret+=getHeader();
		
		List<RegisteredResource> paths = Helper.getSupportedPaths(application.getClasses());
		for(RegisteredResource currResource:paths){
			ret += addEntry(currResource);
		}
		
		return Response.status(200).entity(ret).build();
	}
}
