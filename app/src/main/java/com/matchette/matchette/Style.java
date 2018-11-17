package com.matchette.matchette;

public class Style {
    private String name;
    private int rid;
    private int num;
    private float weight;

    public Style(String name, int rid, int num, float weight) {
        this.name = name;
        this.rid = rid;
        this.num = num;
        this.weight = weight;
    }

    public String getName (){
        return name;
    }

    public int getRid () {
        return rid;
    }

    public int getNum () {
        return num;
    }

    public float getWeight () {
        return weight;
    }
}
