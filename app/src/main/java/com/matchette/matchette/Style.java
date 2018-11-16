package com.matchette.matchette;

public class Style {
    private String name;
    private int rid;
    private int num;

    public Style(String name, int rid, int num) {
        this.name = name;
        this.rid = rid;
        this.num = num;
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
}
