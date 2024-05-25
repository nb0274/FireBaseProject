package com.example.firebaseproject;


import static com.example.firebaseproject.FBRef.REF_STUDENTS;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firebaseproject.Student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SortActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String[] filterOptions = {"Immune students of a given class", "Immune students of a given grade", "All immune students", "All students that can't immune"};
    Spinner spFilterOptions, classDialogSpGrade, classDialogSpClass, gradeDialogSpGrade;
    ArrayAdapter<String> filtersSpinnerAdp;
    ArrayAdapter<Integer> gradesSpinnerAdp, classesSpinnerAdp;
    Intent gi;
    AlertDialog.Builder adb;
    AlertDialog ad;
    LinearLayout classDetailsDialog, gradeDetailsDialog;
    ArrayList<Integer> gradesList, currentClassesList;
    Context activityContext;
    ArrayList<ArrayList<Integer>> classesList;
    ArrayList<Student> studentsList;
    AdapterForStudent studentsAdapter;
    ListView lvFilteredStudents;

    DialogInterface.OnClickListener onClassDialogBtnClick = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            int chosenGrade = 0, chosenClass = 0;

            // Apply button
            if(which == DialogInterface.BUTTON_POSITIVE) {
                if(gradesList.isEmpty()) {
                    Toast.makeText(activityContext, "There are no existing classes!", Toast.LENGTH_LONG).show();
                }
                else {
                    chosenGrade = gradesList.get(classDialogSpGrade.getSelectedItemPosition());
                    chosenClass = classesList.get(classDialogSpGrade.getSelectedItemPosition()).get(classDialogSpClass.getSelectedItemPosition());

                    showClassImmunedStudents(chosenGrade, chosenClass);
                }
            }

            // Cancel button
            else if(which == DialogInterface.BUTTON_NEGATIVE) {
                dialog.cancel();
            }

        }
    };

    DialogInterface.OnClickListener onGradeDialogBtnClick = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            // Save button
            if(which == DialogInterface.BUTTON_POSITIVE) {
                if(gradesList.isEmpty()) {
                    Toast.makeText(activityContext, "There are no existing grades!", Toast.LENGTH_LONG).show();
                }
                else {
                    showGradeImmunedStudents(gradesList.get(gradeDialogSpGrade.getSelectedItemPosition()));
                }
            }

            // Cancel button
            else if(which == DialogInterface.BUTTON_NEGATIVE) {
                dialog.cancel();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);

        spFilterOptions = findViewById(R.id.spFilterOptions);
        lvFilteredStudents = findViewById(R.id.lvFilteredStudents);

        filtersSpinnerAdp = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, filterOptions);
        spFilterOptions.setAdapter(filtersSpinnerAdp);

        gradesList = new ArrayList<>();
        currentClassesList = new ArrayList<>();
        classesList = new ArrayList<>();
        activityContext = this;
        studentsList = new ArrayList<>();
        studentsAdapter = new AdapterForStudent(this, studentsList);

        lvFilteredStudents.setAdapter(studentsAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        gi = getIntent();
    }

    public void applyFilter(View view) {
        int filter = spFilterOptions.getSelectedItemPosition();

        switch(filter){
            case 0:
                displayClassDialog();
                break;

            case 1:
                displayGradeDialog();
                break;

            case 2:
                showAllImmunedStudents();
                break;

            case 3:
                showUnimmuneStudents();
                break;

        }
    }

    private void displayClassDialog() {
        classDetailsDialog = (LinearLayout) getLayoutInflater().inflate(R.layout.class_alert_dialog, null);

        classDialogSpGrade = classDetailsDialog.findViewById(R.id.classDialogSpGrade);
        classDialogSpClass = classDetailsDialog.findViewById(R.id.classDialogSpClass);

        saveGradesAndClasses(0);
        gradesSpinnerAdp = new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, gradesList);
        classesSpinnerAdp = new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, currentClassesList);

        classDialogSpGrade.setAdapter(gradesSpinnerAdp);
        classDialogSpClass.setAdapter(classesSpinnerAdp);

        classDialogSpGrade.setOnItemSelectedListener(this);

        adb = new AlertDialog.Builder(this);

        adb.setView(classDetailsDialog);
        adb.setTitle("Class Info");
        adb.setCancelable(false);

        adb.setPositiveButton("Apply", onClassDialogBtnClick);
        adb.setNegativeButton("Cancel", onClassDialogBtnClick);

        ad = adb.create();
        ad.show();
    }

    private void displayGradeDialog() {
        gradeDetailsDialog = (LinearLayout) getLayoutInflater().inflate(R.layout.grade_alert_dialog, null);

        gradeDialogSpGrade = gradeDetailsDialog.findViewById(R.id.gradeDialogSpGrade);

        saveGradesAndClasses(1);
        gradesSpinnerAdp = new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, gradesList);

        gradeDialogSpGrade.setAdapter(gradesSpinnerAdp);

        adb = new AlertDialog.Builder(this);

        adb.setView(gradeDetailsDialog);
        adb.setTitle("Grade Info");
        adb.setCancelable(false);

        adb.setPositiveButton("Apply", onGradeDialogBtnClick);
        adb.setNegativeButton("Cancel", onGradeDialogBtnClick);

        ad = adb.create();
        ad.show();
    }

    private void saveGradesAndClasses(int dialogNum) {
        REF_STUDENTS.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int currentGrade = 0, i = 0;
                gradesList.clear();
                classesList.clear();

                for(DataSnapshot canImmuneData : snapshot.getChildren())
                {
                    for (DataSnapshot gradeData : canImmuneData.getChildren())
                    {
                        currentGrade = Integer.parseInt(gradeData.getKey());

                        // Checks if the grade exists already in the list
                        if(!gradesList.contains(currentGrade)) {
                            classesList.add(new ArrayList<>());
                            gradesList.add(currentGrade);
                        }

                        for (DataSnapshot classData : gradeData.getChildren())
                        {
                            classesList.get(gradesList.indexOf(currentGrade)).add(Integer.parseInt(classData.getKey()));
                        }

                        i++;
                    }
                }

                gradesSpinnerAdp.notifyDataSetChanged();

                // Only if it's the class dialog, inits the classes spinner
                if(dialogNum == 0)
                    initClassSpinner();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activityContext, "Failed reading from the DB!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        copyArrayList(classesList.get(i), currentClassesList);
        classesSpinnerAdp.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    private void initClassSpinner() {
        if(!classesList.isEmpty()) {
            copyArrayList(classesList.get(0), currentClassesList);
            classesSpinnerAdp.notifyDataSetChanged();
        }
    }

    private void copyArrayList(ArrayList<Integer> src, ArrayList<Integer> dst) {
        dst.clear();
        dst.addAll(src);
    }

    private void showUnimmuneStudents() {
        REF_STUDENTS.child("CannotImmune").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                studentsList.clear();

                for(DataSnapshot gradeData : snapshot.getChildren())
                {
                    for(DataSnapshot classData : gradeData.getChildren())
                    {
                        for(DataSnapshot studData : classData.getChildren())
                        {
                            studentsList.add(studData.getValue(Student.class));
                            i++;
                        }
                    }
                }

                studentsAdapter.notifyDataSetChanged();

                if(i == 0) {
                    Toast.makeText(activityContext, "No unimmune students exists!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activityContext, "Failed reading from the DB!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAllImmunedStudents() {
        REF_STUDENTS.child("CanImmune").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                studentsList.clear();
                Student currentStud;

                for(DataSnapshot gradeData : snapshot.getChildren())
                {
                    for(DataSnapshot classData : gradeData.getChildren())
                    {
                        for(DataSnapshot studData : classData.getChildren())
                        {
                            currentStud = studData.getValue(Student.class);

                            if(isStudentImmune(currentStud)) {
                                studentsList.add(studData.getValue(Student.class));
                                i++;
                            }
                        }
                    }
                }

                studentsAdapter.notifyDataSetChanged();

                if(i == 0) {
                    Toast.makeText(activityContext, "No immune students exists!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activityContext, "Failed reading from the DB!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showClassImmunedStudents(int grade, int classNum) {
        Query query = REF_STUDENTS.child("CanImmune").child(grade + "").child(classNum + "").orderByChild("familyName");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                studentsList.clear();
                Student currentStud;

                for(DataSnapshot studData : snapshot.getChildren())
                {
                    currentStud = studData.getValue(Student.class);

                    if(isStudentImmune(currentStud)) {
                        studentsList.add(studData.getValue(Student.class));
                        i++;
                    }
                }

                studentsAdapter.notifyDataSetChanged();

                if(i == 0) {
                    Toast.makeText(activityContext, "No immune students exists in " + grade + "th grade, Class " + classNum, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activityContext, "Failed reading from the DB!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showGradeImmunedStudents(int grade) {
        REF_STUDENTS.child("CanImmune").child("" + grade).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int i = 0;
                        studentsList.clear();
                        Student currentStud;

                        for(DataSnapshot classData : snapshot.getChildren())
                        {
                            for(DataSnapshot studData : classData.getChildren())
                            {
                                currentStud = studData.getValue(Student.class);

                                if(isStudentImmune(currentStud)) {
                                    studentsList.add(studData.getValue(Student.class));
                                    i++;
                                }
                            }
                        }

                        studentsAdapter.notifyDataSetChanged();

                        if(i == 0) {
                            Toast.makeText(activityContext, "No immune students exists in " + grade + "th grade!", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(activityContext, "Failed reading from the DB!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isStudentImmune(Student student) {
        return (!student.getFirstVaccine().getPlaceTaken().isEmpty()) && (!student.getSecondVaccine().getPlaceTaken().isEmpty());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();

        if(id == R.id.menuAddStudent) {
            gi = new Intent(this, AddStudent.class);
            startActivity(gi);
        }
        else if(id == R.id.menuShowStudents) {
            gi.setClass(this, StudentsDisplay.class);
            startActivity(gi);
        }
        else if(id == R.id.menuCredits) {
            gi.setClass(this, Credits.class);
            startActivity(gi);
        }

        return super.onOptionsItemSelected(item);
    }
}