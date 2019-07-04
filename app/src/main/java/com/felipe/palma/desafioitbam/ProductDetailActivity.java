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
import android.widget.TextView;
import android.widget.Toast;

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


    long product_id;
    TextView txt_product_name, txt_product_price, txt_product_quantity;
    private String product_name, product_image, category_name, product_status, currency_code, product_description;
    private double product_price;
    private int product_quantity;
    WebView txt_product_description;
    ImageView img_product_image;
    Button btn_cart;
    final Context context = this;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    double resp_tax;
    String resp_currency_code;

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
        //makeJsonObjectRequest();
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
                    collapsingToolbarLayout.setTitle(category_name);
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
        product_id = intent.getLongExtra("product_id", 0);
        product_name = intent.getStringExtra("title");
        product_image = intent.getStringExtra("image");
        product_price = intent.getDoubleExtra("product_price", 0);
        product_description = intent.getStringExtra("product_description");
        product_quantity = intent.getIntExtra("product_quantity", 0);
        product_status = intent.getStringExtra("product_status");
        currency_code = intent.getStringExtra("currency_code");
        category_name = intent.getStringExtra("category_name");
    }

    public void initComponent() {
        txt_product_name = findViewById(R.id.product_name);
        img_product_image = findViewById(R.id.product_image);
        txt_product_price = findViewById(R.id.product_price);
        txt_product_description = findViewById(R.id.product_description);
        txt_product_quantity = findViewById(R.id.product_quantity);
        btn_cart = findViewById(R.id.btn_add_cart);
    }

    public void displayData() {
        txt_product_name.setText("TESTE");

        Picasso.with(this)
                .load("https://d3l7rqep7l31az.cloudfront.net/images/products/20002612_612_catalog_1.jpg?1459531023")
                .placeholder(R.drawable.ic_loading)
                .into(img_product_image);

        img_product_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        if (Config.ENABLE_DECIMAL_ROUNDING) {
            String price = "100,00";//String.format(Locale.GERMAN, "%1$,.0f", "100");
            txt_product_price.setText(price + " " + "00");
        } else {
            txt_product_price.setText(product_price + " " + "00");
        }

        txt_product_quantity.setText("1" + " " + "100");
        product_status = "Available";
        if (product_status.equals("Available")) {
            btn_cart.setText("ADD");
            btn_cart.setBackgroundResource(R.color.available);
            btn_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    inputDialog();
                }
            });
        } else {
            btn_cart.setEnabled(false);
            btn_cart.setText("ADD CART");
            btn_cart.setBackgroundResource(R.color.sold);
        }

        txt_product_description.setBackgroundColor(Color.parseColor("#ffffff"));
        txt_product_description.setFocusableInTouchMode(false);
        txt_product_description.setFocusable(false);
        txt_product_description.getSettings().setDefaultTextEncodingName("UTF-8");

        WebSettings webSettings = txt_product_description.getSettings();
        Resources res = getResources();
        int fontSize = 12;//res.getInteger(R.integer.font_size);
        webSettings.setDefaultFontSize(fontSize);
        webSettings.setJavaScriptEnabled(true);

        String mimeType = "text/html; charset=UTF-8";
        String encoding = "utf-8";
        String htmlText = product_description;

        String text = "<html><head>"
                + "<style type=\"text/css\">body{color: #525252;}"
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        String text_rtl = "<html dir='rtl'><head>"
                + "<style type=\"text/css\">body{color: #525252;}"
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        if (Config.ENABLE_RTL_MODE) {
            txt_product_description.loadDataWithBaseURL(null, text_rtl, mimeType, encoding, null);
        } else {
            txt_product_description.loadDataWithBaseURL(null, text, mimeType, encoding, null);
        }

    }

    public void inputDialog() {

        try {

        } catch (SQLException sqle) {
            throw sqle;
        }

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);

        View mView = layoutInflaterAndroid.inflate(R.layout.input_dialog, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setView(mView);

        final EditText edtQuantity = (EditText) mView.findViewById(R.id.userInputDialog);
        alert.setCancelable(false);
        int maxLength = 3;
        edtQuantity.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        edtQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);

        alert.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String temp = edtQuantity.getText().toString();
                int quantity = 0;

                if (!temp.equalsIgnoreCase("")) {



                } else {
                    dialog.cancel();
                }
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
