package controllers;

import models.Album;
import play.Play;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Admin extends Controller {

	/**
	 * 
	 */
	public static void login() {
		Application.list();
	}
	


	/**
	 * Delete album
	 * 
	 * @param id
	 */
	 @Check("isAdmin")
	public static void delete(Long id) {
		if (id == null) {
			render();
		}
		Album album = Album.findById(id);
		album.delete();
		Application.list();
	}

}
