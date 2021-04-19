package com.anuvg.preazyadmin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

class RecyclerAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private Context context;
    private ArrayList<UserDetailCardView> arrayList;

    RecyclerAdapter(Context context, ArrayList<UserDetailCardView> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.user_detail_card_view, null);
        return new RecyclerViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        holder.myUserEndId.setText("" + arrayList.get(position).getUserEndId());
        holder.myUserName.setText("" + arrayList.get(position).getUserName());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}

class RecyclerViewHolder extends RecyclerView.ViewHolder {
    TextView myUserEndId, myUserName;

    RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        myUserEndId = itemView.findViewById(R.id.textViewIdDisplay_CardView);
        myUserName = itemView.findViewById(R.id.textViewUserNameDisplay_CardView);
    }
}