package com.example.notelearning;

import static com.example.notelearning.MainActivity.newFragment2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class MainSearchFragment extends Fragment {

    private RecyclerView searchRV;
    private SearchAdapter adapter;
    public static HashMap<String, String> searchArrayList;


    View v;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_main_search, container, false);
        SearchView searchView = (SearchView) v.findViewById(R.id.search_view);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                System.out.println("bbb" + query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                System.out.println("aaa" + newText);
                return true;
            }
        });
        searchRV = v.findViewById(R.id.search_recycler_view);

        buildRecyclerView();


        v.findViewById(R.id.close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(newFragment2).commitAllowingStateLoss();
            }
        });

        return v;
    }



    private void filter(String text) {
        // creating a new array list to filter our data.
        ArrayList<String> filteredlist = new ArrayList<String>();

        // running a for loop to compare elements.
        int a = 0;
        for (String item : searchArrayList.keySet()) {
            // checking if the entered string matched with any item of our recycler view.
            if (!text.equals("")) {
                if (searchArrayList.get(item).contains(text)) {
                    // if the item is matched we are
                    // adding it to our filtered list.
                    filteredlist.add(item);
                    a++;
                }
            }
        }
        if(a == 0){
            filteredlist.clear();
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            //Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            adapter.filterList(filteredlist);
        }
    }

    private void buildRecyclerView() {

        // below line we are creating a new array list
        searchArrayList = new HashMap<String, String>();

        FirebaseDatabase.getInstance().getReference().child("Users").child(MainActivity.uid).child("folder")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds:snapshot.getChildren()){
                            if(!ds.getKey().equals("Bookmark")){
                                for(DataSnapshot dschild:ds.child("memos").getChildren()) {
                                    searchArrayList.put(dschild.getKey(), dschild.child("title").getValue().toString());
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        // initializing our adapter class.
        adapter = new SearchAdapter(new ArrayList<>(searchArrayList.keySet()));

        // adding layout manager to our recycler view.
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        searchRV.setHasFixedSize(true);

        // setting layout manager
        // to our recycler view.
        searchRV.setLayoutManager(manager);

        // setting adapter to
        // our recycler view.
        searchRV.setAdapter(adapter);

        adapter.setOnItemClickListener(new SearchAdapter.OnItemClickEventListener() {
            @Override
            public void onItemClick(View a_view, int a_position) {

                Intent intent = new Intent(getContext(), SaveAudioActivity.class);
                ArrayList<String> memoUID = new ArrayList<>();
                memoUID.add(adapter.dataList.get(a_position));
                intent.putExtra("memoID", memoUID);
                System.out.println(memoUID);
                intent.putExtra("title",searchArrayList.get(memoUID));
                intent.putExtra("mode", "edit");
                startActivity(intent);
            }
        });
    }




}