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

import com.example.firebaseproject.Student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentsDisplay extends AppCompatActivity implements View.OnCreateContextMenuListener, AdapterView.OnItemLongClickListener {
    ListView lvStudents;
    ArrayList<Student> studentsList;
    StudentAdapter studentsAdapter;
    Context activityContext;
    AlertDialog.Builder adb;
    AlertDialog ad;
    int selectedStudentPosition;
    Intent gi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_students);

        initViews();
    }

    protected void onStart() {
        super.onStart();

        gi = getIntent();
        initStudentsList();
    }

    /**
     * This function initializes the views objects, and all the activity objects.
     */
    private void initViews() {
        lvStudents = findViewById(R.id.lvStudents);
        lvStudents.setOnCreateContextMenuListener(this);
        lvStudents.setOnItemLongClickListener(this);

        studentsList = new ArrayList<Student>();
        studentsAdapter = new StudentAdapter(this, studentsList);
        lvStudents.setAdapter(studentsAdapter);

        activityContext = this;
        selectedStudentPosition = 0;
    }

    /**
     * This function saves all the existing students in the DB into the array list of students.
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

                studentsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activityContext, "Failed reading from the DB!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * This function creates the context menu of actions to perform with the chosen student from the
     * list view.
     * @param menu The context menu object.
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Student Actions");
        menu.add("Show & Edit");
        menu.add("Delete");
    }

    /**
     * This function reacts to the choice from the context menu of actions on the chosen Student -
     * it validates the choice with alert dialog, and performs the desired action.
     * @param item The chosen menu item.
     * @return true for the menu to react, false otherwise.
     */
    public boolean onContextItemSelected(MenuItem item) {
        String action = item.getTitle().toString();

        adb = new AlertDialog.Builder(this);
        adb.setCancelable(false);
        adb.setTitle("Perform action on Student");
        adb.setMessage("Are you sure you want to " + action.toLowerCase() + " the student?");

        // Validates the choice with the user
        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(action.equals("Show & Edit"))
                {
                    // Goes to the AddStudentActivity and displays there
                    gi = new Intent(activityContext, AddStudentActivity.class);
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
     * This function deletes a given student from the DB and the students array list. Also updates
     * the change in the students list view.
     * @param studentIndex The index of the student to delete in the students array list.
     */
    private void deleteStudent(int studentIndex) {
        Student student = studentsList.get(studentIndex);

        // Deletes from the DB
        if(student.getCanImmune()) {
            REF_STUDENTS.child("CanImmune").child("" + student.getGrade())
                    .child("" + student.getClassNum()).child(student.getId()).removeValue();
        }
        else {
            REF_STUDENTS.child("CannotImmune").child("" + student.getGrade())
                    .child("" + student.getClassNum()).child(student.getId()).removeValue();
        }

        studentsList.remove(studentIndex);
        studentsAdapter.notifyDataSetChanged();

        Toast.makeText(this, "Student was deleted!", Toast.LENGTH_SHORT).show();
    }

    /**
     * This function presents the options menu for moving between activities.
     * @param menu the options menu in which you place your items.
     * @return true in order to show the menu, otherwise false.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This function reacts to the user choice in the options menu - it moves to the chosen
     * activity from the menu, or resets the current one.
     * @param item the menu item that was selected.
     * @return must return true for the menu to react.
     */
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();

        if(id == R.id.menuAddStudent) {
            gi = new Intent(this, AddStudentActivity.class);
            startActivity(gi);
        }
        else if(id == R.id.menuSortAndFilter) {
            gi.setClass(this, SortAndFilterActivity.class);
            startActivity(gi);
        }
        else if(id == R.id.menuCredits) {
            gi.setClass(this, CreditsActivity.class);
            startActivity(gi);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This function saves the index of the chosen student from the list view(when long clicked).
     * @param adapterView The adapter view of the students list view.
     * @param view The view object of the selected student in the lv.
     * @param i The position of the chosen student in the list view.
     * @param l The row of the chosen student in the list view.
     * @return false.
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        selectedStudentPosition = i;

        return false;
    }
}