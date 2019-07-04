package com.felipe.palma.desafioitbam;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
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

public class MainActivity extends AppCompatActivity  implements MainContract.MainView{

    private ProductAdapter mProductAdapter;
    private MainContract.presenter mPresenter;

    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout = null;

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

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new ProductDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 74));


        onRefresh();

    }


    private void onRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Utils.isNetworkAvailable(getApplicationContext())) {
                    swipeRefreshLayout.setRefreshing(false);
                    //fetchData();
                    mPresenter.requestDataFromServer();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }

            }
        }, 1500));
    }

    private void fetchData() {

    }

    /**
     * RecyclerItem click event listener
     * */
    private RecyclerItemClickListener recyclerItemClickListener = new RecyclerItemClickListener() {
        @Override
        public void onItemClick(Product item) {

            Toast.makeText(MainActivity.this,
                    "List title:  " + item.getName(),
                    Toast.LENGTH_LONG).show();

        }
    };


    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void setDataToRecyclerView(List<Product> productList) {
        mProductAdapter = new ProductAdapter(this, productList);
        Log.d("DATA", productList.size()+"");
        recyclerView.setAdapter(mProductAdapter);
    }


    @Override
    public void onResponseFailure(Throwable throwable) {
        Toast.makeText(MainActivity.this,
                "Algo deu errado: " + throwable.getMessage(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mPresenter.onDestroy();
    }
}
