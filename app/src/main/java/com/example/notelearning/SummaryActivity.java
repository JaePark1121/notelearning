package com.example.notelearning;

import static com.example.notelearning.SaveAudioActivity.contentView;

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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class SummaryActivity extends AppCompatActivity {

    //OkHttpClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        Intent intent = getIntent();
        String content = intent.getStringExtra("content");


        EditText summary = (EditText) findViewById(R.id.summary_notes);
        summary.setText(content);


        ImageView home = (ImageView) findViewById(R.id.summary_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        ImageView summary_back = (ImageView) findViewById(R.id.summary_back);
        summary_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ImageView menu = (ImageView) findViewById(R.id.summary_menu);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                // popup_layout이 팝업창으로 불러올 화면
                View popupView = inflater.inflate(R.layout.fragment_summary_menu, null);

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
    }

            private void showVocabChoiceDialog () {
                AlertDialog.Builder builder = new AlertDialog.Builder(SummaryActivity.this); // Note the change here
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

                                    String message = "Make a short vocab list of 5 words from this note, separate the word and the " +
                                            "definition with a colon and only use the colon once, don't number each word and don't " +
                                            "output anything else but the words and definitions. The vocab word you choose shouldn't " +
                                            "be a phrase but should be a single word or a single term and don't use articles. Strictly respond in this" +
                                            "format 'Apple: A fruit.' This is the note:" + contentView.getText().toString();
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

                                                System.out.println("Parts: " + entry);

                                                if (parts.length == 2) {
                                                   SaveAudioActivity.words.add(parts[0].trim());
                                                    SaveAudioActivity.definitions.add(parts[1].trim());
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

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    private void onTaskComplete() {
        for (int i = 0; i < SaveAudioActivity.definitions.size(); i++) {
            //  String a = definitions.get(i);
            //  System.out.println(a);
        }
        System.out.println("taskComplete: "+SaveAudioActivity.newMemoKey);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("latestMemoKey", SaveAudioActivity.newMemoKey);
        editor.apply();


        Intent intent = new Intent(getApplicationContext(), VocabListActivity.class);
        intent.putExtra("words", SaveAudioActivity.words);
        intent.putExtra("definitions", SaveAudioActivity.definitions);
        intent.putExtra("mode_vocab", "new_list");
        startActivity(intent);
    }
}
