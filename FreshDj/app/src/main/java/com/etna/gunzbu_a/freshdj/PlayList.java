package com.etna.gunzbu_a.freshdj;

/**
 * Created by SuChi on 31/08/2016.
 */
public class PlayList {
    private String id;
    private String name;
    private String ispublic;

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getIspublic() {
        return ispublic;
    }

    public void setId(String id) {
        this.name = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setIspublic(String ispublic) {
        this.ispublic = ispublic;
    }


    public PlayList (String name, String ispublic, String id){
        this.name = name;
        this.ispublic = ispublic;
        this.id = id;
    }
}
