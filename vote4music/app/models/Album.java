package models;

import java.text.SimpleDateFormat;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import play.data.validation.Required;
import static play.db.jpa.Model.*;
import play.db.jpa.Model;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.*;

/**
 * User: Loic Descotte
 * Date: 28 fevr. 2010
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "searchAlbumsWithFilter",
        query = "select a from Album a where a.name like :filter or a.artist.name like :filter")
})
public class Album extends Model {

    @Required
    public String name;
    @ManyToOne
    public Artist artist;
    @Required
    @Temporal(TemporalType.DATE)
    public Date releaseDate;
    @Enumerated(EnumType.STRING)
    public Genre genre;
    public long nbVotes = 0L;

    public Album(String name) {
        this.name = name;
    }

    /**
     * set Artist
     * @param artist
     */
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

    /**
     * Rate album
     * @param rating
     */
    public void vote(){
    	nbVotes++;
    	save();
    }

    /**
     *
     * @param genre
     * @return
     */
    public static List<Album> listByGenreAndYear(String genre, String year) {
       Genre genreEnum = Genre.valueOf(genre.toString().toUpperCase());
       List<Album> albums = Album.find("byGenre", genreEnum).fetch();
       if (year!=null && !year.equals(""))
            albums = filterByYear(albums, year);
       return sortByPopularity(albums);
    }


    /**
     * Sort by popularity
     * @param albums
     * @return
     */
    private static List<Album> sortByPopularity(List<Album> albums){
    	List sortedAlbums = sort(albums, on(Album.class).nbVotes);
        //lambdaj sort is ascending
        Collections.reverse(sortedAlbums);
    	return sortedAlbums;
    }

    /**
     * filter by year
     * @param albums
     * @return
     */
    public static List<Album> filterByYear(List<Album> albums, String year){
    	return select(albums, having(on(Album.class).getReleaseYear(),equalTo(year)));
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
                Query query= em().createNamedQuery("searchAlbumsWithFilter");
                query.setParameter("filter", "%"+filter+"%");
                query.setMaxResults(100);
                albums = query.getResultList();
            }
            else albums = Album.find("from Album").fetch(100);
            return sortByPopularity(albums);
	}

     /**
      * 
      * @return year
      */
     public String getReleaseYear(){
        SimpleDateFormat formatYear = new SimpleDateFormat("yyyy");
        return formatYear.format(releaseDate);
     }

     /**
      *
      * @return the first year for recorded albums
      */
     public static int getFirstAlbumYear(){
         //TODO get from database
         return 1990;
     }

     /**
      *
      * @return the first year for recorded albums
      */
     public static int getLastAlbumYear(){
          //TODO get from database
         return 2011;
     }
}
