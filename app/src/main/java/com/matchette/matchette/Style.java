package com.matchette.matchette;

public class Style {
    private String name;
    private int rid;
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
