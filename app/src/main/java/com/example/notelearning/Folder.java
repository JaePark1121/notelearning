package com.example.notelearning;

import java.util.ArrayList;
import java.util.HashMap;

public class Folder {
    private String name;
    private HashMap<String, Memos> memos;
    private ArrayList<Memos> bookmarkedMemos;

    public Folder() {
        this.memos = new HashMap<>();
        this.bookmarkedMemos = new ArrayList<>();
    }
    public Folder(String name) {
        this.name = name;
        this.memos = new HashMap<>();
        this.bookmarkedMemos = new ArrayList<>();
    }

    public Folder(String name, HashMap memos) {
        this.name = name;
        this.memos = memos;

    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Memos> getMemos() {
        return this.memos;
    }

    public void setMemos(HashMap<String, Memos> memos) {
        this.memos = memos;
    }

    public ArrayList<Memos> getBookmarkedMemos() {
        return this.bookmarkedMemos;
    }


}
