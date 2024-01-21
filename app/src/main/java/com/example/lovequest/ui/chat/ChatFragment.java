package com.example.lovequest.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lovequest.R;
import com.example.lovequest.adapter.SearchUserRecyclerAdapter;
import com.example.lovequest.model.UserModel;
import com.example.lovequest.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import com.example.lovequest.databinding.FragmentChatBinding;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;

    EditText searchInput;
    ImageButton searchButton;
    RecyclerView recyclerView;
    SearchUserRecyclerAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize views
        searchInput = root.findViewById(R.id.seach_username_input);
        searchButton = root.findViewById(R.id.search_user_btn);
        recyclerView = root.findViewById(R.id.search_user_recycler_view);

        // Load all users initially
        setupSearchRecyclerView("");

        // Setting up the search button click listener
        searchButton.setOnClickListener(v -> {
            String searchTerm = searchInput.getText().toString();
            if(searchTerm.isEmpty() || searchTerm.length() < 3){
                searchInput.setError("Invalid Username");
                return;
            }
            setupSearchRecyclerView(searchTerm);
        });

        return root;
    }

    void setupSearchRecyclerView(String searchTerm) {
        Query query;
        if (searchTerm.isEmpty()) {
            // Load all users
            query = FirebaseUtil.getAllUserReference();
        } else {
            // Filter based on search term
            query = FirebaseUtil.getAllUserReference()
                    .whereGreaterThanOrEqualTo("username", searchTerm)
                    .whereLessThanOrEqualTo("username", searchTerm + '\uf8ff');
        }

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class).build();

        if (adapter != null) {
            adapter.stopListening(); // Stop listening to the previous query
        }
        adapter = new SearchUserRecyclerAdapter(options, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(null);
        recyclerView.setAdapter(adapter);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null && !task.getResult().isEmpty()) {
                    // Log or handle successful search
                } else {
                    Toast.makeText(getContext(), "No users found.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Error fetching users.", Toast.LENGTH_SHORT).show();
                // Handle any errors
            }
        });

        adapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (adapter != null) {
            adapter.stopListening();
        }
        binding = null;
    }
}
