package models;

import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * User: Loic Descotte
 * Date: 28 févr. 2010
 */
@Entity
public class Artist extends Model{
    @Required
    @Column(unique = true)
    public String name;

    public Artist(String name) {
        this.name = name;
    }
}
