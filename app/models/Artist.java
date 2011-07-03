package models;

import com.google.code.morphia.annotations.Entity;
import play.data.validation.Required;
import play.modules.morphia.Model;

/**
 * User: Loic Descotte
 * Date: 28 fevr. 2010
 */
@Entity
public class Artist extends Model{
    @Required
    public String name;

    public Artist(String name) {
        this.name = name;
    }

}
