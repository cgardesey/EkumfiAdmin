package com.ekumfi.admin.activity;

import static com.ekumfi.admin.activity.GetAuthActivity.APITOKEN;
import static com.ekumfi.admin.constants.keyConst.API_URL;
import static com.ekumfi.admin.constants.Const.myVolleyError;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.ekumfi.admin.R;
import com.ekumfi.admin.adapter.WholesalerIndexAdapter;
import com.ekumfi.admin.other.InitApplication;
import com.ekumfi.admin.realm.RealmWholesaler;
import com.ekumfi.admin.util.RealmUtility;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.greysonparrelli.permiso.PermisoActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class WholesalerIndexActivity extends PermisoActivity {
    static RecyclerView recyclerview;
    static TextView no_data;
    static WholesalerIndexAdapter wholesalerIndexAdapter;
    static ArrayList<RealmWholesaler> cartArrayList = new ArrayList<>();
    static ArrayList<RealmWholesaler> newCart = new ArrayList<>();
    public static Activity activity;
    FloatingActionButton fab;

    public WholesalerIndexActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wholesaler_index);

        activity = this;
        
        recyclerview = findViewById(R.id.recyclerview);
        no_data = findViewById(R.id.no_data);
        fab = findViewById(R.id.fab);

        wholesalerIndexAdapter = new WholesalerIndexAdapter(new WholesalerIndexAdapter.WholesalerIndexAdapterInterface() {

            @Override
            public void onItemClick(ArrayList<RealmWholesaler> realmWholesalers, int position, WholesalerIndexAdapter.ViewHolder holder) {

            }

            @Override
            public void onImageClick(ArrayList<RealmWholesaler> realmWholesalers, int position, WholesalerIndexAdapter.ViewHolder holder) {

            }
            @Override
            public void onMenuClick(ArrayList<RealmWholesaler> realmWholesalers, int position, WholesalerIndexAdapter.ViewHolder holder) {
                final RealmWholesaler[] realmWholesaler = {realmWholesalers.get(position)};
                PopupMenu popup = new PopupMenu(WholesalerIndexActivity.this, holder.menu);

                popup.inflate(R.menu.wholesaler_menu);

                popup.setOnMenuItemClickListener(item -> {
                    final String wholesaler_id = realmWholesaler[0].getWholesaler_id();

                    ProgressDialog dialog = new ProgressDialog(WholesalerIndexActivity.this);
                    dialog.setMessage("Please wait...");
                    dialog.setCancelable(false);
                    dialog.setIndeterminate(true);

                    if (item.getItemId() == R.id.edit) {
                        dialog.show();
                        StringRequest stringRequest = new StringRequest(
                                Request.Method.GET,
                                API_URL + "wholesalers/" + wholesaler_id,
                                response -> {
                                    if (response != null) {
                                        try {
                                            dialog.dismiss();
                                            JSONObject jsonObject = new JSONObject(response);
                                            Realm.init(WholesalerIndexActivity.this);
                                            Realm.getInstance(RealmUtility.getDefaultConfig(WholesalerIndexActivity.this)).executeTransaction(realm -> {
                                                WholesalerAccountActivity.realmWholesaler = realm.createOrUpdateObjectFromJson(RealmWholesaler.class, jsonObject);

                                                startActivity(new Intent(WholesalerIndexActivity.this, WholesalerAccountActivity.class)
                                                        .putExtra("MODE", "EDIT")
                                                );
                                            });
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                error -> {
                                    dialog.dismiss();
                                    error.printStackTrace();
                                    myVolleyError(WholesalerIndexActivity.this, error);
                                    Log.d("Cyrilll", error.toString());
                                }
                        ) {
                            @Override
                            public Map getHeaders() throws AuthFailureError {
                                HashMap headers = new HashMap();
                                headers.put("accept", "application/json");
                                headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(APITOKEN, ""));
                                return headers;
                            }
                        };
                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                0,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        InitApplication.getInstance().addToRequestQueue(stringRequest);
                        return true;
                        /*case R.id.product:
                            dialog.show();
                            StringRequest stringRequestDelete = new StringRequest(
                                    Request.Method.POST,
                                    API_URL + "scoped-wholesaler-products",
                                    response -> {
                                        dialog.dismiss();
                                        if (response != null) {
                                            try {
                                                JSONArray jsonArray = new JSONArray(response);
                                                Realm.init(WholesalerIndexActivity.this);
                                                Realm.getInstance(RealmUtility.getDefaultConfig(WholesalerIndexActivity.this)).executeTransaction(realm -> {
//                                                    realm.where(RealmWholesalerProduct.class).findAll().deleteAllFromRealm();
                                                    realm.createOrUpdateAllFromJson(RealmWholesalerProduct.class, jsonArray);
                                                });

                                                WholesalerProductsActivity.realmWholesaler = realmWholesaler[0];
                                                startActivity(new Intent(WholesalerIndexActivity.this, WholesalerProductsActivity.class));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    },
                                    error -> {
                                        dialog.dismiss();
                                        error.printStackTrace();
                                        myVolleyError(WholesalerIndexActivity.this, error);
                                        Log.d("Cyrilll", error.toString());
                                    }
                            ) {
                                @Override
                                public Map getHeaders() throws AuthFailureError {
                                    HashMap headers = new HashMap();
                                    headers.put("accept", "application/json");
                                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(WholesalerIndexActivity.this).getString("com.ekumfi.admin" + APITOKEN, ""));
                                    return headers;
                                }
                                @Override
                                public Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("wholesaler_id", wholesaler_id);
                                    return params;
                                }
                            };
                            stringRequestDelete.setRetryPolicy(new DefaultRetryPolicy(
                                    0,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            InitApplication.getInstance().addToRequestQueue(stringRequestDelete);
                            return true;*/
                        /*case R.id.chat:
                            final String[] wholesaler_name = new String[1];
                            final String[] profile_image_url = new String[1];
                            final String[] availability = new String[1];

                            Realm.init(WholesalerIndexActivity.this);
                            wholesaler_name[0] = realmWholesaler[0].getShop_name();
                            profile_image_url[0] = realmWholesaler[0].getShop_image_url();
                            availability[0] = realmWholesaler[0].getAvailability();

                            dialog.show();
                            StringRequest chatStringRequest = new StringRequest(
                                    Request.Method.POST,
                                    API_URL + "ekumfi-chat-data",
                                    response -> {
                                        if (response != null) {
                                            dialog.dismiss();
                                            try {
                                                JSONObject jsonObject = new JSONObject(response);
                                                Realm.init(WholesalerIndexActivity.this);
                                                Realm.getInstance(RealmUtility.getDefaultConfig(WholesalerIndexActivity.this)).executeTransaction(realm -> {
                                                    try {
                                                        realmWholesaler[0] = realm.createOrUpdateObjectFromJson(RealmWholesaler.class, jsonObject.getJSONObject("wholesaler"));
                                                        realm.createOrUpdateAllFromJson(RealmWholesaler.class, jsonObject.getJSONArray("chats"));
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    wholesaler_name[0] = realmWholesaler[0].getShop_name();
                                                    profile_image_url[0] = realmWholesaler[0].getShop_image_url();
                                                    availability[0] = realmWholesaler[0].getAvailability();
                                                });

                                                startActivity(new Intent(WholesalerIndexActivity.this, MessageActivity.class)
                                                        .putExtra("WHOLESALER_ID", wholesaler_id)
                                                        .putExtra("WHOLESALER_NAME", wholesaler_name[0])
                                                        .putExtra("PROFILE_IMAGE_URL", profile_image_url[0])
                                                        .putExtra("AVAILABILITY", availability[0])
                                                );
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    },
                                    error -> {
                                        error.printStackTrace();
                                        startActivity(new Intent(WholesalerIndexActivity.this, MessageActivity.class)
                                                .putExtra("WHOLESALER_ID", wholesaler_id)
                                                .putExtra("WHOLESALER_NAME", wholesaler_name[0])
                                                .putExtra("PROFILE_IMAGE_URL", profile_image_url[0])
                                                .putExtra("AVAILABILITY", availability[0])
                                        );
                                        dialog.dismiss();
                                        Log.d("Cyrilll", error.toString());
                                    }
                            ) {
                                @Override
                                public Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("wholesaler_id", wholesaler_id);
                                    params.put("consumer_id", "");
                                    Realm.init(WholesalerIndexActivity.this);
                                    Realm.getInstance(RealmUtility.getDefaultConfig(WholesalerIndexActivity.this)).executeTransaction(realm -> {
                                        RealmResults<RealmChat> results = realm.where(RealmChat.class)
                                                .sort("id", Sort.DESCENDING)
                                                .equalTo("wholesaler_id", wholesaler_id)
                                                .equalTo("consumer_id", "")
                                                .findAll();
                                        ArrayList<RealmChat> myArrayList = new ArrayList<>();
                                        for (RealmChat realmChat : results) {
                                            if (realmChat != null && !(realmChat.getChat_id().startsWith("z"))) {
                                                myArrayList.add(realmChat);
                                            }
                                        }
                                        if (results.size() < 3) {
                                            params.put("id", "0");
                                        }
                                        else{
                                            params.put("id", String.valueOf(myArrayList.get(0).getId()));
                                        }
                                    });
                                    return params;
                                }
                                @Override
                                public Map getHeaders() throws AuthFailureError {
                                    HashMap headers = new HashMap();
                                    headers.put("accept", "application/json");
                                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(WholesalerIndexActivity.this).getString("com.ekumfi.admin" + APITOKEN, ""));
                                    return headers;
                                }
                            };
                            chatStringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                    0,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            InitApplication.getInstance().addToRequestQueue(chatStringRequest);
                            return true;*/
                        /*case R.id.orders:
                            dialog.show();
                            StringRequest ordersStringRequest = new StringRequest(
                                    Request.Method.POST,
                                    API_URL + "scoped-wholesaler-carts",
                                    response -> {
                                        if (response != null) {
                                            dialog.dismiss();
                                            try {
                                                JSONArray jsonArray = new JSONArray(response);
                                                Realm.init(WholesalerIndexActivity.this);
                                                Realm.getInstance(RealmUtility.getDefaultConfig(WholesalerIndexActivity.this)).executeTransaction(realm -> {
                                                    realm.where(RealmCart.class).findAll().deleteAllFromRealm();
                                                    realm.createOrUpdateAllFromJson(RealmCart.class, jsonArray);
                                                });
                                                startActivity(new Intent(WholesalerIndexActivity.this, WholesalerOrdersActivity.class)
                                                        .putExtra("WHOLESALER_ID", wholesaler_id)
                                                );
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    },
                                    error -> {
                                        error.printStackTrace();
                                        myVolleyError(WholesalerIndexActivity.this, error);
                                        dialog.dismiss();
                                        Log.d("Cyrilll", error.toString());
                                    }
                            ) {
                                @Override
                                public Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("wholesaler_id", wholesaler_id);
                                    return params;
                                }
                                @Override
                                public Map getHeaders() throws AuthFailureError {
                                    HashMap headers = new HashMap();
                                    headers.put("accept", "application/json");
                                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(WholesalerIndexActivity.this).getString("com.ekumfi.admin" + APITOKEN, ""));
                                    return headers;
                                }
                            };
                            ordersStringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                    0,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            InitApplication.getInstance().addToRequestQueue(ordersStringRequest);
                            return true;*/
                    }
                    return false;
                });
                popup.show();
            }
        }, WholesalerIndexActivity.this, cartArrayList, true);
        recyclerview.setLayoutManager(new LinearLayoutManager(WholesalerIndexActivity.this));
        recyclerview.setNestedScrollingEnabled(false);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setAdapter(wholesalerIndexAdapter);

        fab.setOnClickListener(v -> {
            startActivity(new Intent(WholesalerIndexActivity.this, GetWholesalerPhoneNumberActivity.class));
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        populateChatIndex(WholesalerIndexActivity.this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                API_URL + "wholesalers",
                response -> {
                    if (response != null) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            Realm.init(WholesalerIndexActivity.this);
                            Realm.getInstance(RealmUtility.getDefaultConfig(WholesalerIndexActivity.this)).executeTransaction(realm -> {
                                realm.createOrUpdateAllFromJson(RealmWholesaler.class, jsonArray);
                            });
                            populateChatIndex(WholesalerIndexActivity.this);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                error -> {
                    error.printStackTrace();
                    Log.d("Cyrilll", error.toString());
                }
        ) {

             /*Passing some request headers*/
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("accept", "application/json");
                headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(WholesalerIndexActivity.this).getString("com.ekumfi.admin" + APITOKEN, ""));
                return headers;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        InitApplication.getInstance().addToRequestQueue(stringRequest);
    }


    public static void populateChatIndex(final Context context) {
        Realm.init(context);
        Realm.getInstance(RealmUtility.getDefaultConfig(context)).executeTransaction(realm -> {
            RealmResults<RealmWholesaler> results;

            results = realm.where(RealmWholesaler.class)
                    .sort("id", Sort.DESCENDING)
                    .distinct("wholesaler_id")
                    .findAll();

            if (results.size() < 1) {
                no_data.setVisibility(View.VISIBLE);
                recyclerview.setVisibility(View.GONE);
            }
            else {
                no_data.setVisibility(View.GONE);
                recyclerview.setVisibility(View.VISIBLE);
            }
            newCart.clear();
            for (RealmWholesaler realmWholesaler : results) {
                newCart.add(realmWholesaler);
            }
            cartArrayList.clear();
            cartArrayList.addAll(newCart);
            wholesalerIndexAdapter.notifyDataSetChanged();
        });
    }
}
