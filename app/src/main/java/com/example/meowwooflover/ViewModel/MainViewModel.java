package com.example.meowwooflover.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.meowwooflover.Model.SliderModel;
import com.example.meowwooflover.Model.CategoryModel;
import com.example.meowwooflover.Model.ItemsModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    private MutableLiveData<List<SliderModel>> _banner = new MutableLiveData<>();
    private MutableLiveData<List<CategoryModel>> _category = new MutableLiveData<>();
    private MutableLiveData<List<ItemsModel>> _bestSeller = new MutableLiveData<>();

    public LiveData<List<SliderModel>> getBanners() {
        return _banner;
    }

    public LiveData<List<CategoryModel>> getCategory() {
        return _category;
    }

    public LiveData<List<ItemsModel>> getBestSeller() {
        return _bestSeller;
    }

    public void loadBanners() {
        firebaseDatabase.getReference("Banner").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<SliderModel> lists = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    SliderModel list = childSnapshot.getValue(SliderModel.class);
                    if (list != null) {
                        lists.add(list);
                    }
                }
                _banner.setValue(lists);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error
            }
        });
    }

    public void loadCategory() {
        firebaseDatabase.getReference("Category").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<CategoryModel> lists = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    CategoryModel list = childSnapshot.getValue(CategoryModel.class);
                    if (list != null) {
                        lists.add(list);
                    }
                }
                _category.setValue(lists);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // TODO: Implement error handling
            }
        });
    }

    public void loadBestSeller() {
        firebaseDatabase.getReference("Items").addValueEventListener(new ValueEventListener() {
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
}