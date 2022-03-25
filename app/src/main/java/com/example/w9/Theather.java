package com.example.w9;

public class Theather {
    String town;
    Integer ID;
    public Theather(){

    }
    public Theather(String town, int ID){
        this.town = town;
        this.ID = ID;
    }
    public String getTown(){
        return this.town;
    }
    public int getID() {
        return this.ID;
    }
}

