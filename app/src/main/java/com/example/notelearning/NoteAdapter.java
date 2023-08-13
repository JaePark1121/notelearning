package com.example.notelearning;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> implements OnNotesItemClickListener {

    public static ArrayList<String> selectedMemoUIDs = new ArrayList<>();

    private static void updateBookmarkInFirebase(String key, boolean isBookmarked) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference currentMemoRef = ref.child("Users").child(MainActivity.uid).child("folder").child(MainActivity.curTab).child("memos").child(key);
        DatabaseReference bookmarkMemoRef = ref.child("Users").child(MainActivity.uid).child("folder").child("Bookmark").child("memos").child(key);

        currentMemoRef.child("isBookmarked").setValue(isBookmarked);

        if(isBookmarked) {
            currentMemoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    bookmarkMemoRef.setValue(dataSnapshot.getValue());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } else {
            bookmarkMemoRef.removeValue();
        }
    }




    @Override
    public void onLongClick() {
        if (listener != null) {
            listener.onLongClick();
        }
    }

    @Override
    public void close() {
        if (listener != null) {
            listener.close();
        }
    }

    public static ArrayList<Integer> selected = new ArrayList<>();
    public static ArrayList<String> noteKeys = new ArrayList<>();

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView notebook;
        TextView date;
        TextView title;
        ImageButton bookmark;

        public ViewHolder(View itemView, final OnNotesItemClickListener listener) {
            super(itemView);
            notebook = itemView.findViewById(R.id.notebook_box);
            date = itemView.findViewById(R.id.date);
            title = itemView.findViewById(R.id.note_title);
            bookmark = itemView.findViewById(R.id.bookmark_btn);

            bookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        String key = noteKeys.get(position);
                        Note currentNote = items.get(position);
                        boolean currentStatus = currentNote.isBookmarked();
                        updateBookmarkInFirebase(key, !currentStatus);

                        if (currentStatus) {
                            bookmark.setImageResource(R.drawable.ribbon);
                            System.out.println("Deselect: " + key);
                            currentNote.setBookmarked(false);
                        } else {
                            bookmark.setImageResource(R.drawable.ribbon_colored);
                            currentNote.setBookmarked(true);
                            System.out.println("Select " + key);
                        }
                    }
                }
            });



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    String memoUID = noteKeys.get(position);

                    if (selected.isEmpty()) {
                        if (listener != null) {
                            listener.onItemClick(ViewHolder.this, view, position);
                        }
                    } else {
                        if (selected.contains(position)) {
                            selected.remove(Integer.valueOf(position));
                            selectedMemoUIDs.remove(memoUID);
                            view.findViewById(R.id.notebook_box).setBackgroundResource(R.drawable.notebook_default);
                            if (selected.isEmpty()) {
                                listener.close();
                            }
                        } else {
                            selected.add(position);
                            selectedMemoUIDs.add(memoUID);
                            view.findViewById(R.id.notebook_box).setBackgroundResource(R.drawable.notebook_selected);
                            view.findViewById(R.id.notebook_box).setBackgroundResource(R.drawable.ripple);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = getAdapterPosition();
                    String memoUID = noteKeys.get(pos);

                    if (selected.isEmpty()) {
                        selected.add(pos);
                        selectedMemoUIDs.add(memoUID);
                        view.findViewById(R.id.notebook_box).setBackgroundResource(R.drawable.notebook_selected);
                        view.findViewById(R.id.notebook_box).setBackgroundResource(R.drawable.ripple);
                        if (listener != null) {
                            listener.onLongClick();
                        }
                    }
                    return true;
                }
            });

        }




        public void setItem(Note item) {
            date.setText(item.getDate());
            title.setText(item.getTitle());
        }
    }

    static ArrayList<Note> items = new ArrayList<Note>();
    OnNotesItemClickListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.fragment_main_notes, viewGroup, false);
        return new ViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Note item = items.get(position);
        viewHolder.setItem(item);
        if (!selected.contains(position)) {
            viewHolder.itemView.findViewById(R.id.notebook_box).setBackgroundResource(R.drawable.notebook_default);
        }
        ImageButton bookmark = viewHolder.itemView.findViewById(R.id.bookmark_btn);
        if (!item.isBookmarked()) {
            bookmark.setImageResource(R.drawable.ribbon);
            System.out.println("Deselect: ");

        } else {
            bookmark.setImageResource(R.drawable.ribbon_colored);

            System.out.println("Select " );
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Note item, String key) {
        items.add(item);
        noteKeys.add(key);
    }

    public void setItems(ArrayList<Note> items) {
        this.items = items;
    }

    public Note getItem(int position) {
        return items.get(position);
    }

    public static ArrayList<String> getSelectedMemoUIDs() {
        return selectedMemoUIDs;
    }

    public ArrayList<String> getNoteKeys() {
        return noteKeys;
    }


    public void setItem(int position, Note item) {
        items.set(position, item);
    }

    public void setOnItemClickListener(OnNotesItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {
        if (listener != null) {
            listener.onItemClick(holder, view, position);
        }
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        return false;
    }
}
