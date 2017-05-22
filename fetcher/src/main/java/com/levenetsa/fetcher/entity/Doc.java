package com.levenetsa.fetcher.entity;

public class Doc {
    private String description;
    private String[] words;

    public Doc(String description, String[] words){
        this.description = description;
        this.words = words;
    }
}
