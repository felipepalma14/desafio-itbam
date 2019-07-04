package com.felipe.palma.desafioitbam.network;

import com.felipe.palma.desafioitbam.network.data.ProductResponse;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Felipe Palma on 03/07/2019.
 */
public interface IProductsDataSource {

    @GET("59b6a65a0f0000e90471257d")
    Call<ProductResponse> getProducts();
}
