package com.example.notelearning;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChangeFolderAdapter extends RecyclerView.Adapter<ChangeFolderAdapter.ViewHolder> {

    private ArrayList<String> dataList;

    public ChangeFolderAdapter(ArrayList<String> dataList) {
        this.dataList= dataList;
    }

    @NonNull
    @Override
    public ChangeFolderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.change_folder_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChangeFolderAdapter.ViewHolder holder, int position) {
        String folderName = dataList.get(position);
        holder.folderNameTV.setText(ChangeFolderFragment.folderList.get(0));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView folderNameTV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            folderNameTV = itemView.findViewById(R.id.folder_title);
        }
    }
}
