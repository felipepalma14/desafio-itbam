package com.felipe.palma.desafioitbam;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import com.adroitandroid.chipcloud.ChipCloud;
import com.felipe.palma.desafioitbam.model.CartItem;
import com.felipe.palma.desafioitbam.model.Product;
import com.felipe.palma.desafioitbam.model.Size;
import com.felipe.palma.desafioitbam.utilities.CartSingleton;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.squareup.picasso.Picasso;
/**
 * Created by Felipe Palma on 04/07/2019.
 */
public class ProductDetailActivity extends AppCompatActivity {

    private Product mProductItem;
    private int cartItemsCount;

    TextView txt_product_name, txt_product_price, txt_product_installments,txt_product_regular_price,txt_product_discount_percentage;
    //Para Badge
    TextView textCartItemCount;
    ChipCloud chipCloudProductSizes;
    ImageView img_product_image;
    ProgressDialog dialog;
    Button btn_cart;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        if (Config.ENABLE_RTL_MODE) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        getData();
        initComponent();
        displayData();
        setupToolbar();
        setupSizesProduct();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cartItemsCount = CartSingleton.getInstance().getSizeItems();
    }

    public void setupToolbar() {

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("");
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle("Detalhes do Produto");
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });
    }

    public void getData() {
        Intent intent = getIntent();
        mProductItem = (Product) intent.getSerializableExtra("PRODUCT_ITEM");


    }

    public void initComponent() {
        txt_product_name = findViewById(R.id.product_name);
        img_product_image = findViewById(R.id.product_image);
        txt_product_price = findViewById(R.id.product_price);
        txt_product_regular_price = findViewById(R.id.product_regular_price);
        txt_product_discount_percentage = findViewById(R.id.product_discount_percentage);
        txt_product_installments = findViewById(R.id.product_installments);
        chipCloudProductSizes = findViewById(R.id.product_sizes);
        btn_cart = findViewById(R.id.btn_add_cart);
    }

    public void displayData() {
        txt_product_name.setText(mProductItem.getName());
        if (mProductItem.getImage().isEmpty()){
            Picasso.with(this)
                    .load(R.drawable.ic_no_image)
                    .placeholder(R.drawable.ic_loading)
                    .error(R.drawable.ic_no_image)
                    .resize(250, 250)
                    .centerCrop()
                    .into(img_product_image);
        }else {
            Picasso.with(this)
                    .load(mProductItem.getImage())
                    .placeholder(R.drawable.ic_loading)
                    .error(R.drawable.ic_no_image)
                    .resize(250, 250)
                    .centerCrop()
                    .into(img_product_image);
        }

        img_product_image.setOnClickListener(view -> {
        });

        txt_product_price.setText(mProductItem.getActualPrice());
        if(!mProductItem.getDiscountPercentage().equals("")) {
            txt_product_regular_price.setText(mProductItem.getRegularPrice());
            txt_product_regular_price.setPaintFlags(txt_product_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            txt_product_discount_percentage.setText(mProductItem.getDiscountPercentage());

        }else{
            LinearLayout mLinearLayout = findViewById(R.id.layout_discount);
            mLinearLayout.setVisibility(View.GONE);
        }
        txt_product_installments.setText(mProductItem.getInstallments());
        if (mProductItem.getOnSale()) {
            btn_cart.setText("Adicionar no Carrinho");
            btn_cart.setBackgroundResource(R.color.available);
            btn_cart.setOnClickListener(v -> inputDialog());
        } else {
            btn_cart.setEnabled(false);
            btn_cart.setText("Desculpe, não temos em estoque");
            btn_cart.setBackgroundResource(R.color.sold);
        }


    }

    public void inputDialog() {
        dialog = ProgressDialog.show(ProductDetailActivity.this, "",
                "Aguarde...", true);

        /*
        Simulação de processamento
         */
        dialog.show();
        new Thread(() -> {

                this.runOnUiThread(() -> {
                    try {
                        Thread.sleep(2000);
                        dialog.dismiss();
                        addItemToCart();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });

        }).start();

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
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.action_cart:
//                CartSingleton.getInstance().getCartItems().add(new CartItem());
//                textCartItemCount.setText(String.valueOf(CartSingleton.getInstance().getSizeItems()));
                return true;
//                Intent intent = new Intent(getApplicationContext(), ActivityCart.class);
//                intent.putExtra("tax", resp_tax);
//                intent.putExtra("currency_code", resp_currency_code);
//                startActivity(intent);
//                break;


            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }


    // REPETIDO
    private void setupBadge() {
        textCartItemCount.setText(String.valueOf(cartItemsCount));
    }

    private void setupSizesProduct(){
        chipCloudProductSizes.setMode(ChipCloud.Mode.REQUIRED);
        for (Size size : mProductItem.getSizes()) {
            if(size.getAvailable()) chipCloudProductSizes.addChip(size.getSize());
        }
        chipCloudProductSizes.setSelectedChip(0);
    }

    private void addItemToCart(){
        CartSingleton.getInstance().getCartItems().add(new CartItem());
        Toast.makeText(getBaseContext(), "Produto inserido no carrinho", Toast.LENGTH_LONG).show();
        startActivity(new Intent(ProductDetailActivity.this, MainActivity.class));

    }
}
