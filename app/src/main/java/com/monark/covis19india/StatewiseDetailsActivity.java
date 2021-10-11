package com.monark.covis19india;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.monark.covis19india.models.DistModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StatewiseDetailsActivity extends AppCompatActivity {
    private TextView daily_confirm_case;
    private TextView total_confirm_case;
    private TextView total_active_case;
    private TextView daily_recovery_case;
    private TextView total_recovery_case;
    private TextView daily_death_case;
    private TextView total_death_case;
    private ProgressDialog progressDialog;


    private DistListAdapter distListAdapter;
    private MaterialSearchView materialSearchView;
    List<DistModel> arrayList;

    private RecyclerView recyclerView;
    private Intent intent;
    String state_name;
    private TextView toolbar;
    LinearLayout state_data_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statewise_details);
        intent=getIntent();
        state_name=intent.getStringExtra("state_name");
        toolbar=findViewById(R.id.toolbar_state_details);
        toolbar.setText(state_name);
        daily_confirm_case=findViewById(R.id.dist_daily_confirm_cases);
        total_confirm_case=findViewById(R.id.dist_total_confirm_cases);
        total_active_case=findViewById(R.id.dist_total_active_cases);
        daily_recovery_case=findViewById(R.id.dist_daily_recover_cases);
        total_recovery_case=findViewById(R.id.dist_total_recover_cases);
        daily_death_case = findViewById(R.id.dist_daily_death_cases);
        total_death_case=findViewById(R.id.dist_total_death_cases);
        materialSearchView = findViewById(R.id.search_view);

        progressDialog =new ProgressDialog(this);
        progressDialog.setMessage("Loading Data..");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        getData();
        recyclerView=findViewById(R.id.dist_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        arrayList=new ArrayList<>();

        getDistListData();
    }

    private void getDistListData() {
        String url="https://data.covid19india.org/state_district_wise.json";
        JsonObjectRequest jsonObjectRequest =new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Toast.makeText(MainActivity.this,response.toString(), Toast.LENGTH_SHORT).show();

                        try {
                           JSONObject jsonObject=response.getJSONObject(state_name);
                           JSONObject distobject=jsonObject.getJSONObject("districtData");

                           JSONArray key=distobject.names();
                           for(int i=0;i<key.length();i++){
                               String distName= key.getString(i);

                               JSONObject jsonObjectDist=distobject.getJSONObject(distName);

                               String confirm_cases=jsonObjectDist.getString("confirmed");

                               HashMap<String,String> map=new HashMap<>();
                               map.put("state_name",state_name);
                               map.put("confirm_case",confirm_cases);
                               map.put("dist_name",distName);
                               DistModel distModel = new DistModel(distName, state_name, confirm_cases);

                               arrayList.add(distModel);

                           }

                           distListAdapter=new DistListAdapter(getApplicationContext(),arrayList);
                            distListAdapter.setHasStableIds(true);
                            recyclerView.setAdapter(distListAdapter);
                            progressDialog.dismiss();

                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                            progressDialog.dismiss();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(StatewiseDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

        int socketTime=7000;
        RetryPolicy retryPolicy=new DefaultRetryPolicy(socketTime,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);

        RequestQueue requestQueue= Volley.newRequestQueue(StatewiseDetailsActivity.this);
        requestQueue.add(jsonObjectRequest);
    }

    private void getData() {
        String url="https://data.covid19india.org/data.json";
        JsonObjectRequest jsonObjectRequest =new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Toast.makeText(MainActivity.this,response.toString(), Toast.LENGTH_SHORT).show();

                        try {
                            JSONArray jsonArray=response.getJSONArray("statewise");

                            for(int i=1;i<jsonArray.length();i++){
                                    JSONObject jsonObject= jsonArray.getJSONObject(i);
                                    String state=jsonObject.getString("state");
                                    if(state.equals(state_name)){
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
                                    }
                                    progressDialog.dismiss();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(StatewiseDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

        int socketTime=7000;
        RetryPolicy retryPolicy=new DefaultRetryPolicy(socketTime,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);

        RequestQueue requestQueue= Volley.newRequestQueue(StatewiseDetailsActivity.this);
        requestQueue.add(jsonObjectRequest);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.state_toolbar, menu);

        MenuItem item = menu.findItem(R.id.search);
        materialSearchView.setMenuItem(item);
        materialSearchView.setHint("Search By District...");
        materialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                distListAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                distListAdapter.getFilter().filter(newText);
                return false;
            }


        });

        materialSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                state_data_layout.setVisibility(View.GONE);
            }

            @Override
            public void onSearchViewClosed() {
                state_data_layout.setVisibility(View.VISIBLE);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}