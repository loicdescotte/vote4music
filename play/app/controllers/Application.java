package controllers;

import models.Album;
import models.Artist;
import models.Genre;
import play.Logger;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.mvc.Controller;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;


public class Application extends Controller {

	public static void index() {
		render();
	}

	/**
	 * List albums
	 * 
	 */
	public static void list(String filter) {
		List<Album> albums = Album.findAll(filter);
		render(albums);
	}
	
	/**
	 * List albums by genre
	 * 
	 * @param genre
	 */
	public static void listByGenre(String genre){
		List<Album> albums = Album.listByGenre(genre);
		render(genre, albums);
	}

	/**
	 * List in xml format
	 * 
	 * @param first
	 */
	public static void listXml(String genre) {
		List<Album> albums;
		if (genre != null) {
			Genre genreEnum = Genre.valueOf(genre.toString().toUpperCase());
			albums = Album.find("byGenre", genreEnum).fetch();
		} 
		else albums = Album.all().fetch();
		render(albums);
	}

	/**
	 * Create album
	 * 
	 * @param id
	 */
	public static void form() {		
            render();
	}

	/**
	 * Create or update album
	 * 
	 * @param album
	 * @param artist
	 */
	public static void save(@Valid Album album, @Valid Artist artist) {
		if (Validation.hasErrors())
			render("@form", album);
		// set the album
		album.artist = artist;
		album.save();
		for (int i = 0; i<101; i++){
			Album a = new Album(album.name);
			a.genre=album.genre;
			a.releaseDate=album.releaseDate;
			a.artist=album.artist;
			a.save();
		}
		list(null);
	}

	/**
	 * Save album via API
	 */
	public static void saveXML() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document document = null;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(request.body);
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		Element albumNode = document.getDocumentElement();
		// artist
		NodeList artistNode = albumNode.getElementsByTagName("artist");
		String artistName = artistNode.item(0).getTextContent();
		Artist artist = new Artist(artistName);

		// album name
		NodeList nameNode = albumNode.getElementsByTagName("name");
		String name = nameNode.item(0).getTextContent();
		Album album = new Album(name);

		// release date
		NodeList dateNode = albumNode.getElementsByTagName("release-date");
		String date = dateNode.item(0).getTextContent();
		DateFormat dateFormat = new SimpleDateFormat("yyyy");
		try {
			album.releaseDate = dateFormat.parse(date);
		} catch (ParseException e) {
			Logger.error(e.getMessage());
		}

		// genre
		NodeList genreNode = albumNode.getElementsByTagName("genre");
		String genre = genreNode.item(0).getTextContent();
		Genre genreEnum = Genre.valueOf(genre.toString().toUpperCase());
		album.genre = genreEnum;

		// set the album
		album.artist = artist;
		album.save();
	}

	/**
	 * Add vote
	 * @param rating
	 */
	public static void rate(String id, String rating) {
		String albumId = id.substring(6);
		Album album = Album.findById(1L);//TODO //(Long.parseLong(albumId));
		album.rate(Float.parseFloat(rating));
		renderText(rating);
	}

}