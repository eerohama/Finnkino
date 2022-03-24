package com.example.finnkino;

public class Theater {
    private String name;
    private int ID;

    public Theater(String s, int i){
        this.name = s;
        this.ID = i;
    }

    public String getName(){
        return name;
    }
    public int getID(){
        return ID;
    }

}
