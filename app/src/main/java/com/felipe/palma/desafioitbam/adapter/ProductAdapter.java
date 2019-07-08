package com.felipe.palma.desafioitbam.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.felipe.palma.desafioitbam.R;
import com.felipe.palma.desafioitbam.model.Product;
import com.felipe.palma.desafioitbam.mvp.RecyclerItemClickListener;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.mohan.ribbonview.RibbonView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Felipe Palma on 03/07/2019.
 */
public class ProductAdapter  extends RecyclerView.Adapter<ProductAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<Product> productList;
    private List<Product> productListFiltered;
    private RecyclerItemClickListener listener;



    class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productRegularPrice;
        ImageView productImage;
        RibbonView productDiscount;

        ViewHolder(View view) {
            super(view);
            productName = view.findViewById(R.id.product_name);
            productPrice = view.findViewById(R.id.product_price);
            productRegularPrice = view.findViewById(R.id.product_regular_price);
            productImage = view.findViewById(R.id.product_image);
            productDiscount = view.findViewById(R.id.product_ribbon_discount_percentage);

            view.setOnClickListener(view1 -> listener.onItemClick(productListFiltered.get(getAdapterPosition())));
        }
    }

    public ProductAdapter(Context context, List<Product> productList, RecyclerItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.productList = productList;
        this.productListFiltered = productList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Product product = productListFiltered.get(position);
        String productName = product.getName().substring(0,1) + product.getName().toLowerCase().substring(1,product.getName().length());

        holder.productName.setText(productName);
        holder.productPrice.setText(product.getActualPrice());



        if(!product.getDiscountPercentage().equals("") && product.getOnSale()) {
            holder.productRegularPrice.setText(product.getRegularPrice());
            holder.productRegularPrice.setPaintFlags(holder.productRegularPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.productDiscount.setText(product.getDiscountPercentage());
            holder.productDiscount.setRibbonFillColor(context.getResources().getColor(R.color.txt_price_color));
        }else{
            if (!product.getOnSale()){
                holder.productDiscount.setText("Sem Stock");
                holder.productDiscount.setRibbonFillColor(context.getResources().getColor(R.color.grey));
            }else {
                holder.productRegularPrice.setVisibility(View.INVISIBLE);
                holder.productDiscount.setVisibility(View.GONE);
            }
        }





        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(20)
                .oval(false)
                .build();

        if (product.getImage().isEmpty()) { //url.isEmpty()
            Picasso.with(context)
                    .load(R.drawable.ic_no_image)
                    .placeholder(R.drawable.ic_loading)
                    .error(R.drawable.ic_no_image)
                    .resize(200, 200)
                    .centerCrop()
                    .transform(transformation)
                    .into(holder.productImage);

        }else{
            Picasso.with(context)
                    .load(product.getImage())
                    .placeholder(R.drawable.ic_loading)
                    .error(R.drawable.ic_no_image)
                    //.resize(200, 200)
                    .centerInside()
                    .fit()
                    .transform(transformation)
                    .into(holder.productImage);
        }



    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    productListFiltered = productList;
                } else {
                    List<Product> filteredList = new ArrayList<>();
                    for (Product product : productList) {
                        if (product.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(product);
                        }
                    }
                    productListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = productListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                productListFiltered = (ArrayList<Product>) filterResults.values;

                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemCount() {
        return productListFiltered.size();
    }


}

