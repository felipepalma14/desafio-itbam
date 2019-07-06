package com.felipe.palma.desafioitbam.model;

/**
 * Created by Felipe Palma on 05/07/2019.
 */
public class CartItem {
    private Product product;
    private int quantity = 0;

    public CartItem() {
    }

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
