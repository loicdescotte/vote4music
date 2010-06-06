package controllers;

import play.Play;
import play.mvc.Controller;

public class Admin extends Controller {

	public static void login() {
		render();
	}

	public static void authenticate(String login, String password) {
		if (Play.configuration.getProperty("application.admin").equals(login) && Play.configuration.getProperty("application.adminpwd").equals(password)) {
			session.put("admin", "true");
			Application.list(null, null, null);
		}
		params.flash();
		flash.error("Bad login or password");
		login();
	}

}
