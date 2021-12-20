package com.example.firebaseapp.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.firebaseapp.Activities.ViewPostActivity;
import com.example.firebaseapp.Data.User;
import com.example.firebaseapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.card.MaterialCardView;


public class DisplayDataAdapter extends FirebaseRecyclerAdapter<User,DisplayDataAdapter.MyViewHolder> {
    Context context;

    public DisplayDataAdapter(@NonNull FirebaseRecyclerOptions<User> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_card,parent,false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull User user) {
        holder.recentPostTitle.setText(user.getTitle());
        holder.recentPostAuthor.setText("By: " + user.getAuthor());
        holder.userImage.setClipToOutline(true);
        Glide.with(context).load(user.getImageUrl()).into(holder.userImage);
        holder.materialCardView.setOnClickListener(v->{
            Intent intent = new Intent(this.context, ViewPostActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("title" ,user.getTitle());
            bundle.putString("author" ,user.getAuthor());
            bundle.putString("date" ,user.getDate());
            bundle.putString("time" ,user.getTime());
            bundle.putString("body" ,user.getBody());
            bundle.putString("imageUrl" ,user.getImageUrl());
            intent.putExtras(bundle);
            this.context.startActivity(intent);
        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView recentPostTitle, recentPostAuthor;
        ImageView userImage;
        MaterialCardView materialCardView;
        
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.recentPostTitle = itemView.findViewById(R.id.recentPostTitle);
            this.recentPostAuthor = itemView.findViewById(R.id.recentPostAuthor);
            this.userImage = itemView.findViewById(R.id.recentPostImage);
            this.materialCardView = itemView.findViewById(R.id.userCardView);
        }
    }
}
