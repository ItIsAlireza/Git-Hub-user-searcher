package com.example.githubusersearcher;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class CardMaker extends LinearLayout {
    public static TextView user;
    public static TextView bio;
    public static TextView followers;
    public static TextView following;
    public static CircleImageView avatar;
    public CardMaker(Context context) {
        super(context);
        init(context);
    }

    public CardMaker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CardMaker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.user_card , this, true);

        user = findViewById(R.id.user);
        bio = findViewById(R.id.bio);
        followers = findViewById(R.id.follower);
        following = findViewById(R.id.following);

        avatar = findViewById(R.id.icon);
    }
}