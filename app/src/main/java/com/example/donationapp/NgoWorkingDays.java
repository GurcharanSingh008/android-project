package com.example.donationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NgoWorkingDays extends AppCompatActivity {
    ListView workingDaysListView;
    ArrayAdapter<String> categoryAdapter;
    String[] outputCategory = new String[7];
    String workingDays = "";
    FirebaseAuth mAuth;
    String uId;
    DatabaseReference  userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ngo_working_days);
        mAuth = FirebaseAuth.getInstance();
        uId = mAuth.getCurrentUser().getUid();

        Bundle b = getIntent().getExtras();
        final String[] resultArr = b.getStringArray("Category");


        workingDaysListView = findViewById(R.id.listView);
        String[] categories = getResources().getStringArray(R.array.WorkingDays);
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, categories);
        workingDaysListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        workingDaysListView.setAdapter(categoryAdapter);
        findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workingDays = "";
                SparseBooleanArray categoryChecked = workingDaysListView.getCheckedItemPositions();
                ArrayList<String> selectedCategory = new ArrayList<>();

                for (int i = 0; i < categoryChecked.size(); i++) {
                    int position = categoryChecked.keyAt(i);
                    if (categoryChecked.valueAt(i)) {

                        selectedCategory.add(categoryAdapter.getItem(position));

                    }

                }
                outputCategory = new String[selectedCategory.size()];

                for (int i = 0; i < selectedCategory.size(); i++) {
                    outputCategory[i] = selectedCategory.get(i);

                }

                if (outputCategory.length == 0) {
                    Toast.makeText(getApplicationContext(), "Please Select Working Days", Toast.LENGTH_SHORT).show();
                } else {
                    final ProgressDialog progressDialog = new ProgressDialog(NgoWorkingDays.this);
                    progressDialog.setMessage("Please wait");
                    progressDialog.show();
                    for (String string : outputCategory) {
                        workingDays = workingDays + string + " ";
                    }
                    for (String str : resultArr) {
                        DatabaseReference weekData=FirebaseDatabase.getInstance().getReference().child("User").child("NGO").child(uId).child(str);
                        Map<String, Object> userDetails = new HashMap<>();
                        userDetails.put("Working Days", workingDays);
                        weekData.updateChildren(userDetails);
                    }
                    // user data update karne ke liye
                    userDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(uId);
                    Map<String, Object> userDetails = new HashMap<>();
                    userDetails.put("Working Days", workingDays);
                    userDatabase.updateChildren(userDetails);
                    userDatabase.updateChildren(userDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent intent1 = new Intent(NgoWorkingDays.this, MainActivity.class);

                            progressDialog.dismiss();
                            startActivity(intent1);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });


                }
            }


        });

    }
}



