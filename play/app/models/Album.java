package models;

import javax.persistence.Query;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.util.Collections;
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
@NamedQueries({
    @NamedQuery(name = "searchAlbumsWithFilter",
        query = "select a from Album a where (a.name like :filter or a.artist.name like :filter) order by a.nbVotes desc")
})
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
    	if(total!=0)
            return total/nbVotes;
        return 0;
    }

    /**
     *
     * @param genre
     * @return
     */
    public static List<Album> listByGenre(String genre) {
       Genre genreEnum = Genre.valueOf(genre.toString().toUpperCase());
       List<Album> albums = Album.find("byGenre", genreEnum).fetch();
       return sortByPopularity(albums);
    }

    private static List<Album> sortByPopularity(List<Album> albums){
    	List sortedAlbums = sort(albums, on(Album.class).getPopularity());
        //lambdaj sort is ascending
        Collections.reverse(sortedAlbums);
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
                    Query query= Album.em().createNamedQuery("searchAlbumsWithFilter");
                    query.setParameter("filter", "%"+filter+"%");
                    query.setMaxResults(100);
                    albums = query.getResultList();
		}
		else albums = Album.find("from Album").fetch(100);
		return albums;
	}
}
