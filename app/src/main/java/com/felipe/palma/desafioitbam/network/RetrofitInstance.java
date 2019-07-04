package com.felipe.palma.desafioitbam.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Felipe Palma on 03/07/2019.
 */
public class RetrofitInstance {

    private static Retrofit mRetrofit;

    private static final String BASE_URL = "http://www.mocky.io/v2/";

    /*
    CRIANDO INSTANCIA DO RETROFIT
     */
    public static Retrofit getInstance(){
        if(mRetrofit == null){
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return mRetrofit;
    }
}
