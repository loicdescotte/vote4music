package models;

import play.db.jpa.Model;

import javax.persistence.Entity;

/**
 * User: Loic Descotte
 * Date: 28 févr. 2010
 */
@Entity
public class Genre extends Model{
    public String name;
    public String displayName;
}
