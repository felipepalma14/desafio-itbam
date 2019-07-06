package com.felipe.palma.desafioitbam.mvp;

import com.felipe.palma.desafioitbam.model.Product;



import java.util.List;

/**
 * Created by Felipe Palma on 03/07/2019.
 */

public interface MainContract {

    /**
     * Call when user interact with the view and other when view OnDestroy()
     * */
    interface presenter{

        void onDestroy();

        void onRefreshButtonClick();

        void requestDataFromServer();

    }

    /**
     * showProgress() and hideProgress() would be used for displaying and hiding the progressBar
     * while the setDataToRecyclerView and onResponseFailure is fetched from the GetNoticeInteractorImpl class
     **/
    interface MainView {

        void showProgress();

        void setDataToRecyclerView(List<Product> productList);

        void onResponseFailure(Throwable throwable);

    }

    /**
     * Intractors are classes built for fetching data from your database, web services, or any other data source.
     **/
    interface GetProductIntractor {

        interface OnFinishedListener {
            void onFinished(List<Product> noticeArrayList);
            void onFailure(Throwable t);
        }

        void getProductArrayList(OnFinishedListener onFinishedListener);
    }
}