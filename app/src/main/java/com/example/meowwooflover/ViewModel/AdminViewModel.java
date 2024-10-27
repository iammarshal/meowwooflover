package com.example.meowwooflover.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.meowwooflover.Model.ItemsModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminViewModel extends ViewModel {
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference itemsReference = firebaseDatabase.getReference("Items");

    private MutableLiveData<List<ItemsModel>> _bestSeller = new MutableLiveData<>();

    public LiveData<List<ItemsModel>> getBestSeller() {
        return _bestSeller;
    }

    public void loadBestSeller() {
        itemsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<ItemsModel> lists = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    ItemsModel list = childSnapshot.getValue(ItemsModel.class);
                    if (list != null) {
                        lists.add(list);
                    }
                }
                _bestSeller.setValue(lists);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error
            }
        });
    }

    // Delete item method
    public void deleteItem(String path, OnDeleteItemListener listener) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference(path);
        databaseRef.removeValue()
                .addOnSuccessListener(aVoid -> listener.onDeleteSuccess())
                .addOnFailureListener(listener::onDeleteFailure);
    }

    // Interface for delete operation callback
    public interface OnDeleteItemListener {
        void onDeleteSuccess();
        void onDeleteFailure(Exception e);
    }


    public interface OnUpdateItemListener {
        void onUpdateSuccess();
        void onUpdateFailure(Exception e);
    }
}
