package com.example.firebaseproject;

import static com.example.firebaseproject.FBRef.REF_STUDENTS;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentsDisplay extends AppCompatActivity implements View.OnCreateContextMenuListener, AdapterView.OnItemLongClickListener {
    ListView lvStudents;
    ArrayList<Student> studentsList;
    AdapterForStudent adapterForStudent;
    Context context;
    AlertDialog.Builder adb;
    AlertDialog ad;
    int selectedStudentPosition;
    Intent gi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_display);

        lvStudents = findViewById(R.id.lvStudents);
        lvStudents.setOnCreateContextMenuListener(this);
        lvStudents.setOnItemLongClickListener(this);

        studentsList = new ArrayList<Student>();
        adapterForStudent = new AdapterForStudent(this, studentsList);
        lvStudents.setAdapter(adapterForStudent);

        context = this;
        selectedStudentPosition = 0;
    }

    /**
     * This function is called when the activity is started.
     */
    protected void onStart() {
        super.onStart();

        gi = getIntent();
        initStudentsList();
    }

    /**
     * This function initializes the students list from the DB.
     */
    private void initStudentsList() {
        REF_STUDENTS.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentsList.clear();

                for(DataSnapshot canImmunedData : snapshot.getChildren()) {
                    for (DataSnapshot gradeData : canImmunedData.getChildren()) {
                        for (DataSnapshot classData : gradeData.getChildren()) {
                            for (DataSnapshot studData : classData.getChildren()) {
                                studentsList.add(studData.getValue(Student.class));
                            }
                        }
                    }
                }

                adapterForStudent.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Failed reading from the DB!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * This function is called when a context menu is created.
     * @param menu The context menu that was created.
     * @param v The view that was clicked.
     * @param menuInfo Extra information about the item that was clicked.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Actions");
        menu.add("Show & Edit");
        menu.add("Delete");
    }

    /**
     * This function is called when an item in the context menu is selected.
     * @param item The item that was selected.
     * @return True if the item was selected, false otherwise.
     */
    public boolean onContextItemSelected(MenuItem item) {
        String action = item.getTitle().toString();

        adb = new AlertDialog.Builder(this);
        adb.setCancelable(false);
        adb.setTitle("Action on Student");
        adb.setMessage("Are you sure you want to " + action.toLowerCase() + "?");

        // Validates the choice with the user
        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(action.equals("Show & Edit"))
                {
                    // Goes to the AddStudentActivity and displays there
                    gi = new Intent(context, AddStudent.class);
                    gi.putExtra("Student", studentsList.get(selectedStudentPosition));
                    startActivity(gi);
                }
                else
                {
                    deleteStudent(selectedStudentPosition);
                }
            }
        });

        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        ad = adb.create();
        ad.show();

        return super.onContextItemSelected(item);
    }

    /**
     * This function deletes a student from the list and the DB.
     * @param studentIndex The index of the student to delete.
     */
    private void deleteStudent(int studentIndex) {
        Student student = studentsList.get(studentIndex);

        // Deletes from the DB
        if(student.getCanImmune()) {
            REF_STUDENTS.child("CanImmune").child("" + student.getGrade())
                    .child("" + student.getClassNumber()).child(student.getID()).removeValue();
        }
        else {
            REF_STUDENTS.child("CannotImmune").child("" + student.getGrade())
                    .child("" + student.getClassNumber()).child(student.getID()).removeValue();
        }

        studentsList.remove(studentIndex);
        adapterForStudent.notifyDataSetChanged();

        Toast.makeText(this, "Student deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This function is called when an item in the menu is selected.
     * @param item The item that was selected.
     * @return True if the item was selected, false otherwise.
     */
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();

        if(id == R.id.menuAddStudent) {
            gi = new Intent(this, AddStudent.class);
            startActivity(gi);
        }
        else if(id == R.id.menuCredits) {
            gi.setClass(this, Credits.class);
            startActivity(gi);
        }
        else if(id == R.id.menuSortStudents) {
            gi.setClass(this, SortActivity.class);
            startActivity(gi);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This function is called when a student is long clicked.
     * @param adapterView The AdapterView that was clicked.
     * @param view The view that was clicked.
     * @param i The position of the item in the adapter.
     * @param l The row id of the item that was clicked.
     * @return True if the callback consumed the long click, false otherwise.
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        selectedStudentPosition = i;

        return false;
    }
}