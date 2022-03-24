package com.example.finnkino;

import java.util.ArrayList;

public class TheaterList {
    private ArrayList<Theater> list;
    private static TheaterList instance = null;

    public TheaterList(){
        this.list = new ArrayList<Theater>();
    }

    public static TheaterList getInstance(){
        if(instance == null){
            instance = new TheaterList();
        }
        return instance;
    }
    public int getID(String theater){
        int id = 0;
        for(Theater t: list){
            if(t.getName().equals(theater)) {
                id = t.getID();
            }
        }
        return id;
    }
    public void addToList(Theater t){
        list.add(t);
    }

    public ArrayList<Theater> getList(){
        return list;
    }
}
