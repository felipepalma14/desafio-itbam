package com.felipe.palma.desafioitbam.utilities;

import com.felipe.palma.desafioitbam.model.CartItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Felipe Palma on 05/07/2019.
 */
public class CartSingleton {
    private static CartSingleton mCartSingleton;

    private static List<CartItem> mCartItems = new ArrayList<>();

    //private constructor.
    private CartSingleton(){

        //Prevent form the reflection api.
        if (mCartSingleton != null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }


    public static CartSingleton getInstance(){
        if (mCartSingleton == null){ //if there is no instance available... create new one
            mCartSingleton = new CartSingleton();
        }

        return mCartSingleton;
    }


    public List<CartItem> getCartItems(){
        return mCartItems;
    }

    public int getSizeItems(){
        if(mCartItems.size() > 0) {
            return mCartItems.size();
        }
        return 0;
    }
}
