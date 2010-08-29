import org.junit.*;

import java.util.*;

import play.db.jpa.JPAPlugin;
import play.test.*;
import models.*;

public class CoreTest extends UnitTest {
	
    @Test
    public void filterByYearTest() {
        List<Album> albums = new ArrayList<Album>();
        
        Album album1 = new Album("album1");
        Calendar c1 = Calendar.getInstance();
        c1.set(2010, 1, 1);
        album1.releaseDate= c1.getTime();
        albums.add(album1);
        
        Album album2 = new Album("album1");
        Calendar c2 = Calendar.getInstance();
        c2.set(2009, 1, 1);
        album2.releaseDate= c2.getTime();
        albums.add(album2);
        //filter by year
        albums = Album.filterByYear(albums, "2010");
        
        //Only one album is from 2010
        assertTrue(albums.size()==1);
        
    }

}
