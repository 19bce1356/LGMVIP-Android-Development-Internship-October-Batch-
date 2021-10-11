package com.monark.covis19india;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.HashMap;

public class distwiseDetails extends AppCompatActivity {
    private TextView daily_confirm_case;
    private TextView total_confirm_case;
    private TextView total_active_case;
    private TextView daily_recovery_case;
    private TextView total_recovery_case;
    private TextView daily_death_case;
    private TextView total_death_case;
    private ProgressDialog progressDialog;
    private TextView distName;
    private String dist_name;
    private Intent intent;
    private String state_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distwise_details);

        intent =getIntent();
        dist_name=intent.getStringExtra("dist_name");
        state_name=intent.getStringExtra("state_name");
        distName=findViewById(R.id.dist_name);
        daily_confirm_case=findViewById(R.id.dist1_confirm_cases);
        total_confirm_case=findViewById(R.id.total1_confirm_cases);
        total_active_case=findViewById(R.id.total1_active_cases);
        daily_recovery_case=findViewById(R.id.dist1_recovered_cases);
        total_recovery_case=findViewById(R.id.total1_recovered_cases);
        daily_death_case = findViewById(R.id.dist1_death_cases);
        total_death_case=findViewById(R.id.total1_death_cases);

        progressDialog =new ProgressDialog(this);
        progressDialog.setMessage("Loading Data..");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        distName.setText(dist_name);
        getData();

    }

    private void getData() {
        String url="https://data.covid19india.org/state_district_wise.json";
        JsonObjectRequest jsonObjectRequest =new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Toast.makeText(MainActivity.this,response.toString(), Toast.LENGTH_SHORT).show();

                        try {
                            JSONObject state_dataObject = response.getJSONObject(state_name);
                            JSONObject distObject = state_dataObject.getJSONObject("districtData");
                            JSONObject particular_distData = distObject.getJSONObject(dist_name);
                            JSONObject daily_datObject = particular_distData.getJSONObject("delta");


                            String total_confirm = particular_distData.getString("confirmed");
                            String daily_confirm = daily_datObject.getString("confirmed");

                            String total_active_cases = particular_distData.getString("active");

                            String total_recover = particular_distData.getString("recovered");
                            String daily_recover= daily_datObject.getString("recovered");

                            String total_death = particular_distData.getString("deceased");
                            String daily_death = daily_datObject.getString("deceased");

                            total_confirm_case.setText(total_confirm);
                            daily_confirm_case.setText("[+" + daily_confirm+ "]");

                            total_active_case.setText(total_active_cases);

                            total_recovery_case.setText(total_recover);
                            daily_recovery_case.setText("[+" +daily_recover+ "]");

                            total_death_case.setText(total_death);
                            daily_death_case.setText("[+" +daily_death+ "]");



                            progressDialog.dismiss();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(distwiseDetails.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

        int socketTime=7000;
        RetryPolicy retryPolicy=new DefaultRetryPolicy(socketTime,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);

        RequestQueue requestQueue= Volley.newRequestQueue(distwiseDetails.this);
        requestQueue.add(jsonObjectRequest);
    }
}