package controllers;

import models.Album;
import models.Artist;
import models.Genre;
import play.mvc.*;

import java.util.Date;
import java.util.List;

public class Application extends Controller {

    public static void index() {
        render();
    }

    public static void list() {
        List<Album> albums = Album.findAll();
        render(albums);
    }
}