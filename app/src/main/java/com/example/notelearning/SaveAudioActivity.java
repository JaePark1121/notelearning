package com.example.notelearning;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;


public class SaveAudioActivity extends AppCompatActivity {

    public static EditText contentView;
    public static EditText titleView;

    public static String newMemoKey;


    static ArrayList<String> words = new ArrayList<>();
    static ArrayList<String> definitions = new ArrayList<>();


    public static String uid;
    public static HashMap<String, Folder> userData = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_audio_file);


      /*
        Intent myIntent = getIntent();
        String content = myIntent.getStringExtra("content");
        String title = myIntent.getStringExtra("title");
*/


        TextView save = (TextView) findViewById(R.id.save_button);
        titleView = (EditText) findViewById(R.id.save_notebook_title);
        contentView = (EditText) findViewById(R.id.save_audio_notes);
        TextView dateView = (TextView) findViewById(R.id.save_notes_date);

        ImageView home = (ImageView) findViewById(R.id.saved_home2);
        ImageView menu = (ImageView) findViewById(R.id.saved_menu2);

        home.setVisibility(View.INVISIBLE);
        menu.setVisibility(View.INVISIBLE);


        String mode = getIntent().getStringExtra("mode");
         // This will be the memo's key if in edit mode
        ArrayList<String> memoId = getIntent().getStringArrayListExtra("memoID");


        if ("edit".equals(mode)) {
            fetchMemoFromFirebase(memoId.get(MainRecyclerFragment.uidIndex));
        } else if ("create".equals(mode)) {
            contentView.setText(getIntent().getStringExtra("content"));
            titleView.setText(getIntent().getStringExtra("title"));
        }


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

                String title = titleView.getText().toString();
                String content = contentView.getText().toString();
                String date = dateView.getText().toString();

                Memos newMemo = new Memos(date, title, content, false, "", null);
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                DatabaseReference memoReference;
                System.out.println("CurrentTab: " + MainActivity.curTab);
                if ("edit".equals(mode)) {
                    memoReference = mDatabase.child("Users").child(uid).child("folder").child(MainActivity.curTab).child("memos").child(memoId.get(0));
                    memoReference.setValue(newMemo);
                    // Update the note in the Home tab as well
                    mDatabase.child("Users").child(uid).child("folder").child("Home").child("memos").child(memoId.get(0)).setValue(newMemo);
                } else {
                    memoReference = mDatabase.child("Users").child(uid).child("folder").child(MainActivity.curTab).child("memos").push();
                    newMemoKey = memoReference.getKey();
                    System.out.println();
                    mDatabase.child("Users").child(uid).child("folder").child("Home").child("memos").child(newMemoKey).setValue(newMemo);
                    memoReference.setValue(newMemo);
                }
                //memoReference.setValue(newMemo);



                save.setVisibility(View.INVISIBLE);
                home.setVisibility(View.VISIBLE);
                menu.setVisibility(View.VISIBLE);
            }

        });


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                // popup_layout이 팝업창으로 불러올 화면
                View popupView = inflater.inflate(R.layout.fragment_saved_menu, null);

                // ConstraintLayout 부분은 어떤 Layout인지 따라 달라짐, LinearLayout이면 LinearLayout.~~ 이런식
                int width = FrameLayout.LayoutParams.WRAP_CONTENT;
                int height = FrameLayout.LayoutParams.WRAP_CONTENT;
                //팝업창 바깥을 클릭했을 때 팝업 종료하기 기능, false면 꺼짐
                boolean focusable = true;
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                // AnyViewLayout은 현재 메인 Layout에 있는 View중 아무거나 가져오면 됨, 토큰용
                popupWindow.showAtLocation(menu, Gravity.CENTER, 300, -550);

                Button vocab = (Button) popupView.findViewById(R.id.vocab_button);
                vocab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showVocabChoiceDialog();

                    }
                });


                Button summary = (Button) popupView.findViewById(R.id.summary_button);
                summary.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                HttpURLConnection httpConn = null;
                                String response = "Error";

                                try {
                                    URL url = new URL("https://api.openai.com/v1/chat/completions");

                                    httpConn = (HttpURLConnection) url.openConnection();
                                    httpConn.setRequestMethod("POST");
                                    httpConn.setRequestProperty("Content-Type", "application/json");
                                    httpConn.setRequestProperty("Authorization", "Bearer " + "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");
                                    httpConn.setRequestProperty("OpenAI-Organization", "org-EXTJqre865s2Io0thmu6CYHX");
                                    httpConn.setDoOutput(true);

                                    String message = "Summarize this:" + contentView.getText().toString();
                                    JSONObject payload = new JSONObject();

                                    payload.put("model", "gpt-3.5-turbo");

                                    JSONArray messagesArray = new JSONArray();
                                    JSONObject messageObject = new JSONObject();
                                    messageObject.put("role", "user");
                                    messageObject.put("content", message);

                                    messagesArray.put(messageObject);
                                    payload.put("messages", messagesArray);

                                    OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
                                    writer.write(payload.toString());
                                    writer.flush();
                                    writer.close();

                                    int responseCode = httpConn.getResponseCode();
                                    //System.out.println("Response Code: " + responseCode);

                                    InputStream responseStream = responseCode / 100 == 2 ? httpConn.getInputStream() : httpConn.getErrorStream();
                                    Scanner s = new Scanner(responseStream).useDelimiter("\\A");

                                    response = s.hasNext() ? s.next() : "";
                                    // System.out.println(response);

                                    s.close();
                                    responseStream.close();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    JSONArray choicesArray = jsonResponse.getJSONArray("choices");

                                    if (choicesArray.length() > 0) {
                                        JSONObject firstChoice = choicesArray.getJSONObject(0);
                                        if (firstChoice.has("message")) {
                                            JSONObject messageObject = firstChoice.getJSONObject("message");
                                            String content = messageObject.getString("content");

                                            Intent intent = new Intent(getApplicationContext(), SummaryActivity.class);
                                            intent.putExtra("content", content);
                                            startActivity(intent);
                                        } else {
                                            System.out.println("The 'message' object is not found.");
                                        }
                                    } else {
                                        System.out.println("The 'choices' array is empty.");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    System.out.println("Error parsing the JSON response.");
                                }
                            }
                        });
                    }
                });

                Button delete = (Button) popupView.findViewById(R.id.delete_button);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Dismiss the first popup
                        popupWindow.dismiss();

                        LayoutInflater inflater = (LayoutInflater)
                                getSystemService(LAYOUT_INFLATER_SERVICE);
                        View popupView2 = inflater.inflate(R.layout.fragment_confirm_delete, null);

                        int width = FrameLayout.LayoutParams.WRAP_CONTENT;
                        int height = FrameLayout.LayoutParams.WRAP_CONTENT;
                        boolean focusable = true;
                        final PopupWindow popupWindow2 = new PopupWindow(popupView2, width, height, focusable);

                        popupWindow2.showAtLocation(delete, Gravity.CENTER, 0, 0);

                        Button confirm = (Button) popupView2.findViewById(R.id.confirm_delete_btn);
                        confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
                                startActivity(intent);
                            }
                        });

                        Button cancel = (Button) popupView2.findViewById(R.id.confirm_cancel_btn);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                popupWindow2.dismiss();
                            }
                        });
                    }
                });


            }

        });


        ImageView saveAudioBack = (ImageView) findViewById(R.id.save_audio_back_button);
        saveAudioBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
                startActivity(intent);
            }
        });

        titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                home.setVisibility(View.INVISIBLE);
                menu.setVisibility(View.INVISIBLE);
                save.setVisibility(View.VISIBLE);
            }
        });

        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                home.setVisibility(View.INVISIBLE);
                menu.setVisibility(View.INVISIBLE);
                save.setVisibility(View.VISIBLE);
            }
        });
    }

    private void fetchMemoFromFirebase(String memoId) {
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference memoReference = mDatabase.child("Users").child(uid).child("folder").child("Home").child("memos").child(memoId);

        memoReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Memos existingMemo = dataSnapshot.getValue(Memos.class);
                if (existingMemo != null) {
                    titleView.setText(existingMemo.getTitle());
                    contentView.setText(existingMemo.getContent());
                    // Set other fields if needed
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error here
                Toast.makeText(SaveAudioActivity.this, "Failed to load memo.", Toast.LENGTH_SHORT).show();
            }
        });
    }

        private void showVocabChoiceDialog () {
            AlertDialog.Builder builder = new AlertDialog.Builder(SaveAudioActivity.this); // Note the change here
            builder.setTitle("Vocab List Options");

            // Set up the buttons
            builder.setPositiveButton("View Existing", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getApplicationContext(), VocabListActivity.class);
                    intent.putExtra("mode_vocab", "old_list");
                    intent.putExtra("mode_vocab_save", "old_list_save");

                    startActivity(intent);
                }
            });
            builder.setNegativeButton("Create New", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            HttpURLConnection httpConn = null;
                            String response = "Error";

                            try {
                                URL url = new URL("https://api.openai.com/v1/chat/completions");

                                httpConn = (HttpURLConnection) url.openConnection();
                                httpConn.setRequestMethod("POST");
                                httpConn.setRequestProperty("Content-Type", "application/json");
                                httpConn.setRequestProperty("Authorization", "Bearer " + "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");
                                httpConn.setRequestProperty("OpenAI-Organization", "org-EXTJqre865s2Io0thmu6CYHX");
                                httpConn.setDoOutput(true);

                                String message = "Make a vocab list from this note, separate the word and the " +
                                        "definition with a colon and only use the colon once, don't number each word and don't " +
                                        "output anything else but the words and definitions. The vocab word you choose shouldn't " +
                                        "be a phrase but should be a single word or a single term and don't use articles. For example, only respond in this" +
                                        "kind of format 'Apple: A fruit.' This is the note:" + contentView.getText().toString();
                                JSONObject payload = new JSONObject();

                                payload.put("model", "gpt-3.5-turbo");

                                JSONArray messagesArray = new JSONArray();
                                JSONObject messageObject = new JSONObject();
                                messageObject.put("role", "user");
                                messageObject.put("content", message);

                                messagesArray.put(messageObject);
                                payload.put("messages", messagesArray);

                                OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
                                writer.write(payload.toString());
                                writer.flush();
                                writer.close();

                                int responseCode = httpConn.getResponseCode();
                                System.out.println("Response Code: " + responseCode);

                                InputStream responseStream = responseCode / 100 == 2 ? httpConn.getInputStream() : httpConn.getErrorStream();
                                Scanner s = new Scanner(responseStream).useDelimiter("\\A");

                                response = s.hasNext() ? s.next() : "";
                                System.out.println(response);

                                s.close();
                                responseStream.close();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                JSONArray choicesArray = jsonResponse.getJSONArray("choices");

                                if (choicesArray.length() > 0) {
                                    JSONObject firstChoice = choicesArray.getJSONObject(0);
                                    if (firstChoice.has("message")) {
                                        JSONObject messageObject = firstChoice.getJSONObject("message");
                                        String content = messageObject.getString("content");
                                        System.out.println(content);


                                        String[] entries = content.split("\n"); // Split by \n

                                        for (String entry : entries) {
                                            String[] parts = entry.split(":"); // Split each entry by colon

                                            if (parts.length == 2) {
                                                words.add(parts[0].trim());
                                                definitions.add(parts[1].trim());
                                            }
                                        }

                                    } else {
                                        System.out.println("The 'message' object is not found.");
                                    }
                                } else {
                                    System.out.println("The 'choices' array is empty.");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                System.out.println("Error parsing the JSON response.");
                            }
                            onTaskComplete();
                        }
                    });


                }
            });


            builder.show();
        }


    private void onTaskComplete() {
        for (int i = 0; i < definitions.size(); i++) {
            String a = definitions.get(i);
            System.out.println(a);
        }
        System.out.println("taskComplete: "+newMemoKey);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("latestMemoKey", newMemoKey);
        editor.apply();


        Intent intent = new Intent(getApplicationContext(), VocabListActivity.class);
        intent.putExtra("words", words);
        intent.putExtra("definitions", definitions);
        intent.putExtra("mode_vocab", "new_list");
        startActivity(intent);
    }
}