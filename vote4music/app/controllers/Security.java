package controllers;

import play.Play;

public class Security extends Secure.Security {
	
    static boolean check(String profile) {
    	if(profile.equals("admin"))
    			return session.get("username").equals("admin");
    	return false;
    }
    
    static boolean authentify(String username, String password) {
    	return Play.configuration.getProperty("application.admin").equals(username) 
    		&& Play.configuration.getProperty("application.adminpwd").equals(password);
    }
    
}