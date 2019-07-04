package com.felipe.palma.desafioitbam.adapter;

import android.content.Context;
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView product_name, product_price;
        public ImageView product_image;

        public ViewHolder(View view) {
            super(view);
            product_name = view.findViewById(R.id.product_name);
            product_price = view.findViewById(R.id.product_price);
            product_image = view.findViewById(R.id.category_image);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(productListFiltered.get(getAdapterPosition()));
                }
            });
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
        holder.product_name.setText(productName);

            String price = product.getActualPrice();//String.format(Locale.ROOT, "%,d", product.getActualPrice());
            holder.product_price.setText(price);


        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(20)
                .oval(false)
                .build();

        if (product.getImage().isEmpty()) { //url.isEmpty()
            Picasso.with(context)
                    .load(R.drawable.ic_no_image)
                    .placeholder(R.drawable.ic_loading)
                    .error(R.drawable.ic_no_image)
                    .resize(250, 250)
                    .centerCrop()
                    .transform(transformation)
                    .into(holder.product_image);

        }else{
            Picasso.with(context)
                    .load(product.getImage())
                    .placeholder(R.drawable.ic_loading)
                    .error(R.drawable.ic_no_image)
                    .resize(250, 250)
                    .centerCrop()
                    .transform(transformation)
                    .into(holder.product_image);
        }



    }

    @Override
    public int getItemCount() {
        return productListFiltered.size();
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
                    for (Product row : productList) {
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
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

}

