import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import models.Album;
import models.Artist;
import org.junit.After;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import play.Logger;
import play.db.jpa.JPAPlugin;
import play.mvc.Http.Response;
import play.test.Fixtures;
import play.test.FunctionalTest;

public class ApplicationTest extends FunctionalTest {

    @Before
    public void setUp() {
        Fixtures.deleteAll();
    }

    @Test
    public void testThatIndexPageWorks() {
        Response response = GET("/");
        assertIsOk(response);
        assertContentType("text/html", response);
        assertCharset("utf-8", response);
    }

    @Test
    public void testYML() {
		Fixtures.load("data.yml");
        Response response = GET("/api/albums.xml");
        assertIsOk(response);
    }


    @Test
    public void testUniqueArtist() {
        Artist artist1 = new Artist("john");
        Album album1 = new Album("coolAlbum");
        album1.artist=artist1;
        album1.replaceDuplicateArtist();
        album1.save();
        // name must be unique
        Artist artist2 = new Artist("john");
        Album album2 = new Album("coolAlbum2");
        album2.artist=artist2;
        album2.replaceDuplicateArtist();
        album2.save();
        // check artist is unique
        assertEquals(Artist.find("byName", "john").fetch().size(),1);
    }


    @Test
    public void testJsonApi() {
        //preconditions
		Response artists = GET("/api/artists.json");
        assertFalse(artists.out.toString().contains("john"));

		Response albums = GET("/api/albums.json");
        assertFalse(albums.out.toString().contains("album1"));

		String album1 = "{ \"name\":\"album1\", \"artist\":{ \"name\":\"john\" }, \"releaseDate\":\"12 sept. 2010 00:00:00\", \"genre\":\"ROCK\" }";
        POST("/api/album", "application/json", album1);

        artists = GET("/api/artists.json");
        assertTrue(artists.out.toString().contains("john"));

		albums = GET("/api/albums.json");
        assertTrue(albums.out.toString().contains("album1"));
    }

	@Test
    public void testXmlApi() {
        Response artists = GET("/api/artists.xml");
        assertFalse(artists.out.toString().contains("john"));

		Response albums = GET("/api/albums.xml");
        assertFalse(albums.out.toString().contains("album1"));

		String album1 = "<album><artist><name>john</name></artist><name>album1</name><release-date>2010</release-date><genre>ROCK</genre><nvVotes>0</nvVotes></album>";
        POST("/api/album", "application/xml", album1);
        
		artists = GET("/api/artists.xml");
        assertTrue(artists.out.toString().contains("john"));

		albums = GET("/api/albums.xml");
        assertTrue(albums.out.toString().contains("album1"));
       
    }



}
