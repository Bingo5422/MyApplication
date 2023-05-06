package com.example.myapplication.Bean;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Random;

@Entity(indices = {@Index(value = {"email","name"},unique = true)})
public class FriendsBean {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String path;
    private String name;

    private String email;


    public FriendsBean() {
    }

    public String getPath() {
        return path;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

//    public FriendsBean(String path, String name, String email) {
//        this.path = path;
//        this.name = name;
//        this.email = email;
//    }


    public void setPath(String path) {
        this.path = path;
    }




    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "FriendsBean{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", name='" + name + '\'' +
                '}';
    }


}
