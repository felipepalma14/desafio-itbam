package com.felipe.palma.desafioitbam;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.felipe.palma.desafioitbam.adapter.ProductAdapter;
import com.felipe.palma.desafioitbam.adapter.decoration.ProductDividerItemDecoration;
import com.felipe.palma.desafioitbam.model.Product;
import com.felipe.palma.desafioitbam.mvp.GetProductsIntractorImp;
import com.felipe.palma.desafioitbam.mvp.MainContract;
import com.felipe.palma.desafioitbam.mvp.MainPresenterImpl;
import com.felipe.palma.desafioitbam.mvp.RecyclerItemClickListener;
import com.felipe.palma.desafioitbam.utilities.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Felipe Palma on 03/07/2019.
 */

public class MainActivity extends AppCompatActivity  implements MainContract.MainView{

    private ProductAdapter mProductAdapter;
    private MainContract.presenter mPresenter;
    private int mCartItemCount = 0;

    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout = null;

    //Para Badge
    TextView textCartItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        INSTANCIA BUTTERKNIFE
         */
        ButterKnife.bind(this);

        mPresenter = new MainPresenterImpl(this, new GetProductsIntractorImp());
        mPresenter.requestDataFromServer();

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new ProductDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 0));

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        onRefresh();

    }


    private void onRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Utils.isNetworkAvailable(getApplicationContext())) {
                    swipeRefreshLayout.setRefreshing(false);

                    mPresenter.requestDataFromServer();

                    Toast.makeText(MainActivity.this,"Atualizando!!!",
                            Toast.LENGTH_LONG).show();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }

            }
        }, 1500));
    }


    /**
     * RecyclerItem click event listener
     * */
    private RecyclerItemClickListener recyclerItemClickListener = new RecyclerItemClickListener() {
        @Override
        public void onItemClick(Product item) {
            Intent mIntent = new Intent(MainActivity.this,ProductDetailActivity.class);
            mIntent.putExtra("PRODUCT_ITEM",item);
            startActivity(mIntent);
        }
    };


    @Override
    public void showProgress() {

    }


    @Override
    public void setDataToRecyclerView(List<Product> productList) {
        mProductAdapter = new ProductAdapter(this, productList, recyclerItemClickListener);
        recyclerView.setAdapter(mProductAdapter);
    }


    @Override
    public void onResponseFailure(Throwable throwable) {
        Toast.makeText(MainActivity.this,
                "Algo deu errado: " + throwable.getMessage(),
                Toast.LENGTH_LONG).show();
        Log.e("ERR", throwable.getMessage());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        final MenuItem menuItem = menu.findItem(R.id.action_cart);

        View actionView = MenuItemCompat.getActionView(menuItem);
        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);

        setupBadge();

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_cart: {
                // Do something
                mCartItemCount+=1;
                textCartItemCount.setText(String.valueOf(mCartItemCount));
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupBadge() {
         textCartItemCount.setText(String.valueOf(mCartItemCount));
//        if (textCartItemCount != null) {
//            if (mCartItemCount == 0) {
//                if (textCartItemCount.getVisibility() != View.GONE) {
//                    textCartItemCount.setVisibility(View.GONE);
//                }
//            } else {
//                textCartItemCount.setText(String.valueOf(Math.min(mCartItemCount, 99)));
//                if (textCartItemCount.getVisibility() != View.VISIBLE) {
//                    textCartItemCount.setVisibility(View.VISIBLE);
//                }
//            }
//        }
    }

}
