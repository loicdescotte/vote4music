package models;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import play.data.validation.Required;

import static play.db.jpa.Model.*;

import play.db.jpa.JPA;
import play.db.jpa.Model;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.*;

/**
 * User: Loic Descotte
 * Date: 28 fevr. 2010
 */

@Entity
public class Album extends Model {

    @Required
    public String name;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    public Artist artist;
    @Temporal(TemporalType.DATE)
    @Required
    public Date releaseDate;
    @Enumerated(EnumType.STRING)
    public Genre genre;
    public long nbVotes = 0L;
    public boolean hasCover = false;

    private static SimpleDateFormat formatYear = new SimpleDateFormat("yyyy");

    public Album(String name) {
        this.name = name;
    }

    /**
     * Remove duplicate artist
     *
     * @return found duplicate artist if exists
     */
    public void replaceDuplicateArtist() {
        Artist existingArtist = Artist.find("byName",artist.name).first();
        if (existingArtist != null) {
            //Artist name is unique
            artist = existingArtist;
        }
    }

    /**
     * Vote for an album
     */
    public void vote() {
        nbVotes++;
        save();
    }

    /**
     * Find albums by genre and year
     *
     * @param genre
     * @param year
     * @return
     */
    public static List<Album> findByGenreAndYear(String genre, String year) {
        List<Album> albums;
        Genre genreEnum = Genre.valueOf(genre.toString().toUpperCase());
        albums = find("genre = ? order by nbVotes desc", genreEnum).fetch(100);
        //labmdaj example
        albums = filterByYear(albums, year);
        return albums;
    }

    /**
     * LambdaJ example : Filter by year
     *
     * @param albums
     * @param year
     * @return
     */
    public static List<Album> filterByYear(List<Album> albums, String year) {
        return select(albums, having(on(Album.class).getReleaseYear(), equalTo(year)));
    }

    /**
     * @param filter
     * @return found albums
     */
    public static List<Album> findAll(String filter) {
        String likeFilter = "%" + filter + "%";
        //limit to 100 results
        List<Album> albums = find("byNameLike", likeFilter).fetch(100);
        return albums;
    }

    /**
     * @return release year
     */
    public String getReleaseYear() {
        return formatYear.format(releaseDate);
    }

    /**
     * @return first year for recorded albums
     */
    public static int getFirstAlbumYear() {
        // get a single result via play-jpa gives the wrong result
        Date result = (Date) em().createQuery("select min(a.releaseDate) from Album a").getSingleResult();
        if (result != null)
            return Integer.parseInt(formatYear.format(result));
        //if no album is registered return 1990
        return 1990;
    }

    /**
     * @return last year for recorded albums
     */
    public static int getLastAlbumYear() {
        Date result = (Date) em().createQuery("select max(a.releaseDate) from Album a").getSingleResult();
        if (result != null)
            return Integer.parseInt(formatYear.format(result));
        //if no album is registered return current year
        return Integer.parseInt(formatYear.format(new Date()));

    }
}
