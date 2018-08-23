package br.com.tutorialandroid.splashscreen.domain;

/**
 * Created by Renato Almeida on 09/06/2017.
 */

public class User {
    private int id;

    public User(int id){
        this.id = id;
    }

    public int getId() {
        return (id);
    }

    public void setId(long id) {
        this.id = (int) id;
    }
}

