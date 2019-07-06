package com.felipe.palma.desafioitbam;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    View lyt_empty_cart;
    RelativeLayout lyt_order;
    CartAdapter mCartAdapter;
    double total_price;
    final int CLEAR_ALL_ORDER = 0;
    final int CLEAR_ONE_ORDER = 1;
    int FLAG;
    int ID;
    double str_tax;
    String str_currency_code;
    Button btn_checkout, btn_continue;
    ArrayList<ArrayList<Object>> data;
    public static ArrayList<Integer> product_id = new ArrayList<Integer>();
    public static ArrayList<String> product_name = new ArrayList<String>();
    public static ArrayList<Integer> product_quantity = new ArrayList<Integer>();
    public static ArrayList<String> currency_code = new ArrayList<String>();
    public static ArrayList<Double> sub_total_price = new ArrayList<Double>();
    public static ArrayList<String> product_image = new ArrayList<String>();

    List<Cart> arrayCart;
    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        view = findViewById(android.R.id.content);

        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Carrinho");
        }

        Intent intent = getIntent();
        str_tax = intent.getDoubleExtra("tax", 0);
        str_currency_code = intent.getStringExtra("currency_code");

        recyclerView = findViewById(R.id.recycler_view);
        lyt_empty_cart = findViewById(R.id.lyt_empty_history);
        btn_checkout = findViewById(R.id.btn_checkout);
        btn_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(CartActivity.this, ActivityCheckout.class);
//                intent.putExtra("tax", str_tax);
//                intent.putExtra("currency_code", str_currency_code);
//                startActivity(intent);
            }
        });
        btn_continue = findViewById(R.id.btn_continue);
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new ProductDividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL, 86));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        lyt_order = (RelativeLayout) findViewById(R.id.lyt_history);

        mCartAdapter = new CartAdapter(this, arrayCart);


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showClearDialog(CLEAR_ONE_ORDER, product_id.get(position));
                    }
                }, 400);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        new getDataTask().execute();

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
                if (product_id.size() > 0) {
                    showClearDialog(CLEAR_ALL_ORDER, 1111);
                } else {
                    Snackbar.make(view, "Carrinho Vazio", Snackbar.LENGTH_SHORT).show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showClearDialog(int flag, int id) {
        FLAG = flag;
        ID = id;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar");
        switch (FLAG) {
            case 0:
                builder.setMessage("Limpar pedido");
                break;
            case 1:
                builder.setMessage("Remover item");
                break;
        }
        builder.setCancelable(false);
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (FLAG) {
                    case 0:
                        clearData();
                        new getDataTask().execute();
                        break;
                    case 1:
                        clearData();
                        new getDataTask().execute();
                        break;
                }
            }
        });

        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public void clearData() {
        product_id.clear();
        product_name.clear();
        product_quantity.clear();
        sub_total_price.clear();
        currency_code.clear();
        product_image.clear();
    }

    public class getDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            getDataFromDatabase();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            String _price = String.format(Locale.GERMAN, "%1$,.0f", total_price);
            String _tax = String.format(Locale.GERMAN, "%1$,.0f", str_tax);

            TextView txt_total_price = findViewById(R.id.txt_total_price);
            TextView txt_tax = findViewById(R.id.txt_tax);

//            if (Config.ENABLE_DECIMAL_ROUNDING) {
//                txt_total_price.setText(getResources().getString(R.string.txt_total) + " " + _price + " " + str_currency_code);
//                txt_tax.setText(getResources().getString(R.string.txt_tax) + " " + _tax + " %");
//            } else {
//                txt_total_price.setText(getResources().getString(R.string.txt_total) + " " + total_price + " " + str_currency_code);
//                txt_tax.setText(getResources().getString(R.string.txt_tax) + " " + str_tax + " %");
//            }

            if (product_id.size() > 0) {
                lyt_order.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(mCartAdapter);
            } else {
                lyt_empty_cart.setVisibility(View.VISIBLE);
            }

        }
    }

    public void getDataFromDatabase() {

        total_price = 0;
        clearData();

        for (int i = 0; i < data.size(); i++) {
            ArrayList<Object> row = data.get(i);

            product_id.add(Integer.parseInt(row.get(0).toString()));
            product_name.add(row.get(1).toString());
            product_quantity.add(Integer.parseInt(row.get(2).toString()));
            sub_total_price.add(Double.parseDouble(row.get(3).toString()));

            total_price += sub_total_price.get(i);

            currency_code.add(row.get(4).toString());
            product_image.add(row.get(5).toString());
        }

        total_price += (total_price * (str_tax / 100));

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

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {

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
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }
}
