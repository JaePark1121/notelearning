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
    EditText summary;



    //OkHttpClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        Intent intent = getIntent();
        String content = intent.getStringExtra("content");



        mAuth = FirebaseAuth.getInstance();
        currentUserUID = mAuth.getCurrentUser().getUid();


        summary = (EditText) findViewById(R.id.summary_notes);

        TextView save = (TextView) findViewById(R.id.summary_save);

        ImageView home = (ImageView) findViewById(R.id.summary_home);

        ImageView menu = (ImageView) findViewById(R.id.summary_menu);

        ImageView back = (ImageView) findViewById(R.id.summary_back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        home.setVisibility(View.INVISIBLE);
        save.setVisibility(View.VISIBLE);
        menu.setVisibility(View.INVISIBLE);

        summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                home.setVisibility(View.INVISIBLE);
                save.setVisibility(View.VISIBLE);
                menu.setVisibility(View.INVISIBLE);
            }
        });



        String mode = getIntent().getStringExtra("mode_summary");
        String mode_save = getIntent().getStringExtra("mode_summary_save");

        if ("new_summary".equals(mode)) {

            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            String memoKey = sharedPreferences.getString("latestMemoKey", null);
            summary.setText(content);
            summary_memoId = memoKey;
            System.out.println("New Summary " + summary_memoId);

        } else if ("old_summary".equals(mode)) {
            if("old_summary_save".equals(mode_save)){
                summary_memoId = SaveAudioActivity.newMemoKey;
                System.out.println("Old Summary Save" + summary_memoId);

            }
            else {
                summary_memoId = NoteAdapter.noteKeys.get(NoteAdapter.selected.get(0));
                System.out.println("Old Summary" + summary_memoId);

            }
        }
        else{
            System.out.println("No mode");
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

                    if(MainActivity.curTab.equals("Home")){
                        mDatabase.child("Users").child(uid).child("folder").child("Home").child("memos").child(summary_memoId).child("summary").setValue(summary_content);
                    }
                    else {
                        memoReference = mDatabase.child("Users").child(uid).child("folder").child(MainActivity.curTab).child("memos").child(summary_memoId);
                        memoReference.child("summary").setValue(summary_content);
                        // Update the note in the Home tab as well
                        mDatabase.child("Users").child(uid).child("folder").child("Home").child("memos").child(summary_memoId).child("summary").setValue(summary_content);
                    }
                    //Is this right?
                       // newMemoKey = memoId.get(0);


                save.setVisibility(View.INVISIBLE);
                home.setVisibility(View.VISIBLE);
                menu.setVisibility(View.VISIBLE);
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
            System.out.println("Fetch: " + summary_memoId);
            fetchVocabFromFirebase();
        }






    }

    @Override
    public void onStart() {
        super.onStart();
        fetchVocabFromFirebase();
    }

    private void fetchVocabFromFirebase() {
        summaryReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                old_summary = dataSnapshot.getValue().toString();
                summary.setText(old_summary);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    public void onBackPressed(){
        super.onBackPressed();
    }

}
