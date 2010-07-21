package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import play.data.validation.Required;
import play.db.jpa.Model;

import static ch.lambdaj.Lambda.*;

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
    

    /**
     * 
     * @return
     */
    public float getPopularity(){
    	return total/nbVotes;
    }
    
    /**
     * 
     * @return
     */
    public static List<Album> sortByPopularity(List<Album> albums){
    	List sortedAlbums = sort(albums, on(Album.class).getPopularity());
    	return sortedAlbums;
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
     * @param filter
     * @return
     */
	public static List<Album> findAll(String filter) {
		List<Album> albums;
		if(filter != null){
                    //limit to 100 results
                    albums = Album.find("select a from Album a where (a.name like ? or a.artist.name like ? order by a.nbVotes desc", filter, filter).fetch(100);
		}
		else albums = Album.find("from Album").fetch(100);
		return albums;
	}
}
