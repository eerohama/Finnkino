package com.example.finnkino;

import java.util.ArrayList;

public class Movies {
    private ArrayList<String> list;
    private String header;
    private static Movies instance = null;

    public Movies(){
        this.list = new ArrayList<String>();
    }

    public static Movies getInstance(){
        if(instance == null){
            instance = new Movies();
        }
        return instance;
    }

    public void addToList(String s){
        list.add(s);
    }

    public ArrayList<String> getList(){
        return list;
    }

    public void clearList(){
        list.clear();
    }

    public void setHeader(String s){
        this.header = s;
    }

    public String getHeader(){
        return header;
    }
}
