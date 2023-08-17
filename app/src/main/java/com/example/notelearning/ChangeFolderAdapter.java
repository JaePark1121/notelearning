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
    private ChangeFolderAdapter.FolderClickListener listener;

    // Modified Constructor to take in the listener
    public ChangeFolderAdapter(ArrayList<String> dataList, ChangeFolderAdapter.FolderClickListener listener) {
        this.dataList = dataList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChangeFolderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.change_folder_item, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChangeFolderAdapter.ViewHolder holder, int position) {
        String folderName = dataList.get(position);
        holder.folderNameTV.setText(folderName);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public interface FolderClickListener {
        void onFolderClick(String folderName);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView folderNameTV;

        public ViewHolder(@NonNull View itemView, final FolderClickListener listener) {
            super(itemView);
            folderNameTV = itemView.findViewById(R.id.folder_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onFolderClick(folderNameTV.getText().toString());
                        }
                    }
                }
            });
        }
    }
}
