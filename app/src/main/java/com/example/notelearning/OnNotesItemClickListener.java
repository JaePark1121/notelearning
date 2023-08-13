package com.example.notelearning;

import android.view.KeyEvent;
import android.view.View;

public interface OnNotesItemClickListener {

    void onLongClick();

    void close();

    public void onItemClick(NoteAdapter.ViewHolder holder, View view, int position);

    boolean onKeyDown(int keycode, KeyEvent event);
}
