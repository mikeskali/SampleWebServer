package sample.webserver;

import com.sun.jersey.spi.container.servlet.ServletContainer;
import sample.webserver.math.MathOperations;
import sample.webserver.generic.Welcome;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SampleWebServer {
	private static Logger logger = LoggerFactory.getLogger(SampleWebServer.class);

	private static Integer DEFAULT_PORT = 8080;

	int serverPort;
	Server jettyServer;


	//======================Standalone Execution==================================//
	public static void main(String[] args) throws Exception {
		Logger logger = LoggerFactory.getLogger(SampleWebServer.class);
		logger.info("Usage: java -jar webserver-simulator-VERSION listeningPort fully-qualified-class-name1 fully-qualified-class-name2 fully-qualified-class-name3...");

		SampleWebServer webServer = new SampleWebServer();

		if(args.length == 0) {
			webServer = startNoArguments(webServer);
		} else {
			webServer = startWithArguments(webServer, args);
		}
		webServer.join();
	}

	public static SampleWebServer startNoArguments(SampleWebServer webServer) throws Exception {
		logger.info("No arguments passed to the program, using default values");
		int port 						= DEFAULT_PORT;

		List<Class<?>> jerseyProviderClasses = new ArrayList<>();

		jerseyProviderClasses.add(Welcome.class);
		jerseyProviderClasses.add(MathOperations.class);

		webServer.startServer(port, jerseyProviderClasses);

		return webServer;
	}

	public static SampleWebServer startWithArguments(SampleWebServer webServer, String[] args) throws Exception {
		logger.info("Starting web server using passed arguments");

		int port 						= webServer.getPort(args);
		List<Class<?>> jerseyListeners	= webServer.loadJerseyProvidersFromArguments(args);
		webServer.startServer(port, jerseyListeners);

		return webServer;
	}


	@SuppressWarnings("rawtypes")
	public void startServer(int port, List<Class<?>> providerClasses) throws Exception{
		logger.info("Starting Server");
		// Do we need sessions?
		

		logger.info("Starting server on port: " + port);
		jettyServer = new Server(port);
		jettyServer.setHandler(getJerseyServletHolder(providerClasses));

		logger.info("-----------Before Add Servlet------------------");

		try {
			logger.info("-----------Before Start------------------");
			jettyServer.start();
		} catch (Exception e){
			logger.info("Failed starting Jetty server, check if the port is already in use", e);
		}
	}
	
	public ServletContextHandler getJerseyServletHolder(List<Class<?>> providerClasses){
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		
		ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, "/*");
		jerseyServlet.setInitOrder(0);		
		// Tells the Jersey Servlet which REST service/class to load. 
		// [For specified_value, specify one or more class paths. Use a delimiter when specifying multiple class paths. You can use spaces, commas (,), semicolons (;), and \n as delimiters.]
		String jerseyProviders = generateJerseyProvidersClassnames(providerClasses);		

		jerseyServlet.setInitParameter("com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.ClassNamesResourceConfig");
		jerseyServlet.setInitParameter("com.sun.jersey.config.property.classnames",  jerseyProviders);			

		List<RegisteredResource> supportedPaths = Helper.getSupportedPaths(providerClasses);
		if(!providerClasses.contains(Welcome.class)){
			providerClasses.add(Welcome.class);
		}
		
		logger.info("Known Services:");
		logger.info("path, http method, consumes, produces");
		for(RegisteredResource curr:supportedPaths){
			logger.info(curr.getPath() + ":" + curr.getMethod() + ":" + curr.getConsumes() + ":" + curr.getProduces());
		}
		
		return context;
	}

	public void join() throws InterruptedException{
		jettyServer.join();
	}

	private String generateJerseyProvidersClassnames(List<Class<?>> providerClasses){
		String retStr = "";
		for(Class<?> cl:providerClasses){
			retStr += cl.getCanonicalName() + ";";
		}

		if(retStr.length() > 0){
			retStr = retStr.substring(0, retStr.length()-1);
		}

		return retStr;
	}

	public boolean isRunning(){
		if(jettyServer == null){
			return false;
		}
		return jettyServer.isRunning();
	}

	public boolean isFailed(){
		if(jettyServer == null){
			return false;
		}

		return jettyServer.isFailed();
	}

	public void stopServer() throws Exception{
		logger.info("Destroying Jetty server");
		jettyServer.stop();						
		logger.info("Jetty server is down");
	}

	private int getPort(String[] args){
		if(args != null && args.length > 0 ){
			String portStr = args[0];

			try{
				serverPort = Integer.valueOf(portStr);
			} catch (Exception e){
				System.out.println("Failed loading the port");
				e.printStackTrace();
			}
		} else {
			logger.info("No port specified, trying default port(" + serverPort + "). If you would like to specify a port, pass it as the only argument");
		}
		return serverPort;
	}

	@SuppressWarnings("rawtypes")
	private List<Class<?>> loadJerseyProvidersFromArguments(String[] args){
		List<Class<?>> classes = new ArrayList<Class<?>>();
		if(args.length > 1){
			for(int i=1;i<args.length;i++){
				String currClassStr = args[i];
				logger.info("got class: " + currClassStr);

				Class currClass;
				try {
					currClass = Class.forName(currClassStr);
					classes.add(currClass);
				} catch (ClassNotFoundException e) {
					logger.error("The specified class(" + currClassStr + ") is not recognized, ignoring", e);
				}				
			}
		} 

		if(classes.size() == 0) {
			logger.info("No provider classes specified, initializing the default PandaWhoIsSimulator, PageRankSimulator and UrlParserSimulator");
		}

		// The default polite entry point
		classes.add(Welcome.class);
		return classes;
	}
}
