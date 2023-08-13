package com.example.notelearning;

import static com.example.notelearning.VocabListActivity.addVocabFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class AddVocabFragment extends Fragment {


    public static String uid;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.fragment_add_vocab, container, false);
        FloatingActionButton addVocab = view.findViewById(R.id.add_vocab_button);
        Button closeTab = view.findViewById(R.id.close_vocab_fragment);

        addVocab.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                EditText word1 = view.findViewById(R.id.word_box);
                EditText definition1 = view.findViewById(R.id.definition_box);


                String word = word1.getText().toString();
                String definition = definition1.getText().toString();

                VocabListActivity activity = (VocabListActivity) getActivity();

                // Get the selected memo UIDs
                ArrayList<String> memoUIDs = VocabListActivity.memoUID;
                System.out.println(memoUIDs.get(0));




                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                if(memoUIDs.get(0) == null){
                    mDatabase.child("Users").child(uid).child("folder").child(MainActivity.curTab).child("memos")
                            .child(SaveAudioActivity.newMemoKey).child("vocablist").child(word).setValue(definition);
                }
                else {

                    mDatabase.child("Users").child(uid).child("folder").child(MainActivity.curTab).child("memos")
                            .child(memoUIDs.get(0)).child("vocablist").child(word).setValue(definition);
                }

            }
        });


        closeTab.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(addVocabFragment).commitAllowingStateLoss();

            }
        });


        // Inflate the layout for this fragment
        return view;

    }
}