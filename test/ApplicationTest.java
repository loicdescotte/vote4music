
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
        JPAPlugin.startTx(false);
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

    @Ignore
    @Test
    public void testYML() {
        Response response = GET("/api/albums.xml");
        assertIsOk(response);
        String xmlTree = response.out.toString();
        //just to see in console what is loaded with YAML for selenium tests
        System.out.println(xmlTree);
    }


    @Test
    public void testUniqueArtist() {
        //JPA init error
        Artist artist1 = new Artist("joe");
        Album album1 = new Album("coolAlbum");
        album1.setArtist(artist1);
        album1.save();
        // warning : name must be unique
        Artist artist2 = new Artist("joe");
        Album album2 = new Album("coolAlbum2");
        album2.setArtist(artist2);
        album2.save();
        // check artist is unique
        assertEquals(Artist.find("byName", "joe").fetch().size(),1);
    }

    //TODO fix JSON save album API and remove XML in test
    @Ignore
    @Test
    public void testArtistisUniqueFromAPI() {
        String album1 = "<album><artist>joe</artist><name>album1</name><release-date>2010</release-date><genre>ROCK</genre></album>";
        POST("/api/album", "application/xml", album1);
        // Other album, same artist name
        String album2 = "<album><artist>joe</artist><name>album2</name><release-date>2010</release-date><genre>ROCK</genre></album>";
        POST("/api/album", "application/xml", album2);
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
        String album3 = "<album><artist>bob</artist><name>album3</name><release-date>2010</release-date><genre>ROCK</genre></album>";
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


    @After
     public void end() {
        JPAPlugin.closeTx(false);
    }

}
