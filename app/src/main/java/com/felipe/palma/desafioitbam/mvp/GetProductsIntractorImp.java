package com.felipe.palma.desafioitbam.mvp;

import android.util.Log;

import com.felipe.palma.desafioitbam.network.IProductsDataSource;
import com.felipe.palma.desafioitbam.network.RetrofitInstance;
import com.felipe.palma.desafioitbam.network.data.ProductResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Felipe Palma on 03/07/2019.
 */
public class GetProductsIntractorImp implements MainContract.GetProductIntractor{

    @Override
    public void getProductArrayList(OnFinishedListener onFinishedListener) {
        /** Create handle for the RetrofitInstance interface*/
        IProductsDataSource service = RetrofitInstance.getInstance().create(IProductsDataSource.class);

        /** Call the method with parameter in the interface to get the notice data*/
        Call<ProductResponse> call = service.getProducts();

        /**Log the URL called*/
        Log.d("URL Called", call.request().url() + "");

        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                onFinishedListener.onFinished(response.body().getProducts());

            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                onFinishedListener.onFailure(t);
            }
        });

    }
}
