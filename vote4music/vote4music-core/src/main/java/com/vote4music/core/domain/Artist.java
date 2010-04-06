package com.vote4music.core.domain;

import org.springframework.beans.factory.annotation.Required;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * User: Loic Descotte
 * Date: 5 avr. 2010
 */
@Entity
public class Artist {
    @Column(unique = true)
    public String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
