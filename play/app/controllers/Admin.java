package controllers;

import models.Album;
import models.Artist;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Admin extends Controller {

	/**
	 * 
	 */
	public static void login() {
		Application.list(null);
	}

	/**
	 * Delete album
	 * 
	 * @param id
	 */
	@Check("admin")
	public static void delete(Long id) {
		Album album = Album.findById(id);
		album.delete();
		Application.list(null);
        }

        /**
         * Update album
         * @param id
         */
        @Check("admin")
        public static void form(Long id) {
		Album album = Album.findById(id);
		Artist artist = album.artist;
		render("@Application.form",album, artist);
	}


}
