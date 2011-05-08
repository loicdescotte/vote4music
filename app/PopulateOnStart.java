import play.*;
import play.jobs.*;
import play.test.*;
 
import models.*;
 
@OnApplicationStart
public class PopulateOnStart extends Job {
 
    public void doJob() {
        // Check if the database is empty
        if(Album.count() == 0 && Artist.count() == 0) {
            Fixtures.load("init-data.yml");
        }
    }
 
}