package com.ekumfi.admin.activity;

import static com.ekumfi.admin.activity.GetAuthActivity.APITOKEN;
import static com.ekumfi.admin.activity.GetAgentAuthActivity.getAgentAuthActivity;
import static com.ekumfi.admin.activity.GetAgentPhoneNumberActivity.getAgentPhoneNumberActivity;
import static com.ekumfi.admin.constants.keyConst.API_URL;
import static com.ekumfi.admin.constants.Const.isNetworkAvailable;
import static com.ekumfi.admin.fragment.AgentAccountFragment1.profile_image_file;
import static com.ekumfi.admin.fragment.AgentAccountFragment1.agent_name;
import static com.ekumfi.admin.fragment.AgentAccountFragment2.latitude;
import static com.ekumfi.admin.fragment.AgentAccountFragment2.longitude;
import static com.ekumfi.admin.fragment.AgentAccountFragment2.primary_contact;
import static com.ekumfi.admin.receiver.NetworkReceiver.activeActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ekumfi.admin.R;
import com.ekumfi.admin.constants.Const;
import com.ekumfi.admin.fragment.AgentAccountFragment1;
import com.ekumfi.admin.fragment.AgentAccountFragment2;
import com.ekumfi.admin.other.MyHttpEntity;
import com.ekumfi.admin.pagerAdapter.AgentAccountPageAdapter;
import com.ekumfi.admin.realm.RealmAgent;
import com.ekumfi.admin.receiver.NetworkReceiver;
import com.ekumfi.admin.util.NonSwipeableViewPager;
import com.ekumfi.admin.util.RealmUtility;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.greysonparrelli.permiso.Permiso;
import com.greysonparrelli.permiso.PermisoActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import io.realm.Realm;


@SuppressWarnings("HardCodedStringLiteral")
public class AgentAccountActivity extends PermisoActivity {

    public static RealmAgent realmAgent = new RealmAgent();
    static Context context;


    boolean close = false;
    NonSwipeableViewPager mViewPager;
    AgentAccountPageAdapter agentAccountPageAdapter;
    FloatingActionButton moveprevious, movenext, done;

    RelativeLayout rootview;
    ProgressBar progressBar;
    String tag1 = "android:switcher:" + R.id.pageques_agent + ":" + 0;
    String tag2 = "android:switcher:" + R.id.pageques_agent + ":" + 1;

    AgentAccountFragment1 tabFrag1;
    AgentAccountFragment2 tabFrag2;

    String agent_id, user_id;
    private ProgressDialog mProgress;
    NetworkReceiver networkReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        networkReceiver = new NetworkReceiver();
        Permiso.getInstance().setActivity(this);

        setContentView(R.layout.activity_agent_account);
        getSupportActionBar().hide();

        mProgress = new ProgressDialog(this);
        mProgress.setTitle(getString(R.string.updating_profile));
        mProgress.setMessage(getString(R.string.please_wait));
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        rootview = findViewById(R.id.root);

        if (getIntent().getStringExtra("MODE").equals("EDIT")) {
            if (realmAgent != null) {
                agent_id = realmAgent.getAgent_id();
                user_id = realmAgent.getUser_id();
            }
        }
        progressBar = findViewById(R.id.pbar_pic);
        Realm.init(getApplicationContext());
        agentAccountPageAdapter = new AgentAccountPageAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.pageques_agent);
        mViewPager.setAdapter(agentAccountPageAdapter);
        mViewPager.setOffscreenPageLimit(1); //posible candidate for bug
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem());
                return true;
            }
        });
        progressBar.setVisibility(View.GONE);

        movenext = findViewById(R.id.movenext);
        moveprevious = findViewById(R.id.moveprevious);
        done = findViewById(R.id.done);
        movenext.setOnClickListener(v -> {
            tabFrag1 = (AgentAccountFragment1) getSupportFragmentManager().findFragmentByTag(tag1);
            tabFrag2 = (AgentAccountFragment2) getSupportFragmentManager().findFragmentByTag(tag2);

            switch (mViewPager.getCurrentItem()) {
                case 0:
                    if (tabFrag1.validate()) {
                        mViewPager.setCurrentItem(1);
                        moveprevious.setVisibility(View.VISIBLE);
                        movenext.setVisibility(View.GONE);
                        done.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getApplicationContext(), "Please correct the errors.", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        });
        done.setOnClickListener(v -> {
            if (tabFrag2.validate()) {
                if (isNetworkAvailable(AgentAccountActivity.this)) {
                    if (agent_id != null && !agent_id.equals("")) {
                        new updateAgentAsync(getApplicationContext()).execute();
                    } else {
                        new addAgentAsync(getApplicationContext()).execute();
                    }
                } else {
                    Toast.makeText(AgentAccountActivity.this, getString(R.string.internet_connection_is_needed), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AgentAccountActivity.this, getString(R.string.pls_correct_the_errors), Toast.LENGTH_LONG).show();
            }
        });
        moveprevious.setOnClickListener(v -> {
            tabFrag1 = (AgentAccountFragment1) getSupportFragmentManager().findFragmentByTag(tag1);
            tabFrag2 = (AgentAccountFragment2) getSupportFragmentManager().findFragmentByTag(tag2);

            switch (mViewPager.getCurrentItem()) {
                case 1:
                    if (tabFrag1.validate()) {
                        mViewPager.setCurrentItem(0);
                        movenext.setVisibility(View.VISIBLE);
                        moveprevious.setVisibility(View.GONE);
                        done.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(getApplicationContext(), "Please correct the errors.", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        Permiso.getInstance().setActivity(this);
        activeActivity = this;
        registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void showTwoButtonSnackbar() {

        // Create the Snackbar
        LinearLayout.LayoutParams objLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final Snackbar snackbar = Snackbar.make(rootview, "Exit?", Snackbar.LENGTH_INDEFINITE);

        // Get the Snackbar layout view
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();

        // Inflate our courseListMaterialDialog viewBitmap bitmap = ((RoundedDrawable)profilePic.getDrawable()).getSourceBitmap();
        View snackView = getLayoutInflater().inflate(R.layout.snackbar, null);


        TextView textViewOne = snackView.findViewById(R.id.first_text_view);
        textViewOne.setText(this.getResources().getString(R.string.yes));
        textViewOne.setOnClickListener(v -> {
            snackbar.dismiss();
            close = true;
            AgentAccountActivity.this.onBackPressed();

            //  finish();
        });

        final TextView textViewTwo = snackView.findViewById(R.id.second_text_view);

        textViewTwo.setText(this.getResources().getString(R.string.no));
        textViewTwo.setOnClickListener(v -> {
            Log.d("Deny", "showTwoButtonSnackbar() : deny clicked");
            snackbar.dismiss();


        });

        // Add our courseListMaterialDialog view to the Snackbar's layout
        layout.addView(snackView, objLayoutParams);

        // Show the Snackbar
        snackbar.show();
    }

    @Override
    public void onBackPressed() {
        if (close) {
            super.onBackPressed();
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }
        showTwoButtonSnackbar();
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    private class addAgentAsync extends AsyncTask<Void, Integer, String> {

        HttpClient httpClient = new DefaultHttpClient();
        private Context context;

        private addAgentAsync(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpResponse httpResponse = null;
            HttpEntity httpEntity = null;
            String responseString = null;
            String URL = API_URL + "agents";
            try {
                HttpPost httpPost = new HttpPost(URL);
                MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();

                // Add the file to be uploaded
                if (profile_image_file != null) {
                    multipartEntityBuilder.addPart("profile_image_file", new FileBody(profile_image_file));
                }
                multipartEntityBuilder.addTextBody("name", agent_name.getText().toString().trim());

                multipartEntityBuilder.addTextBody("primary_contact", primary_contact.getText().toString());
//                multipartEntityBuilder.addTextBody("auxiliary_contact", auxiliary_contact.getText().toString());
                multipartEntityBuilder.addTextBody("longitude", String.valueOf(longitude));
                multipartEntityBuilder.addTextBody("latitude", String.valueOf(latitude));
                /*multipartEntityBuilder.addTextBody("street_address", street_address);
                multipartEntityBuilder.addTextBody("digital_address", digital_address);*/

                multipartEntityBuilder.addTextBody("user_id", getIntent().getStringExtra("USER_ID"));


                // Progress listener - updates task's progress
                MyHttpEntity.ProgressListener progressListener =
                        progress -> publishProgress((int) progress);

                // POST
                httpPost.setEntity(new MyHttpEntity(multipartEntityBuilder.build(),
                        progressListener));
                httpPost.setHeader("accept", "application/json");
                httpPost.setHeader("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(APITOKEN, ""));


                httpResponse = httpClient.execute(httpPost);
                httpEntity = httpResponse.getEntity();

                int statusCode = httpResponse.getStatusLine().getStatusCode();

                if (statusCode == 200 || statusCode == 201) {
                    // Server response
                    responseString = EntityUtils.toString(httpEntity);
                }
            } catch (UnsupportedEncodingException | ClientProtocolException e) {
                responseString = e.getMessage();
                e.printStackTrace();
                Log.e("UPLOAD", e.getMessage());
            } catch (IOException e) {
                responseString = e.getMessage();
                Log.e("gardes", e.toString());
//                e.printStackTrace();
            }

            return responseString;
        }

        @Override
        protected void onPreExecute() {
            mProgress.setTitle("Creating Profile.");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(String result) {
            mProgress.dismiss();
            if (result != null) {
                if (result.contains("connect")) {
                    Toast.makeText(getApplicationContext(), context.getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        Realm.init(AgentAccountActivity.this);
                        Realm.getInstance(RealmUtility.getDefaultConfig(AgentAccountActivity.this)).executeTransaction(realm -> {
                            try {
                                realm.createOrUpdateObjectFromJson(RealmAgent.class, jsonObject.getJSONObject("agent"));
                                getAgentPhoneNumberActivity.finish();
                                getAgentAuthActivity.finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                        finish();
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.error_occured), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.error_occured), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Update process
            /*progressbar.setProgress(progress[0]);
            statustext.setText(progress[0].toString() + "%  complete");*/
        }
    }

    private class updateAgentAsync extends AsyncTask<Void, Integer, String> {

        HttpClient httpClient = new DefaultHttpClient();
        private Context context;

        private updateAgentAsync(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpResponse httpResponse = null;
            HttpEntity httpEntity = null;
            String responseString = null;
            String URL = API_URL + "agents/" + agent_id;
            try {
                HttpPost httpPost = new HttpPost(URL);
                MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();

                // Add the file to be uploaded
                if (profile_image_file != null) {
                    multipartEntityBuilder.addPart("profile_image_file", new FileBody(profile_image_file));
                }
                multipartEntityBuilder.addTextBody("name", agent_name.getText().toString().trim());

                multipartEntityBuilder.addTextBody("primary_contact", primary_contact.getText().toString());
//                multipartEntityBuilder.addTextBody("auxiliary_contact", auxiliary_contact.getText().toString());
                multipartEntityBuilder.addTextBody("longitude", String.valueOf(longitude));
                multipartEntityBuilder.addTextBody("latitude", String.valueOf(latitude));
                /*multipartEntityBuilder.addTextBody("street_address", street_address);
                multipartEntityBuilder.addTextBody("digital_address", digital_address);*/

                multipartEntityBuilder.addTextBody("user_id", user_id);

                // Progress listener - updates task's progress
                MyHttpEntity.ProgressListener progressListener =
                        progress -> publishProgress((int) progress);

                // POST
                httpPost.setEntity(new MyHttpEntity(multipartEntityBuilder.build(),
                        progressListener));
                httpPost.setHeader("accept", "application/json");
                httpPost.setHeader("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(APITOKEN, ""));


                httpResponse = httpClient.execute(httpPost);
                httpEntity = httpResponse.getEntity();

                int statusCode = httpResponse.getStatusLine().getStatusCode();

                if (statusCode == 200 || statusCode == 201) {
                    // Server response
                    responseString = EntityUtils.toString(httpEntity);
                }
            } catch (UnsupportedEncodingException | ClientProtocolException e) {
                responseString = e.getMessage();
                e.printStackTrace();
                Log.e("UPLOAD", e.getMessage());
            } catch (IOException e) {
                responseString = e.getMessage();
                Log.e("gardes", e.toString());
//                e.printStackTrace();
            }

            return responseString;
        }

        @Override
        protected void onPreExecute() {
            mProgress.show();
        }

        @Override
        protected void onPostExecute(String result) {
            mProgress.dismiss();
            if (result != null) {
                if (result.contains("connect")) {
                    Toast.makeText(getApplicationContext(), context.getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        Realm.getInstance(RealmUtility.getDefaultConfig(AgentAccountActivity.this)).executeTransaction(realm -> {
                            realm.createOrUpdateObjectFromJson(RealmAgent.class, jsonObject);
                            Const.showToast(getApplicationContext(), "Successfully saved!");
                            finish();
                        });
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.error_occured), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.error_occured), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Update process
        }
    }
}
