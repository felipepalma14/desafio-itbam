package com.felipe.palma.desafioitbam.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Felipe Palma on 03/07/2019.
 */
public class Size implements Serializable {
    @SerializedName("available")
    @Expose
    private boolean available;
    @SerializedName("size")
    @Expose
    private String size;
    @SerializedName("sku")
    @Expose
    private String sku;

    public boolean getAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

}
