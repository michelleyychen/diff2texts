package config;

import org.glassfish.jersey.server.ResourceConfig;


public class MyResourceConfig extends ResourceConfig{
	
	  public MyResourceConfig() {
		  
		  packages("api");
		  
	  }
	  
}
