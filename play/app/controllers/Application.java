package controllers;
import models.Album;
import models.Artist;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.mvc.*;

import java.util.List;

public class Application extends Controller {

    public static void index() {
        render();
    }

    /**
     * List with pagination
     * @param first
     */
    public static void list(Integer first) {
        if(first == null){
            first = 1;
        }
        int total = Album.all().fetch().size();
        List<Album> albums = Album.all().from(first).fetch(10);
        int count = 10;
        render(albums, first, total, count);
    }

    /**
     * Edit album
     * @param id
     */
     public static void form(Long id) {
        if(id == null) {
            render();
        }
        Album album = Album.findById(id);
        Artist artist = album.artist;
        render(album, artist);
    }

    public static void save(@Valid Album album, Artist artist) {
        if(Validation.hasErrors())
            render("@form", album);
        //set the album
        album.artist=artist;
        album.save();
        list(1);
    }
}