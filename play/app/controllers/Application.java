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
	 * 
	 * @param first
	 * @param filter
	 * @param genre
	 */
	public static void list() {
		// number of items to display
		int count = 4;
		// first item to display
		int first;
		if (session.get("first") == null) {
			first = 0;
			session.put("first", first);
		} else
			first = Integer.parseInt(session.get("first"));
		String genre = session.get("genre");
		String filter = session.get("filter");
		int total = 0;
		List<Album> albums = null;
		StringBuilder query = new StringBuilder("select a from Album a where (a.name like ? or a.artist.name like ?)");
		// filters
		if (genre != null && !genre.equals("")) {
			Genre genreEnum = Genre.valueOf(genre.toString().toUpperCase());
			if (filter == null) {
				total = Album.find("byGenre", genreEnum).fetch().size();
				albums = Album.find("byGenre", genreEnum).from(first).fetch(count);
			} else {
				query.append(" and a.genre=? order by a.averageVote desc");
				String queryFitler = "%" + filter + "%";
				total = Album.find(query.toString(), queryFitler, queryFitler, genreEnum).fetch().size();
				albums = Album.find(query.toString(), queryFitler, queryFitler, genreEnum).from(first).fetch(count);
			}
		} else {
			if (filter == null) {
				total = Album.findAll().size();
				albums = Album.all().from(first).fetch(count);
			} else {
				query.append(" order by a.averageVote desc");
				String queryFitler = "%" + filter + "%";
				total = Album.find(query.toString(), queryFitler, queryFitler).fetch().size();
				albums = Album.find(query.toString(), queryFitler, queryFitler).from(first).fetch(count);
			}
		}

		render(albums, total, count);
	}

	/**
	 * Set list parameters in user session
	 * 
	 * @param first
	 * @param filter
	 * @param genre
	 */
	public static void paramList(Integer first, String filter, String genre) {
		if (first != null)
			session.put("first", first);
		if (filter != null)
			session.put("filter", filter);
		if (genre != null)
			session.put("genre", genre);
		list();
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
		} else
			albums = Album.findAll();
		render(albums);
	}

	/**
	 * Create or update album
	 * 
	 * @param id
	 */
	public static void form(Long id) {
		if (id == null) {
			render();
		}
		Album album = Album.findById(id);
		Artist artist = album.artist;
		render(album, artist);
	}

	/**
	 * Delete album
	 * 
	 * @param id
	 */
	public static void delete(Long id) {
		if (id == null) {
			render();
		}
		Album album = Album.findById(id);
		album.delete();
		list();
	}

	/**
	 * Create or update album
	 * 
	 * @param album
	 * @param artist
	 */
	public static void save(@Valid Album album, Artist artist) {
		if (Validation.hasErrors())
			render("@form", album);
		// set the album
		album.artist = artist;
		album.save();
		list();
	}

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
		Album album = Album.findById(Long.parseLong(albumId));
		album.rate(Float.parseFloat(rating));
		renderText(rating);
	}

}