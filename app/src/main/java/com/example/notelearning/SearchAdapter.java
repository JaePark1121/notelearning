package com.example.notelearning;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>{

    public interface OnItemClickEventListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickEventListener mItemClickListener;

    public void setOnItemClickListener(OnItemClickEventListener listener){
        mItemClickListener = listener;
    }





    public ArrayList<String> dataList;

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
        return new ViewHolder(view, mItemClickListener);
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
        public ViewHolder(@NonNull View itemView, final OnItemClickEventListener itemClickListener) {
            super(itemView);

            noteNameTV = itemView.findViewById(R.id.search_note_title);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    final int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION) {
                        itemClickListener.onItemClick(view, position);
                    }
                }
            });
        }

    }
}