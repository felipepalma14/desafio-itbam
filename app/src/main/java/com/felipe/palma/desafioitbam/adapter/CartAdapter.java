package com.felipe.palma.desafioitbam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.felipe.palma.desafioitbam.R;
import com.felipe.palma.desafioitbam.model.Cart;
import com.felipe.palma.desafioitbam.model.Product;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

/**
 * Created by Felipe Palma on 05/07/2019.
 */
public class CartAdapter  extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Context context;
    private Cart cart;

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView productName;
        TextView productQuantity;
        TextView productPrice;
        TextView productSize;
        ImageView productImage;


        public ViewHolder(View view) {
            super(view);
            productName = view.findViewById(R.id.product_name);
            productQuantity = view.findViewById(R.id.product_quantity);
            productPrice = view.findViewById(R.id.product_price);
            productSize = view.findViewById(R.id.product_size);
            productImage = view.findViewById(R.id.product_image);
        }

    }

    public CartAdapter(Context context, Cart cart) {
        this.context = context;
        this.cart = cart;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Product mProductItem = cart.getItems().get(position).getProduct();
        holder.productName.setText(mProductItem.getName());
        holder.productQuantity.setText(String.valueOf(cart.getItems().get(position).getQuantity()));
        holder.productPrice.setText(String.valueOf(cart.getItems().get(position).getProduct().getActualPrice()));
        holder.productSize.setText(cart.getItems().get(position).getSize());


        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(8)
                .oval(false)
                .build();
        if (mProductItem.getImage().isEmpty()) { //url.isEmpty()
            Picasso.with(context)
                    .load(R.drawable.ic_no_image)
                    .placeholder(R.drawable.ic_loading)
                    .resize(50, 50)
                    .centerCrop()
                    .transform(transformation)
                    .into(holder.productImage);

        }else {

            Picasso.with(context)
                    .load(mProductItem.getImage())
                    .placeholder(R.drawable.ic_loading)
                    .resize(150, 150)
                    .centerInside()
                    .transform(transformation)
                    .into(holder.productImage);
        }
    }

    @Override
    public int getItemCount() {
        return cart.getItems().size();
    }

}
