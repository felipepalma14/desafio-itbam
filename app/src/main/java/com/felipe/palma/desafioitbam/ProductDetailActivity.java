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
import com.adroitandroid.chipcloud.ChipListener;
import com.felipe.palma.desafioitbam.model.Cart;
import com.felipe.palma.desafioitbam.model.CartItem;
import com.felipe.palma.desafioitbam.model.Product;
import com.felipe.palma.desafioitbam.model.Size;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.squareup.picasso.Picasso;
/**
 * Created by Felipe Palma on 04/07/2019.
 */
public class ProductDetailActivity extends AppCompatActivity {

    private Product mProductItem;
    private CartItem mCartItem = new CartItem();
    private Cart mCart = new Cart();

    private int cartItemsCount;

    TextView txt_product_name, txt_product_price, txt_product_installments,txt_product_regular_price,txt_product_discount_percentage;
    //Para Badge
    TextView textCartItemCount;
    ChipCloud chipCloudProductSizes;
    ImageView img_product_image;
    ProgressDialog dialog;
    Button btn_cart;

    private CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);


        getData();
        initComponent();
        displayData();
        setupToolbar();
        setupSizesProduct();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cartItemsCount = mCart.getItems().size();
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
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
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
        mProductItem = (Product) intent.getSerializableExtra(Config.PRODUCT_ITEM);


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
                    //.fit()
                    .into(img_product_image);
        }else {
            Picasso.with(this)
                    .load(mProductItem.getImage())
                    .placeholder(R.drawable.ic_loading)
                    .error(R.drawable.ic_no_image)
                    .fit()
                    //.centerCrop()

                    .into(img_product_image);
        }

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
            btn_cart.setText(getResources().getText(R.string.txt_add_cart));
            btn_cart.setBackgroundResource(R.color.available);
            btn_cart.setOnClickListener(v -> inputDialog());
        } else {
            btn_cart.setEnabled(false);
            btn_cart.setText(getResources().getText(R.string.txt_oput_of_stock));
            btn_cart.setBackgroundResource(R.color.sold);
        }


    }

    public void inputDialog() {
        dialog = ProgressDialog.show(ProductDetailActivity.this, "Adicionando Produto",
                "Aguarde ...", true);

        /*
        Simulação de processamento
         */
        //dialog.show();
        new Thread(() -> this.runOnUiThread(() -> {
            try {
                Thread.sleep(2000);
                dialog.dismiss();
                addItemToCart();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        })).start();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        final MenuItem menuItem = menu.findItem(R.id.action_cart);

        View actionView = MenuItemCompat.getActionView(menuItem);
        textCartItemCount = actionView.findViewById(R.id.cart_badge);

        setupBadge();

        actionView.setOnClickListener(v -> onOptionsItemSelected(menuItem));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        Intent intent;
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                break;

            case R.id.action_cart:
                intent = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intent);
                return true;


            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
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

    private void setupSizesProduct(){
        chipCloudProductSizes.setMode(ChipCloud.Mode.REQUIRED);
        for (Size size : mProductItem.getSizes()) {
            if(size.getAvailable()) chipCloudProductSizes.addChip(size.getSize());
        }
        chipCloudProductSizes.setSelectedChip(0);
        mCartItem.setSize(mProductItem.getSizes().get(0).getSize());

        chipCloudProductSizes.setChipListener(new ChipListener() {
            @Override
            public void chipSelected(int index) {
                //...
                mCartItem.setSize(mProductItem.getSizes().get(index).getSize());

            }
            @Override
            public void chipDeselected(int index) {
                //...
            }
        });
    }

    private void addItemToCart(){
        mCartItem.setProduct(mProductItem);
        mCartItem.setQuantity(1);

        mCart.addItem(mCartItem);
        Toast.makeText(getBaseContext(), "Produto inserido no carrinho", Toast.LENGTH_LONG).show();
        startActivity(new Intent(ProductDetailActivity.this, MainActivity.class));

    }
}
