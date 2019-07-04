package com.felipe.palma.desafioitbam;


import android.util.Log;

import androidx.test.runner.AndroidJUnit4;

import com.felipe.palma.desafioitbam.network.IProductsDataSource;
import com.felipe.palma.desafioitbam.network.RetrofitInstance;
import com.felipe.palma.desafioitbam.network.data.ProductResponse;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.junit.Assert.assertTrue;

/**
 * Created by Felipe Palma on 03/07/2019.
 */


@RunWith(AndroidJUnit4.class)
public class GetProductsTest {
    @Test
    public void getProductsFromServer(){
        IProductsDataSource serviceAccess = RetrofitInstance.getInstance().create(IProductsDataSource.class);

        Call<ProductResponse> call = serviceAccess.getProducts();

        try {
            Response<ProductResponse> response = call.execute();
            Log.d("DATA", response.body().getProducts().size()+"");
            assertTrue(response.isSuccessful());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
