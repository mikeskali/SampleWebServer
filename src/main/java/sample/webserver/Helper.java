package sample.webserver;

import javax.ws.rs.Consumes;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class Helper {
	@SuppressWarnings({ "rawtypes"})
	public static List<RegisteredResource> getSupportedPaths(Collection<Class<?>> providerClasses){
		List<RegisteredResource> retList = new ArrayList<RegisteredResource>();
		for(Class cl:providerClasses){
			retList.addAll(getClassPaths(cl));
		}
		
		retList.sort((str1,str2)->str1.getPath().compareTo(str2.getPath()));
		return retList;
	}
	
	@SuppressWarnings({ "rawtypes"})
	public static List<RegisteredResource> getSupportedPaths(Set<Class<?>> providerClasses){
		List<RegisteredResource> retList = new ArrayList<RegisteredResource>();
		for(Class cl:providerClasses){
			retList.addAll(getClassPaths(cl));
		}
		
		retList.sort((str1,str2)->str1.getPath().compareTo(str2.getPath()));
		return retList;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<RegisteredResource> getClassPaths(Class providerClass){
		List<RegisteredResource> retPaths = new ArrayList<RegisteredResource>();
		
		String root = "";
		
		Annotation retAnnotation = providerClass.getDeclaredAnnotation(Path.class);
		Method[] methods = providerClass.getDeclaredMethods();		
		
		if(retAnnotation != null){
			Path path = (Path)retAnnotation;
			root = path.value();
			if(!root.startsWith("/")){
				root = "/" + root;
			}
		}
		
		for(Method method:methods){
			Annotation[] annotations = method.getAnnotations();
			Produces produces = method.getDeclaredAnnotation(Produces.class);
			Consumes consumes = method.getDeclaredAnnotation(Consumes.class);
			
			String producesStr = "";
			String consumesStr = "";
			
			if(produces != null){
				producesStr = combineStrings(produces.value());
			}
			
			if(consumes != null){
				consumesStr = combineStrings(consumes.value());
			}			
			
			for(Annotation annotation:annotations){				
				Annotation[] annotations2ndLevel = annotation.annotationType().getAnnotations();
				for(Annotation secLevel:annotations2ndLevel){
					if(secLevel.annotationType() == HttpMethod.class){
						// It is a resource...
						Path path = method.getAnnotation(Path.class);
						if(path == null || path.value().isEmpty()){
							retPaths.add(new RegisteredResource(root, annotation.annotationType().getSimpleName(), consumesStr, producesStr));
						} else {
							String currPath = path.value();
							if(!currPath.startsWith("/")){
								currPath = "/" + currPath;
							}							
							
							retPaths.add(new RegisteredResource(root + currPath, annotation.annotationType().getSimpleName(), consumesStr, producesStr));
						}
					}
				}
			}
		}
		
		return retPaths;
	}
	
	private static String combineStrings(String[] strings){
		String retStr = "";
		for(String curr:strings){
			if(retStr.length()==0){
				retStr = curr;
			} else {
				retStr = retStr + "," + curr;
			}
		}
		return retStr;
	}
}
