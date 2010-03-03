package controllers;

import models.Album;
import models.Artist;
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
        Artist artist = album.artist;
        render(album, artist);
    }

    public static void save(@Valid Album album, Artist artist) {
        if(validation.hasErrors())
            render("@form", album);
        //set the album
        album.artist=artist;
        //save artist if transient
        if(album.artist.id==null)
            album.artist.save();
        album.save();
        list();
    }
}