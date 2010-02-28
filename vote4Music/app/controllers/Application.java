package controllers;

import models.Album;
import play.data.validation.Valid;
import play.mvc.*;

import java.util.List;

public class Application extends Controller {

    public static void index() {
        render();
    }

    public static void list() {
        List<Album> albums = Album.findAll();
        render(albums);
    }

     public static void form(Long id) {
        if(id == null) {
            render();
        }
        Album album = Album.findById(id);
        render(album);
    }

    public static void save(@Valid Album album) {
        if(validation.hasErrors()) {
            if(request.isAjax()) error("Invalid value");
            render("@form", album);
        }
        album.save();
        list();
    }
}