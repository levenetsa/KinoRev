package com.levenetsa.fetcher.entity;

import java.sql.Timestamp;

public class Film {
    private Integer id;
    private String name;
    private Timestamp lastFetched;

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

    public String toJsonp() {
        return "[\"" + name + "\", \"" + id + "\"]";
    }
}
