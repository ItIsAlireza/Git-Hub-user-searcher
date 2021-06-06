package com.example.githubusersearcher;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    EditText userGet;
    CardMaker cardMaker;
    LinearLayout.LayoutParams layoutParams;
    static String userSearch;
    HashSet<String> uniqueHistory;
    ArrayList<String> searchHistory;

    LinearLayout cardCont;

    StringBuilder URLsearch;

    public static final String PREF_NAME = "searchHistoryPREF";
    static SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        cardMaker();
    }


    public void init() {
        userGet = findViewById(R.id.userGet);
        cardCont = findViewById(R.id.cardCont);


        uniqueHistory = new HashSet<>();

        sp = getSharedPreferences(PREF_NAME , MODE_PRIVATE);

        URLsearch = new StringBuilder();
        URLsearch.append("https:/api.github.com/users/");

        for(int i = 1 ; i <= sp.getAll().size() ; i++){
            uniqueHistory.add(sp.getString(String.valueOf(i) , ""));
        }

        searchHistory = new ArrayList<>(uniqueHistory);
    }

    public void cardMaker() {
        cardMaker = new CardMaker(this);
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

    }

    public void showSearchHistory(View view) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("You Search history");

        if (!searchHistory.isEmpty()) {
            builder.setItems(searchHistory.toArray(new String[0]), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    userGet.setText(searchHistory.get(which));

                }
            })
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setNegativeButton("Clear History", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            searchHistory.clear();
                            getApplicationContext().getSharedPreferences(PREF_NAME ,MODE_PRIVATE).edit().clear().apply();
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "history cleared", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            builder.setMessage("Search history is empty")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void send(View view) {

        View v = this.getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }

        uniqueHistory.add(userGet.getText().toString());
        URLsearch.append(userGet.getText().toString());


        RequestQueue queue = Volley.newRequestQueue(this);
        String url = URLsearch.toString();

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        show_res(response);
                        userGet.setText("");
                        userSearch = "";
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "no response", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        queue.add(request);
    }

    public void show_res(String data) {

        cardCont.removeAllViews();
        try {
            JSONObject main = new JSONObject(data.trim());

            String user = main.getString("login");
            String bio = main.getString("bio");
            String followers = main.getString("followers");
            String following = main.getString("following");

            String imgURL = main.getString("avatar_url");
            Picasso.get().load(imgURL).into(cardMaker.avatar);
            cardMaker.user.setText(user);
            cardMaker.bio.setText(bio);
            cardMaker.followers.setText(followers);
            cardMaker.following.setText(following);
            cardCont.addView(cardMaker);


            searchHistory = new ArrayList<>(uniqueHistory);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        URLsearch.setLength(0);
        URLsearch.append("https://api.github.com/users/");
        int i = 1 ;
        for(String searchs : searchHistory){
            String search = searchs;
            sp.edit().putString(String.valueOf(i) , search).apply();
            i++;

        }
    }
}
