package com.example.notelearning;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VocabAdapter extends RecyclerView.Adapter<VocabAdapter.ViewHolder>{

    private List<Vocab> words; // sample data source

    public VocabAdapter(List<Vocab> vocabWords) {
        this.words = vocabWords;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView vocab;
        TextView definition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            vocab = itemView.findViewById(R.id.vocab_word);
            definition = itemView.findViewById(R.id.definition);
        }
    }

    @NonNull
    @Override
    public VocabAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.fragment_vocab_box, parent, false);
        return new VocabAdapter.ViewHolder(itemView); // fixed this line
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
