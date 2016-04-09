package com.udacity.kssand.githubcommits;

/**
 * Created by kssand on 08-Apr-16.
 */
public class Committer {
    private String name;
    private String messsage;
    private String date;


    public Committer(String name, String messsage, String date) {
        this.name = name;
        this.messsage = messsage;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getMesssage() {
        return messsage;
    }

    public String getDate() {
        return date;
    }
}
