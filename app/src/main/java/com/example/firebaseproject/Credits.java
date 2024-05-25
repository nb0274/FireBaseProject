package com.example.firebaseproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Credits extends AppCompatActivity {
    Intent in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
    }

    protected void onStart() {
        super.onStart();

        in = getIntent();
    }

    /**
     * This function is called when the activity is started.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This function is called when the activity is started.
     */
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();

        if(id == R.id.menuAddStudent){
            in = new Intent(this,AddStudent.class);
            startActivity(in);
        }
        else if(id == R.id.menuShowStudents){
            in.setClass(this, StudentsDisplay.class);
            startActivity(in);
        }
        else if(id == R.id.menuSortStudents) {
            in.setClass(this, SortActivity.class);
            startActivity(in);
        }

        return super.onOptionsItemSelected(item);
    }
}