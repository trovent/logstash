package org.logstash.syntax;

import static spark.Spark.exception;
import static spark.Spark.internalServerError;
import static spark.Spark.notFound;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.get;

import org.jruby.runtime.builtin.IRubyObject;
import org.logstash.RubyUtil;

import com.google.gson.Gson;

public class SyntaxCheckServer {
	
	private static final int DEFAULT_PORT = 8080;
	
	/**
	 * Starting the server
	 * 
	 * @param port Listening port
	 */
	public static void start(Integer port) {
		
		// Set the server port
		port(port != null ? port : DEFAULT_PORT);
        
		// Handle not found api
        notFound((req, res) -> {
            res.type("application/json");
            return new Gson().toJson(new SyntaxCheckError("API does not exist"));
        });
        
        // Handle internal server error
        internalServerError((req, res) -> {
            res.type("application/json");
            return new Gson().toJson(new SyntaxCheckError("Internal server error"));
        });
        
        // Handle any exception
        exception(Exception.class, (exception, req, res) -> {
        	res.type("application/json");
        	res.status(500);
        	res.body(new Gson().toJson(new SyntaxCheckError(exception.getMessage())));
        });
        
        // APIs defined under /api path
        path("/api", () -> {
        	
        	get("/", (req, res) -> "Hello World");
        	        	
        	// Check syntax API
	        post("/syntax-check", (req, res)-> {
	        	String configText = req.body();
	            final IRubyObject compiler = RubyUtil.RUBY.executeScript(
	                "require 'logstash/compiler'\nLogStash::Compiler",
	                ""
	            );
	            final IRubyObject code =
	                    compiler.callMethod(RubyUtil.RUBY.getCurrentContext(), "check_syntax",
	                        new IRubyObject[]{
	                            RubyUtil.RUBY.newString(configText)
	                        }
	                    );
	            SyntaxCheck synCheck = code.toJava(SyntaxCheck.class);
	            
	            res.type("application/json");
	            return new Gson().toJson(synCheck);
	        });
	        
        });
        
	}
	
	/**
	 * Stopping the server
	 */
	public static void stop() {
		stop();
	}

}
