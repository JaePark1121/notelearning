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
    private ChangeFolderAdapter.OnItemClickEventListener listener;

    static int selectedPosition = -1;

    static String selectedFolderName;

    // Modified Constructor to take in the listener
    public ChangeFolderAdapter(ArrayList<String> dataList, ChangeFolderAdapter.OnItemClickEventListener listener) {
        this.dataList = dataList;
        this.mItemClickListener = listener;
    }

    public interface OnItemClickEventListener {
        void onItemClick(View view, int position, String folderName);
    }

    private ChangeFolderAdapter.OnItemClickEventListener mItemClickListener;

    public void setOnItemClickListener(ChangeFolderAdapter.OnItemClickEventListener listener){
        mItemClickListener = listener;
    }

    @NonNull
    @Override
    public ChangeFolderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.change_folder_item, parent, false);
        return new ViewHolder(view, mItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChangeFolderAdapter.ViewHolder holder, int position) {
        String folderName = dataList.get(position);
        holder.folderNameTV.setText(folderName);
        if(selectedPosition == position){
            holder.itemView.setBackgroundResource(R.drawable.notebook_selected);
        }
        else{
            holder.itemView.setBackgroundResource(R.drawable.notebook_default);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView folderNameTV;

        public ViewHolder(@NonNull View itemView, final OnItemClickEventListener listener) {
            super(itemView);
            folderNameTV = itemView.findViewById(R.id.folder_title);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(view, position, folderNameTV.getText().toString());
                        }
                    }
                }
            });
        }
    }
}
