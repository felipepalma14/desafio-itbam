package com.felipe.palma.desafioitbam.model;

import com.felipe.palma.desafioitbam.utilities.CartSingleton;
import com.felipe.palma.desafioitbam.utilities.Utils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Felipe Palma on 05/07/2019.
 */
public class Cart implements Serializable {
    private List<CartItem> items;


    public Cart() {
        CartSingleton.getInstance();
    }

    public List<CartItem> getItems() {
        return CartSingleton.getInstance().getCartItems();
    }

    public void addItem(CartItem item){
        this.getItems().add(item);
    }

    public double getTotalPrice(){
        double totalPrice = 0;
        if(this.getItems().size()>0){
            for (CartItem item: this.getItems()) {
                totalPrice+= Utils.stringToValue(item.getProduct().getActualPrice());
            }
        }
        return totalPrice;
    }

    public void removeItem(Product item){
        this.getItems().remove(item);
    }
}
