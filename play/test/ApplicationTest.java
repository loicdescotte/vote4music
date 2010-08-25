import models.Album;
import models.Artist;
import org.junit.*;
import play.test.*;
import play.mvc.Http.*;

public class ApplicationTest extends FunctionalTest {

    @Test
    public void testThatIndexPageWorks() {
        Response response = GET("/");
        assertIsOk(response);
        assertContentType("text/html", response);
        assertCharset("utf-8", response);
    }
    
    @Test
    @Ignore
    public void testArtistisUnique() {
        //TODO test from UI, JPA does not work from here
        Artist artist1 = new Artist("joe");
        Album album1 = new Album("coolAlbum");
        album1.setArtist(artist1);
        album1.save();
        //warning : name must be unique
        Artist artist2 = new Artist("joe");
        Album album2 = new Album("coolAlbum2");
        album2.setArtist(artist2);
        album2.save();

        //check artist is unique
        assert(Artist.find("byName").fetch().size()==1);
    }
    
}