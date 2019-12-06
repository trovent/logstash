package org.logstash.integration;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import io.restassured.RestAssured;

/**
 * JUnit Wrapper for Syntax check Integration Tests.
 */
public final class SyntaxCheckTests {
	
	private static final int PORT = 4567;

    @Test
    public void syntax_test_valid() throws Exception {
    	
    	String[] files = {"netflow.conf", "connections.conf", "syslog.conf", "winlogbeat.conf"};
    	
    	for (String file : files) {
    		String configuration = "";
        	try(FileInputStream fis = new FileInputStream("configurations/" + file)) {
        		configuration = IOUtils.toString(fis, "UTF-8");
        	}
        	
        	RestAssured.with().body(configuration)
	    		.when()
	    		.request("POST", String.format("http://localhost:%d/api/syntax-check", PORT))
	    		.then()
	    		.statusCode(200)
	    		.assertThat()
	    		.body("isOk", equalTo(true));
    	}
    }
    
    @Test
    public void syntax_test_invalid() throws Exception {
    	
    	String[] files = {"failed.conf"};
    	
    	for (String file : files) {
    		String configuration = "";
        	try(FileInputStream fis = new FileInputStream("configurations/" + file)) {
        		configuration = IOUtils.toString(fis, "UTF-8");
        	}
        	
        	RestAssured.with().body(configuration)
	    		.when()
	    		.request("POST", String.format("http://localhost:%d/api/syntax-check", PORT))
	    		.then()
	    		.statusCode(200)
	    		.assertThat()
	    		.body("isOk", equalTo(false))
	    		.assertThat()
	    		.body("error", containsString("at line 11"));
    	}
    }
    
    @Test
    public void syntax_test_404() throws Exception {
    	
    	RestAssured.when()
			.request("POST", String.format("http://localhost:%d/api/syntax-check-404", PORT))
			.then()
			.statusCode(404);
    }
}
