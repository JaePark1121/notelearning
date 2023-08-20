package com.example.notelearning;


import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class VocabAdapter extends RecyclerView.Adapter<VocabAdapter.ViewHolder>{

     static Context context;

    private List<Vocab> words; // sample data source

    public VocabAdapter(List<Vocab> vocabWords) {

        this.words = vocabWords;
    }

    public interface OnDeleteButtonClickListener {
        void onDeleteButtonClick();
    }

    private static OnDeleteButtonClickListener mListener;

    public VocabAdapter(List<Vocab> vocabWords, OnDeleteButtonClickListener listener) {
        this.words = vocabWords;
        this.mListener = listener;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView vocab;
        TextView definition;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            vocab = itemView.findViewById(R.id.vocab_word);
            definition = itemView.findViewById(R.id.definition);
            ImageView deleteBtn = itemView.findViewById(R.id.vocab_box_delete);

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onDeleteButtonClick();
                    }
                }
            });




        }
    }

    @NonNull
    @Override
    public VocabAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.fragment_vocab_box, parent, false);
        return new VocabAdapter.ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull VocabAdapter.ViewHolder holder, int position) {
        Vocab word = words.get(position);
        holder.vocab.setText(word.getWord());
        holder.definition.setText(word.getDefinition());
    }

    @Override
    public int getItemCount() {
        return words.size();
    }
}
