
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
        Fixtures.load("data.yml");
    }

    @Test
    public void testThatIndexPageWorks() {
        Response response = GET("/");
        assertIsOk(response);
        assertContentType("text/html", response);
        assertCharset("utf-8", response);
    }

    //@Ignore
    @Test
    public void testYML() {
        Response response = GET("/api/albums.xml");
        assertIsOk(response);
        String xmlTree = response.out.toString();
        //just to see in console what is loaded with YAML for selenium tests
        Logger.info(xmlTree);
    }


    @Test
    public void testUniqueArtist() {
        //JPA init error
        Artist artist1 = new Artist("joe");
        Album album1 = new Album("coolAlbum");
        album1.artist=artist1;
        album1.replaceDuplicateArtist();
        album1.save();
        // warning : name must be unique
        Artist artist2 = new Artist("joe");
        Album album2 = new Album("coolAlbum2");
        album2.artist=artist2;
        album2.replaceDuplicateArtist();
        album2.save();
        // check artist is unique
        assertEquals(Artist.find("byName", "joe").fetch().size(),1);
    }


    @Test
    public void testArtistisUniqueFromAPI() {
        String album1 = "{ \"name\":\"album1\", \"artist\":{ \"name\":\"joe\" }, \"releaseDate\":\"12 sept. 2010 00:00:00\", \"genre\":\"ROCK\" }";
        POST("/api/album", "application/json", album1);
        // Other album, same artist name
        String album2 = "{ \"name\":\"album2\", \"artist\":{ \"name\":\"joe\" }, \"releaseDate\":\"13 sept. 2010 00:00:00\", \"genre\":\"ROCK\" }";
        POST("/api/album", "application/json", album2);
        // check artist is unique (name must be unique)
        Response response = GET("/api/artists.xml");
        String xmlTree = response.out.toString();
        // parse response and assert there is only one artist
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document = null;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(new ByteArrayInputStream(xmlTree.getBytes()));
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
        Element rootNode = document.getDocumentElement();
        assertTrue(rootNode.getElementsByTagName("artist").getLength() == 1);
        // add an artist
        String album3 = "{ \"name\":\"album3\", \"artist\":{ \"name\":\"bob\" }, \"releaseDate\":\"14 sept. 2010 00:00:00\", \"genre\":\"ROCK\" }";
        POST("/api/album", "application/json", album1);
        POST("/api/album", "application/xml", album3);

        response = GET("/api/artists.xml");
        xmlTree = response.out.toString();

        // parse response and assert there is only one artist
        factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(new ByteArrayInputStream(xmlTree.getBytes()));
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
        rootNode = document.getDocumentElement();
        assertTrue(rootNode.getElementsByTagName("artist").getLength() == 2);
    }

}
