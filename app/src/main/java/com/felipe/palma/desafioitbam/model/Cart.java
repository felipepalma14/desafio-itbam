package com.felipe.palma.desafioitbam.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Felipe Palma on 05/07/2019.
 */
public class Cart implements Serializable {
    private List<Product> items;

    public Cart() {
    }

    public Cart(List<Product> items) {
        this.items = items;
    }

    public List<Product> getItems() {
        return items;
    }

    public void addItem(Product item){
        this.getItems().add(item);
    }


    public void removeItem(Product item){
        this.getItems().remove(item);
    }
}
