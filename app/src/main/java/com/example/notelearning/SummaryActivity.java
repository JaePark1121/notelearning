package com.example.notelearning;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SummaryActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private String currentUserUID;

    static String summary_memoId = "";

    private FirebaseDatabase database;

    static DatabaseReference summaryReference;

    static String uid;

    static String old_summary = "";



    //OkHttpClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        Intent intent = getIntent();
        String content = intent.getStringExtra("content");



        mAuth = FirebaseAuth.getInstance();
        currentUserUID = mAuth.getCurrentUser().getUid();


        EditText summary = (EditText) findViewById(R.id.summary_notes);

        TextView save = (TextView) findViewById(R.id.summary_save);

        ImageView home = (ImageView) findViewById(R.id.summary_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });



        summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                home.setVisibility(View.INVISIBLE);
                save.setVisibility(View.VISIBLE);
            }
        });



        String mode = getIntent().getStringExtra("mode_summary");
        String mode_save = getIntent().getStringExtra("mode_summary_save");

        if ("new_summary".equals(mode)) {

            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            String memoKey = sharedPreferences.getString("latestMemoKey", null);
            summary.setText(content);
            summary_memoId = memoKey;

        } else if ("old_summary".equals(mode)) {
            summary.setText(old_summary);
            if("old_summary_save".equals(mode_save)){
                summary_memoId = SaveAudioActivity.newMemoKey;
            }
            else {
                summary_memoId = NoteAdapter.noteKeys.get(NoteAdapter.selected.get(0));
            }
        }
        database = FirebaseDatabase.getInstance();


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

                String summary_content = summary.getText().toString();

                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                DatabaseReference memoReference;
                System.out.println("CurrentTab: " + MainActivity.curTab);

                    memoReference = mDatabase.child("Users").child(uid).child("folder").child(MainActivity.curTab).child("memos").child(summary_memoId);
                    memoReference.child("summary").setValue(summary_content);
                    // Update the note in the Home tab as well
                    mDatabase.child("Users").child(uid).child("folder").child("Home").child("memos").child(summary_memoId).child("summary").setValue(summary_content);

                    //Is this right?
                       // newMemoKey = memoId.get(0);


                save.setVisibility(View.INVISIBLE);
                home.setVisibility(View.VISIBLE);
            }

        });




        if (summary_memoId != null) {
            System.out.println("memoID complete");
            summaryReference = FirebaseDatabase.getInstance().getReference("Users")
                    .child(currentUserUID)
                    .child("folder")
                    .child(MainActivity.curTab)
                    .child("memos")
                    .child(summary_memoId)
                    .child("summary");
            fetchVocabFromFirebase();
        }






    }

    private void fetchVocabFromFirebase() {
        summaryReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                old_summary = dataSnapshot.getValue().toString();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

}
