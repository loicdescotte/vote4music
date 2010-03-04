package models;

import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.List;

/**
 * User: Loic Descotte
 * Date: 28 févr. 2010
 */
@Entity
public class Album extends Model {
    @Required
    public String name;
    @ManyToOne
    public Artist artist;
    public Date releaseDate;
    public String genre;

    public void setArtist(Artist artist){
        System.out.println(artist.name);
        List<Artist> existingArtists = Artist.find("byName", artist.name).fetch();
        if(existingArtists.size()>0){
            //Artist name is unique
            this.artist=existingArtists.get(0);
        }
        else{
            this.artist=artist;
        }
    }

    @Override
    public Album save(){
        //save artist if transient
        if(artist.id==null)
            artist.save();
        return super.save();
    }
}
