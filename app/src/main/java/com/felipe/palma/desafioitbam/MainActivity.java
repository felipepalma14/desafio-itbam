package com.felipe.palma.desafioitbam;

import android.app.SearchManager;
import android.content.Context;
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
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.felipe.palma.desafioitbam.adapter.ProductAdapter;
import com.felipe.palma.desafioitbam.adapter.decoration.ProductDividerItemDecoration;
import com.felipe.palma.desafioitbam.model.Cart;
import com.felipe.palma.desafioitbam.model.Product;
import com.felipe.palma.desafioitbam.mvp.GetProductsIntractorImp;
import com.felipe.palma.desafioitbam.mvp.MainContract;
import com.felipe.palma.desafioitbam.mvp.MainPresenterImpl;
import com.felipe.palma.desafioitbam.mvp.RecyclerItemClickListener;
import com.felipe.palma.desafioitbam.utilities.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Felipe Palma on 03/07/2019.
 */

public class MainActivity extends AppCompatActivity  implements MainContract.MainView{

    private MainContract.presenter mPresenter;
    private int cartItemsCount;
    private ProductAdapter mProductAdapter;

    private Cart mCart = new Cart();

    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout = null;
    private SearchView searchView;

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
        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            if (Utils.isNetworkAvailable(getApplicationContext())) {
                swipeRefreshLayout.setRefreshing(false);

                mPresenter.requestDataFromServer();

                Toast.makeText(MainActivity.this,"Atualizando!!!",
                        Toast.LENGTH_LONG).show();
            } else {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            }

        }, 1500));
    }


    /**
     * RecyclerItem click event listener
     * */
    private RecyclerItemClickListener recyclerItemClickListener = item -> {
        Intent mIntent = new Intent(MainActivity.this,ProductDetailActivity.class);
        mIntent.putExtra(Config.PRODUCT_ITEM,item);
        startActivity(mIntent);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        cartItemsCount = mCart.getItems().size();
        setupBadge();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);

        final MenuItem menuItem = menu.findItem(R.id.action_cart);

        View actionView = MenuItemCompat.getActionView(menuItem);
        textCartItemCount = actionView.findViewById(R.id.cart_badge);

        setupBadge();

        setupSearchView(menu);


        actionView.setOnClickListener(v -> onOptionsItemSelected(menuItem));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_cart: {
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intent);

                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupBadge() {
        if (textCartItemCount != null) {
            if (cartItemsCount == 0) {
                if (textCartItemCount.getVisibility() != View.GONE) {
                    textCartItemCount.setVisibility(View.GONE);
                }
            } else {
                textCartItemCount.setText(String.valueOf(cartItemsCount));
                if (textCartItemCount.getVisibility() != View.VISIBLE) {
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }


    private void setupSearchView(Menu menu) {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mProductAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mProductAdapter.getFilter().filter(query);
                return false;
            }
        });
    }

}
