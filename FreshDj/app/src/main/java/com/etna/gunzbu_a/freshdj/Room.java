package com.etna.gunzbu_a.freshdj;

/**
 * Created by Kevin_Tan on 09/06/2016.
 */
public class Room {
    private int ownerId;
    private String name;
    private int nbFollowers;

    public int getOwnerId() {

        return ownerId;
    }

    public String getName() {
        return name;
    }

    public int getNbFollowers() {
        return nbFollowers;
    }

    public void setOwnerId(int owner_id) {
        this.ownerId = owner_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNbFollowers(int nb_followers) {
        this.nbFollowers = nb_followers;
    }
}
