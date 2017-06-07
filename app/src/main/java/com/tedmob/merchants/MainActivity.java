package com.tedmob.merchants;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.tedmob.merchants.adapter.MerchantAdapter;
import com.tedmob.merchants.adapter.OnLoadMoreListener;
import com.tedmob.merchants.model.APIResponseJSON;
import com.tedmob.merchants.model.Merchant;
import com.tedmob.merchants.request.MerchantService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    public static final String BASE_URL = "https://api.mybookqatar.com/";

    Retrofit retrofit;
    public MerchantService merchantService;

    public List<Merchant> merchantList = new ArrayList<>();

    private RecyclerView recyclerView;
    private MerchantAdapter mAdapter;

    private int page = 1;
    public static MainActivity instance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        //retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        merchantService = retrofit.create(MerchantService.class);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new MerchantAdapter(recyclerView, merchantList, this);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                System.out.println("onload");
                page++;
                merchantList.add(null);
                mAdapter.notifyItemInserted(merchantList.size() - 1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        merchantList.remove(merchantList.size() - 1);
                        mAdapter.notifyItemRemoved(merchantList.size());
                        page++;
                        GetData(page);
//                        mAdapter.notifyDataSetChanged();
//                        mAdapter.setLoaded();
                    }
                }, 2000);


            }
        });


        GetData(page);
    }

    private void GetData(int page) {

        Boolean Checknet = Checknet();


        if (Checknet) {
            Call<APIResponseJSON> call = merchantService.getMerchants(page, 10, 1);
            call.enqueue(new Callback<APIResponseJSON>() {
                @Override
                public void onResponse(Call<APIResponseJSON> call, Response<APIResponseJSON> response) {
                    APIResponseJSON body = response.body();

                    for (Merchant merchant : body.message)
                        merchantList.add(merchant);

                    mAdapter.notifyDataSetChanged();
                    mAdapter.setLoaded();

                }

                @Override
                public void onFailure(Call<APIResponseJSON> call, Throwable t) {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(instance, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(instance);
                    }
                    builder.setTitle("Api Error")
                            .setMessage("Please try again later")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                            .show();
                }

            });
        }
    }

    private Boolean Checknet() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null) {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(instance, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(instance);
            }
            builder.setTitle("Internet Error")
                    .setMessage("No internet connection Please try again later")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .show();

            return false;
        } else {
            return true;
        }

    }


    public void MakeCall(int position) {

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + merchantList.get(position).contact_number.toString()));
        String[] PERMISSIONS = {android.Manifest.permission.CALL_PHONE};
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions((MainActivity) this, PERMISSIONS, 1001);
            else
                instance.startActivity(callIntent);

        } else
            instance.startActivity(callIntent);


    }

    public void SendMail(int position) {
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{merchantList.get(position).contact_email});

        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Hello Tedmob");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Good morning");


        emailIntent.setType("message/rfc822");

        try {
            startActivity(Intent.createChooser(emailIntent,
                    "Send email using..."));
        } catch (android.content.ActivityNotFoundException ex) {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1001: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(instance, "Permission granted", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(instance, "The app was not allowed to call.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void makeCall() {
    }
}
