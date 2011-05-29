package models;

import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.List;

/**
 * User: Loic Descotte
 * Date: 28 fevr. 2010
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
