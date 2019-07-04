package com.felipe.palma.desafioitbam.network.data;

import com.felipe.palma.desafioitbam.model.Product;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Felipe Palma on 03/07/2019.
 */
public class ProductResponse implements Serializable {
    @SerializedName("products")
    @Expose
    private List<Product> products = null;

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

}
