package com.example.notelearning;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>{

    private ArrayList<String> dataList;

    public SearchAdapter(ArrayList<String> dataList) {
        this.dataList= dataList;
    }

    public void filterList(ArrayList<String> filterlist) {
        dataList = filterlist;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        String noteName = dataList.get(position);
        holder.noteNameTV.setText(MainSearchFragment.searchArrayList.get(noteName));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView noteNameTV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noteNameTV = itemView.findViewById(R.id.search_note_title);
        }
    }
}
