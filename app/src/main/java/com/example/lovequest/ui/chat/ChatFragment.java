package com.example.lovequest.ui.chat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lovequest.R;
import com.example.lovequest.adapter.SearchUserRecyclerAdapter;
import com.example.lovequest.model.UserModel;
import com.example.lovequest.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import com.example.lovequest.databinding.FragmentChatBinding;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;

    EditText searchInput;
    ImageButton searchButton;
    RecyclerView recyclerView;
    SearchUserRecyclerAdapter adapter;
    private ChatViewModel chatViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        // Initialize views
        searchInput = root.findViewById(R.id.seach_username_input);
        searchButton = root.findViewById(R.id.search_user_btn);
        recyclerView = root.findViewById(R.id.search_user_recycler_view);

        // Observe the ViewModel's LiveData
        chatViewModel.getFilteredUsersLiveData().observe(getViewLifecycleOwner(), this::setupSearchRecyclerView);

        // Set up the text change listener for the search input
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String searchTerm = editable.toString();
                if (searchTerm.isEmpty() || searchTerm.length() < 3) {
                    // Clear the RecyclerView when the search input is empty or too short
                    setupSearchRecyclerView(null);
                } else {
                    // Perform a search with the provided search term
                    performSearch(searchTerm);
                }
            }
        });

        return root;
    }

    void setupSearchRecyclerView(List<UserModel> filteredUsers) {
        if (adapter != null) {
            adapter.stopListening(); // Stop listening to the previous query
        }

        FirebaseUtil.getUserReference().get().addOnSuccessListener(documentSnapshot -> {
            UserModel currentUser = documentSnapshot.toObject(UserModel.class);
            if (currentUser != null && currentUser.getGender() != null) {
                String oppositeGender = currentUser.getGender().equalsIgnoreCase("Male") ? "Female" : "Male";

                Query query;
                if (filteredUsers != null) {
                    // If filteredUsers is provided, show the filtered list
                    query = FirebaseUtil.getAllUserReference()
                            .whereIn("userId", getFilteredUserIds(filteredUsers))
                            .orderBy("username"); // You can add sorting if needed
                } else {
                    // If filteredUsers is null, show users of the opposite gender
                    query = FirebaseUtil.getAllUserReference()
                            .whereEqualTo("gender", oppositeGender)
                            .orderBy("username"); // You can add sorting if needed
                }

                FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                        .setQuery(query, snapshot -> {
                            UserModel userModel = snapshot.toObject(UserModel.class);
                            userModel.setUserId(snapshot.getId());
                            return userModel;
                        })
                        .setLifecycleOwner(this)
                        .build();

                adapter = new SearchUserRecyclerAdapter(options, getContext());
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(adapter);
                recyclerView.setItemAnimator(null);
                adapter.startListening();
            }
        });
    }

    private List<String> getFilteredUserIds(List<UserModel> filteredUsers) {
        List<String> userIds = new ArrayList<>();
        for (UserModel user : filteredUsers) {
            userIds.add(user.getUserId());
        }
        return userIds;
    }

    private void performSearch(String searchTerm) {
        // Perform a search query based on the searchTerm
        // Replace the following line with your actual search query
        Query searchQuery = FirebaseUtil.getAllUserReference()
                .orderBy("username")
                .startAt(searchTerm)
                .endAt(searchTerm + "\uf8ff");

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(searchQuery, snapshot -> {
                    UserModel userModel = snapshot.toObject(UserModel.class);
                    userModel.setUserId(snapshot.getId());
                    return userModel;
                })
                .setLifecycleOwner(this)
                .build();

        adapter = new SearchUserRecyclerAdapter(options, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
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