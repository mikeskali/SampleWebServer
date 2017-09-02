package sample.webserver.math;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/Math")
public class MathOperations {
	Logger logger = LoggerFactory.getLogger(MathOperations.class);

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response simulatorWelcome(){
		return Response.status(200).entity("<H1>Math Service</H1> <p>Hi, we currently only support fibonacci service.. use /Math/fibonacci to try it out</p>").build();
	}

	public static long calculateFibonacci(int level) {
		if (level == 1 || level == 2) {
			return 1;
		}
		int fibo1 = 1, fibo2 = 1, fibonacci = 1;

		for (int i = 3; i <= level; i++) {
			fibonacci = fibo1 + fibo2; // Fibonacci number is sum of previous two Fibonacci number
			fibo1 = fibo2;
			fibo2 = fibonacci;

		}
		return fibonacci; // Fibonacci number
	}
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("fibonacci")
	public Response getFibonacci(@QueryParam("level") String levelStr){
		logger.debug("Got a fibonacci request, requested level is:" + levelStr);


		String errorResponse = null;
		if(levelStr == null || levelStr.length() < 1){
			errorResponse = "Illegal level value, level parameter is empty";
		}

		int level = -1;
		try {
			level = Integer.parseInt(levelStr);
		} catch (Exception e){
			logger.error("Error parsing level", e);
			errorResponse = "Illegal level value, please use positive Integer values (got:  "+ e.getMessage() + ")";
		}

		String response = "<H1>Fibonacci Service</H1>\n";

		if(errorResponse != null){
			logger.error(errorResponse);
			response += "<p>Error: " + errorResponse + "</p>";
		} else {
			long fibonacciResult = calculateFibonacci(level);
			response += "<p>Fibonacci level " + level + ", is " + fibonacciResult + "</p>";
		}


		int status = 200;
		return Response.status(status).entity(response).build();
	}

}