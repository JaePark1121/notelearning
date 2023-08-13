package com.example.notelearning;

import java.util.HashMap;

public class Memos {
    private String date;
    private String title;
    private String content;
    public boolean isBookmarked;
    private String summary;

    private HashMap<String,String> vocabList;

    public Memos() {
    }

    public Memos(String date, String title, String content, boolean isBookmarked, String summary, HashMap<String, String> vocabList) {
        this.date = date;
        this.title = title;
        this.content = content;
        this.isBookmarked = isBookmarked;
        this.summary = summary;
        this.vocabList = vocabList;
    }

    public  String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public  String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public  boolean getIsBookmarked() {
        return this.isBookmarked;
    }

    public  void setIsBookmarked(boolean isBookmarked) {
        this.isBookmarked = isBookmarked;
    }

    public String getSummary() {
        return this.summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public HashMap<String, String> getVocabList(){return this.vocabList;}
    public void setVocabList(HashMap<String, String> vocabList){this.vocabList = vocabList;}
}

