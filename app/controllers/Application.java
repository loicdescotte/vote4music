package controllers;

import com.google.gson.Gson;
import models.Album;
import models.Artist;
import models.Genre;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import play.Logger;
import play.Play;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.libs.XPath;
import play.mvc.Controller;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Application extends Controller {

    public static void index() {
        render();
    }


    /**
     * List albums
     */
    public static void list() {
        List<Album> albums = Album.all().fetch(100);
        render(albums);
    }

    /**
     * List albums with filter
     *
     * @param filter
     */
    public static void search(String filter) {
        List<Album> albums = Album.findAll(filter);
        render("@list", albums);
    }

    /**
     * List albums by genre
     *
     * @param genre
     */
    public static void listByGenreAndYear(String genre, String year) {
        //genre and year are mandatory
        notFoundIfNull(genre);
        notFoundIfNull(year);
        List<Album> albums = Album.findByGenreAndYear(genre, year);
        render(genre, year, albums);
    }


    /**
     * List albums in xml or json format
     *
     * @param genre
     * @param year
     */
    public static void listByApi(String genre, String year) {
        List<Album> albums;
        if (genre != null) {
            Genre genreEnum = Genre.valueOf(genre.toString().toUpperCase());
            albums = Album.find("byGenre", genreEnum).fetch();
        } else {
            albums = Album.findAll();
        }
        if (year != null) {
            albums = Album.filterByYear(albums, year);
        }
        if (request.format.equals("json"))
            renderJSON(albums);
        render(albums);
    }

    /**
     * List artists in xml or json format
     */
    public static void listArtistsByApi() {
        List<Artist> artists = Artist.findAll();
        if (request.format.equals("json"))
            renderJSON(artists);
        render(artists);
    }

    /**
     * Create album
     */
    public static void form() {
        render();
    }

    /**
     * Create or update album
     *
     * @param album
     * @param artist
     * @param cover
     */
    public static void save(@Valid Album album, @Valid Artist artist, File cover) {
        if (Validation.hasErrors()) {
            render("@form", album);
        }
        // set the album
        album.artist = artist;
        //look for duplicates
        album.replaceDuplicateArtist();
        album.save();

        //album cover
        if (cover != null) {
            String path = "/public/shared/covers/" + album.id;
            album.hasCover = true;
            File newFile = Play.getFile(path);
            //delete old cover if exists
            if (newFile.exists())
                newFile.delete();
            cover.renameTo(newFile);

            album.save();
        }

        //return to album list
        list();
    }


    /**
     * Save album via API
     */
    public static void saveAlbumByApi() {
        if (request.contentType.equalsIgnoreCase("application/xml"))
            saveAlbumXml();
        else if (request.contentType.equalsIgnoreCase("application/json"))
            saveAlbumJson();
    }

    /**
     * Save album via JSON API
     */
    private static void saveAlbumJson() {
        Gson gson = new Gson();
        Album album = gson.fromJson(new InputStreamReader(request.body), Album.class);
        album.replaceDuplicateArtist();
        album.save();
    }

    /**
     * Save album via XML API
     */
    private static void saveAlbumXml() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document = null;
        try {
            //create xml document
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(request.body);
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
        Element albumNode = document.getDocumentElement();
        //get the artist
        Node artistNode = XPath.selectNode("artist", albumNode);
        String artistName = XPath.selectText("name",artistNode);
        Artist artist = new Artist(artistName);
        //get the name
        String albumName = XPath.selectText("name", albumNode);
        Album album = new Album(albumName);
        //get the date
        String date = XPath.selectText("release-date",albumNode);
        DateFormat dateFormat = new SimpleDateFormat("yyyy");
        try {
            album.releaseDate = dateFormat.parse(date);
        } catch (ParseException e) {
            Logger.error(e.getMessage());
        }
        //get the genre
        String genre = XPath.selectText("genre", albumNode);
        Genre genreEnum = Genre.valueOf(genre.toString().toUpperCase());
        album.genre = genreEnum;

        //save in db
        album.artist = artist;
        album.save();
    }

    /**
     * @param id
     */
    public static void vote(String id) {
        Album album = Album.findById(Long.parseLong(id));
        album.vote();
        renderText(album.nbVotes);
    }

    /**
     * Years to display for top albums form
     *
     * @return
     */
    public static List<String> getYearsToDisplay() {
        List<String> years = new ArrayList<String>();
        for (int i = Album.getFirstAlbumYear(); i <= Album.getLastAlbumYear(); i++) {
            years.add(String.valueOf(i));
        }
        Collections.reverse(years);
        return years;
    }

    // WebSocket tests

    public static void publishEvent(String message) throws IOException {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Logger.error(e.getMessage());
        }
        AsyncController.liveStream.publish(message);
    }

    public static void testWebSocket() {
        render();
    }
}
