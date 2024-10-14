package com.example.prototype;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

//custom adapter for cardview
public class customAdapter extends RecyclerView.Adapter<customAdapter.contentViewHolder>{

    private List<content> contentList;

    public customAdapter(List<content> contentList) {
        this.contentList = contentList;
    }

    @NonNull
    @Override
    public contentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item_layout,
                parent,
                false);
        return new contentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull contentViewHolder holder, int position) {
        content Content = contentList.get(position);
        holder.textView.setText(Content.contentName);

    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    public static class contentViewHolder extends RecyclerView.ViewHolder{
        TextView textView;

        public contentViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.txtviewHeader);

        }
    }
}
