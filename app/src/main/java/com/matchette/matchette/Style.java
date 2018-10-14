package com.matchette.matchette;

public class Style {
    private String name;
    private int rid;

    public Style(String name, int rid) {
        this.name = name;
        this.rid = rid;
    }

    public String getName (){
        return name;
    }

    public int getRid () {
        return rid;
    }
}
