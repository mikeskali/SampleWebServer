package sample.webserver;

public class RegisteredResource {
	String path;
	String method;
	String produces;
	String consumes;
	
	public RegisteredResource(String path, String method, String consumes, String produces) {
		super();
		this.path = path;
		this.method = method;
		this.consumes = consumes;
		this.produces = produces;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getProduces() {
		return produces;
	}
	public void setProduces(String produces) {
		this.produces = produces;
	}
	public String getConsumes() {
		return consumes;
	}
	public void setConsumes(String consumes) {
		this.consumes = consumes;
	}	
}
