package com.matchette.matchette;

/**
 * This class represents a clothing style.
 */

public class Style {
    private String name;
    private int rid; // id of style in R
    private float weight;

    public Style(String name, int rid, float weight) {
        this.name = name;
        this.rid = rid;
        this.weight = weight;
    }

    public String getName (){
        return name;
    }

    public int getRid () {
        return rid;
    }

    public float getWeight () {
        return weight;
    }
}
