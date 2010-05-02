package models;

import play.db.jpa.Model;

/**
 * User: Loic Descotte
 * Date: 2 mai 2010
 */
public class Vote extends Model{

    public Album album;
    //TODO validation -> <=10
    public Integer rate;
}
