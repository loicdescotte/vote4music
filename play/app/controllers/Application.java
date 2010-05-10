package controllers;

import models.Album;
import models.Artist;
import models.Genre;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.mvc.Controller;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class Application extends Controller {

    public static void index() {
        render();
    }

    /**
     * List with pagination
     * @param first
     */
    public static void list(Integer first, String filter, String genre) {
        //number of items to display
        int count = 4;        
        if(first == null){
            first = 0;
        }
        int total=0;
        List<Album> albums = null;
        StringBuffer query = new StringBuffer("select a from Album a where (a.name like ? or a.artist.name like ?)");
        //filter if needed            
        if(genre!=null && !genre.equals("")){
            Genre genreEnum = Genre.valueOf(genre.toString().toUpperCase());
            if(filter == null){                
                total = Album.find("byGenre",genreEnum ).fetch().size();
                albums= Album.find("byGenre",genreEnum).from(first).fetch(count);
            }
            else{
                query.append(" and a.genre=?");
                String queryFitler = "%"+filter+"%"; 
                total = Album.find(query.toString(),queryFitler,queryFitler,genreEnum).fetch().size();
                albums= Album.find(query.toString(),queryFitler,queryFitler,genreEnum).from(first).fetch(count);
            }
        }
        else{
            if(filter == null){
                total = Album.findAll().size();
                albums= Album.all().from(first).fetch(count);
            }
            else{
                String queryFitler = "%"+filter+"%";
                total = Album.find(query.toString(),queryFitler,queryFitler).fetch().size();
                albums= Album.find(query.toString(),queryFitler,queryFitler).from(first).fetch(count);
            }
         }
        render(albums, first, total, count, filter, genre);
    }
    
    /**
     * List in xml format
     * @param first
     */
    public static void listXml(String genre) {
        List<Album> albums;
        if(genre!=null){
            Genre genreEnum = Genre.valueOf(genre.toString().toUpperCase());
            albums = Album.find("byGenre",genreEnum ).fetch();
        }
        else albums = Album.findAll();
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
        list(0,null,null);
    }

    public static void saveXML(){
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document = null;
        try{
        DocumentBuilder builder = factory.newDocumentBuilder();
        document = builder.parse(request.body);
        }
        catch(Exception e){
        }
        Element albumNode = document.getDocumentElement();
        //artist
        NodeList artistNode = albumNode.getElementsByTagName("artist");
        String artistName = artistNode.item(0).getTextContent();
        Artist artist = new Artist(artistName);

        //album name
        NodeList nameNode = albumNode.getElementsByTagName("name");
        String name = nameNode.item(0).getTextContent();
        Album album = new Album(name);

        //release date
        NodeList dateNode = albumNode.getElementsByTagName("release-date");
        String date = dateNode.item(0).getTextContent();
        DateFormat dateFormat = new SimpleDateFormat("yyyy");
        try{
            album.releaseDate=dateFormat.parse(date);
        }
        catch(ParseException e){
        }

        //genre
        NodeList genreNode = albumNode.getElementsByTagName("genre");
        String genre= genreNode.item(0).getTextContent();
        Genre genreEnum = Genre.valueOf(genre.toString().toUpperCase());
        album.genre=genreEnum;

        //set the album
        album.artist=artist;
        album.save();
    }
}