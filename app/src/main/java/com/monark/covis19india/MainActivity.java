package com.monark.covis19india;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    private TextView daily_confirm_case;
    private TextView total_confirm_case;
    private TextView total_active_case;
    private TextView daily_recovery_case;
    private TextView total_recovery_case;
    private TextView daily_death_case;
    private TextView total_death_case;
    private TextView date;
    private TextView daily_confirmed_case;
    private ImageView refresh_btn;
    private CardView card1,card2,card3,card4;
    private Toolbar toolbar;
    private View shimmer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        daily_confirm_case=findViewById(R.id.dist_confirm_cases);
        total_confirm_case=findViewById(R.id.total_confirm_cases);
        total_active_case=findViewById(R.id.total_active_cases);
        daily_recovery_case=findViewById(R.id.dist_recovered_cases);
        total_recovery_case=findViewById(R.id.total_recovered_cases);
        daily_death_case = findViewById(R.id.dist_death_cases);
        total_death_case=findViewById(R.id.total_death_cases);
        date=findViewById(R.id.update);
        refresh_btn=findViewById(R.id.refresh_btn);
        card1=findViewById(R.id.card1);
        card2=findViewById(R.id.card2);
        card3=findViewById(R.id.card3);
        card4=findViewById(R.id.card4);
        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStateListActivity();
            }
        });
        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStateListActivity();
            }
        });
        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStateListActivity();
            }
        });
        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStateListActivity();
            }
        });
//
//        toolbar=findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getData();

        refresh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shimmer=findViewById(R.id.shimmer_loading);
                shimmer.setVisibility(View.VISIBLE);
                getData();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Refreshed", Toast.LENGTH_SHORT).show();
                    }
                },3000);
            }
        });

    }

    private void getData() {
        String url="https://data.covid19india.org/data.json";
        shimmer=findViewById(R.id.shimmer_loading);
       shimmer.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjectRequest =new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Toast.makeText(MainActivity.this,response.toString(), Toast.LENGTH_SHORT).show();

                        try {
                            JSONArray jsonArray=response.getJSONArray("statewise");

                            JSONObject jsonObject= jsonArray.getJSONObject(0);

//                            confirm cases
                            String total_confirmed_cases=jsonObject.getString("confirmed");
                            String daily_confirmed_cases=jsonObject.getString("deltaconfirmed");
                            total_confirm_case.setText(total_confirmed_cases);
                            daily_confirm_case.setText("[+"+daily_confirmed_cases+"]");

//                            Active cases
                            String total_Active_cases=jsonObject.getString("active");
                            total_active_case.setText(total_Active_cases);

//                            Recoverd cases
                            String total_recover_cases=jsonObject.getString("recovered");
                            String daily_recover_cases=jsonObject.getString("deltarecovered");
                            total_recovery_case.setText(total_recover_cases);
                            daily_recovery_case.setText("[+"+daily_recover_cases+"]");

//                            Death cases
                            String total_Deaths_cases=jsonObject.getString("deaths");
                            String daily_deaths_cases=jsonObject.getString("deltadeaths");
                            total_death_case.setText(total_Deaths_cases);
                            daily_death_case.setText("[+"+daily_deaths_cases+"]");

                            String updatetime=jsonObject.getString("lastupdatedtime");
                            date.setText(updatetime);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    shimmer.setVisibility(View.INVISIBLE);
                                }
                            },3000);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            shimmer.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        shimmer.setVisibility(View.VISIBLE);
                    }
                });

        int socketTime=7000;
        RetryPolicy retryPolicy=new DefaultRetryPolicy(socketTime,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);

        RequestQueue requestQueue= Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(jsonObjectRequest);

    }
//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_home,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void getStateListActivity() {
        startActivity(new Intent(MainActivity.this,StateListActivity.class));
    }
}