package com.felipe.palma.desafioitbam;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.felipe.palma.desafioitbam.adapter.CartAdapter;
import com.felipe.palma.desafioitbam.adapter.decoration.ProductDividerItemDecoration;
import com.felipe.palma.desafioitbam.model.Cart;
import com.felipe.palma.desafioitbam.model.CartItem;
import com.felipe.palma.desafioitbam.utilities.CartSingleton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    View lyt_empty_cart;
    TextView txtTotalPrice;
    RelativeLayout lyt_order;
    View view;

    ProgressDialog dialog;

    CartAdapter mCartAdapter;
    final int CLEAR_ALL_ORDER = 0;
    final int CLEAR_ONE_ORDER = 1;
    int FLAG;
    Button btn_checkout, btn_continue;

    // INSTANCIA DO CARRINHO DE COMPAS
    private Cart mCart = new Cart();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        view = findViewById(android.R.id.content);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Carrinho");
        }

        recyclerView = findViewById(R.id.recycler_view);
        lyt_empty_cart = findViewById(R.id.lyt_empty_history);
        txtTotalPrice = view.findViewById(R.id.txt_total_price);

        btn_checkout = findViewById(R.id.btn_checkout);
        btn_checkout.setOnClickListener(view -> finishPurchase());

        btn_continue = findViewById(R.id.btn_continue);
        btn_continue.setOnClickListener(v -> finish());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new ProductDividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL, 86));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        lyt_order = findViewById(R.id.lyt_history);

        mCartAdapter = new CartAdapter(this, mCart);

        /*
            VERIFICA SE O CARRINHO POSSUI ITENS
            SENAO MOSTRA SACOLA VAZIA
         */
        cartIsEmpty();


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                new Handler().postDelayed(() -> showClearDialog(CLEAR_ONE_ORDER,position), 400);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


    }


    public void finishPurchase() {
        dialog = ProgressDialog.show(CartActivity.this,
                getResources().getText(R.string.txt_process_purchase),
                getResources().getText(R.string.txt_finish_purchase), true);

        /*
        Simulação de processamento
         */
        dialog.show();
        new Thread(() -> this.runOnUiThread(() -> {
            try {
                Thread.sleep(2000);
                dialog.dismiss();
                mCart.getItems().clear();
                finish();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        })).start();

    }

    private void cartIsEmpty() {
        if (mCart.getItems().size() > 0) {
            lyt_order.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(mCartAdapter);
            txtTotalPrice.setText( String.format("R$%.2f",(mCart.getTotalPrice())));
        } else {
            lyt_empty_cart.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;

            case R.id.clear:
                if (mCart.getItems().size() > 0) {
                    showClearDialog(CLEAR_ALL_ORDER, 0);
                } else {
                    Snackbar.make(view, getResources().getText(R.string.txt_empty_cart), Snackbar.LENGTH_SHORT).show();
                }
                mCartAdapter.notifyDataSetChanged();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showClearDialog(int flag, int position) {
        FLAG = flag;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getText(R.string.txt_confirm));
        switch (FLAG) {
            case 0:
                builder.setMessage(getResources().getText(R.string.txt_clear_all_cart));
                break;
            case 1:
                builder.setMessage("Remover item: " +  mCart.getItems().get(position).getProduct().getName());
                break;
        }
        builder.setCancelable(false);
        builder.setPositiveButton("Sim", (dialog, which) -> {
            switch (FLAG) {
                case 0:
                    clearData();
                    break;
                case 1:
                    clearItem(position);
                    break;
            }
        });

        builder.setNegativeButton("Não", (dialog, which) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();

    }

    public void clearData() {
        mCart.getItems().clear();
        mCartAdapter.notifyDataSetChanged();
        cartIsEmpty();
    }

    public void clearItem(int position) {
        mCart.getItems().remove(position);
        mCartAdapter.notifyDataSetChanged();
        cartIsEmpty();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {

            this.clickListener = clickListener;

            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }
}
