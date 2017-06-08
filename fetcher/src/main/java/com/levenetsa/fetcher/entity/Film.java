package com.levenetsa.fetcher.entity;

import java.sql.Timestamp;

public class Film {
    private Integer id;
    private String name;
    private Timestamp lastFetched;
    private String cast;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getlastFetched() {
        return lastFetched;
    }

    public void setLastFetched(Timestamp lastFetched) {
        this.lastFetched = lastFetched;
    }

    public String getCast() {
        return cast;
    }

    public void setCast(String cast) {
        this.cast = cast;
    }

    public String toJsonp() {
        return "[\"" + name + "\", \"" + id + "\"]";
    }
}
