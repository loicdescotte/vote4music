package controllers;

import models.Album;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Admin extends Controller {

    /**
     * Log in
     */
    public static void login() {
        Application.list();
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
        Application.list();
    }

    /**
     * Update album
     *
     * @param id
     */
    @Check("admin")
    public static void form(Long id) {
        Album album = Album.findById(id);
        render("@Application.form", album);
    }


}
