package models;

import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import org.hibernate.cfg.NotYetImplementedException;

import com.sun.org.apache.xml.internal.utils.UnImplNode;

import java.util.Date;
import java.util.List;

/**
 * User: Loic Descotte
 * Date: 28 fevr. 2010
 */
@Entity
public class Album extends Model {
    @Required
    public String name;
    @ManyToOne
    public Artist artist;
    @Required
    public Date releaseDate;
    @Enumerated(EnumType.STRING)
    public Genre genre;
    public long nbVotes = 0L;
    public float total = 0F;

    public Album(String name) {
        this.name = name;
    }

    public void setArtist(Artist artist){
        List<Artist> existingArtists = Artist.find("byName", artist.name).fetch();
        if(existingArtists.size()>0){
            //Artist name is unique
            this.artist=existingArtists.get(0);
        }
        else{
            this.artist=artist;
        }
    }
    
    public void rate(float rating){
        total += rating;
    	nbVotes++;
    	save();
    }

    @Override
    public Album save(){
        //save artist if transient
        if(artist.id==null)
            artist.save();
        return super.save();
    }
    
    /**
     * 
     * @return
     */
    public static List<Album> sortByPopularity(List<Album> albums){
    	//use LambdaJ
    	throw new NotYetImplementedException("not implemented");
    }
}
