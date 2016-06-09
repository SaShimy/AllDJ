package com.etna.gunzbu_a.freshdj;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Kevin_Tan on 09/06/2016.
 */
public interface ApiCalls {
    @GET("/room/all/")
    Call<List<Room>> rooms();
}
