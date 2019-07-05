package com.felipe.palma.desafioitbam;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.felipe.palma.desafioitbam.model.Product;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    private Product mProductItem;

    TextView txt_product_name, txt_product_price, txt_product_installments, txt_product_regular_price,txt_product_discount_percentage;
    ImageView img_product_image;
    Button btn_cart;
    final Context context = this;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        getData();
        initComponent();
        displayData();
        setupToolbar();
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
                    collapsingToolbarLayout.setTitle("TEste CAtegoria");
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

        img_product_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
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


        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);

        View mView = layoutInflaterAndroid.inflate(R.layout.input_dialog, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setView(mView);

        final EditText edtQuantity = mView.findViewById(R.id.userInputDialog);
        alert.setCancelable(false);
        int maxLength = 3;
        edtQuantity.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        edtQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);

        alert.setPositiveButton("ADD", (dialog, whichButton) -> {
            String temp = edtQuantity.getText().toString();
            int quantity = 0;

            if (!temp.equalsIgnoreCase("")) {



            } else {
                dialog.cancel();
            }
        });

        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alert.create();
        alertDialog.show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.cart:
//                Intent intent = new Intent(getApplicationContext(), ActivityCart.class);
//                intent.putExtra("tax", resp_tax);
//                intent.putExtra("currency_code", resp_currency_code);
//                startActivity(intent);
                break;

            case R.id.share:
                //requestStoragePermission();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }
/*
    @TargetApi(16)
    private void requestStoragePermission() {
        Dexter.withActivity(ActivityProductDetail.this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            (new ShareTask(ActivityProductDetail.this)).execute(Config.ADMIN_PANEL_URL + "/upload/product/" + product_image);
                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }
*/
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetailActivity.this);
        builder.setTitle("PERMISSAO");
        builder.setMessage("PERMISSAO");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }


}
