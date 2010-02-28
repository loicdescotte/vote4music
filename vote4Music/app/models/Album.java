package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import java.util.Date;

/**
 * User: Loic Descotte
 * Date: 28 févr. 2010
 */
@Entity
public class Album extends Model {
    public String name;
    public Artist artist;
    public Date releaseDate;
    public Genre genre;
}
