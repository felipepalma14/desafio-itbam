package com.felipe.palma.desafioitbam.mvp;

import com.felipe.palma.desafioitbam.model.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Felipe Palma on 03/07/2019.
 */
public class MainPresenterImpl implements MainContract.presenter, MainContract.GetProductIntractor.OnFinishedListener {

    private MainContract.MainView mainView;
    private MainContract.GetProductIntractor mGetProductIntractor;

    public MainPresenterImpl(MainContract.MainView mainView, MainContract.GetProductIntractor getProductIntractor) {
        this.mainView = mainView;
        this.mGetProductIntractor = getProductIntractor;
    }

    @Override
    public void onDestroy() {

        mainView = null;

    }

    @Override
    public void onRefreshButtonClick() {

        if(mainView != null){
            mainView.showProgress();
        }
        mGetProductIntractor.getProductArrayList(this);

    }

    @Override
    public void requestDataFromServer() {
        mGetProductIntractor.getProductArrayList(this);
    }


    @Override
    public void onFinished(List<Product> productArrayList) {
        if(mainView != null){
            mainView.setDataToRecyclerView(productArrayList);
            mainView.hideProgress();
        }
    }

    @Override
    public void onFailure(Throwable t) {
        if(mainView != null){
            mainView.onResponseFailure(t);
            mainView.hideProgress();
        }
    }


}
