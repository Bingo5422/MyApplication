package com.example.myapplication.Bean;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class FriendsBean {
    @PrimaryKey(autoGenerate = true)

    private int id;
    private String path;
    private String name;


    public String getPath() {
        return path;
    }

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
