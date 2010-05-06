package controllers;
import models.Album;
import models.Artist;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.mvc.*;
import java.util.ArrayList;
import java.util.List;

public class Application extends Controller {

    public static void index() {
        render();
    }

    /**
     * List with pagination
     * @param first
     */
    public static void list(Integer first, String filter) {
        //number of items to display
        int count = 4;        
        if(first == null){
            first = 0;
        }
        int total;
        List<Album> albums;
        //filter if needed
        if(filter == null){
            total = Album.findAll().size();
            albums= Album.all().from(first).fetch(count);
        }
        else{
            String query = "select a from Album a where a.name like ? or a.artist.name like ?";
            String queryFitler = "%"+filter+"%";
            total = Album.find(query,queryFitler,queryFitler).fetch().size();
            albums= Album.find(query,queryFitler,queryFitler).from(first).fetch(count);
        }
        render(albums, first, total, count, filter);
    }
    
    /**
     * List with pagination
     * @param first
     */
    public static void listByGenre(String genre) {        	
        //List<Album> albums= Album.find("byGenre", genre).fetch();
		List<Album> albums= Album.findAll();
        render(albums);
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
        list(0,null);
    }
}