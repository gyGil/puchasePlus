package com.example.administrator.myapplication;

//================================================//
// NAME : List
// PURPOSE : list for database
//================================================//
public class List {

    //====================================[ MEMBERS ]====================================//
    private int id;
    private String name;

    //=================================[ CONSTRUCTORS ]=================================//
    public List() {}

    public List(String name) { this.name = name; }

    public List(int id, String name){
        this.id = id;
        this.name = name;
    }

    //================================[ SETTER/ GETTER ]================================//
    public int getId() {return  id; }
    public void setId(int id) {this.id = id; }
    public String getName() {return name; }
    public void setName(String name) {this.name = name; }
}
