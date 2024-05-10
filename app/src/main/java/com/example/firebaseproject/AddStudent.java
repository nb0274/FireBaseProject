package com.example.firebaseproject;

import static com.example.firebaseproject.FBRef.REF_STUDENTS;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AddStudentActivity extends AppCompatActivity {
    String[] grades = {"7th", "8th", "9th", "10th", "11th", "12th"};
    Spinner spGrades;
    ArrayAdapter<String> spinnerAdp;
    AlertDialog.Builder adb;
    LinearLayout vaccineDialog;
    EditText dialogEtPlace, dialogEtDate, etPrivateName, etFamilyName, etId, etClass;
    Switch swCanImmune;
    int currentVaccine;
    Vaccine[] vaccinesData;
    Context activityContext;
    Intent si;
    ArrayList<String> idsList;

    DialogInterface.OnClickListener onDialogBtnClick = new DialogInterface.OnClickListener() {

        /**
         * This function reacts to the choice of the user in the vaccine alert dialog.
         * @param dialog The vaccine alert dialog.
         * @param which The alert dialog button clicked.
         */
        @Override
        public void onClick(DialogInterface dialog, int which) {

            // Save button
            if(which == DialogInterface.BUTTON_POSITIVE) {
                if(dialogEtPlace.getText().toString().isEmpty() || dialogEtDate.getText().toString().isEmpty()) {
                    Toast.makeText(activityContext, "Place or date are empty!", Toast.LENGTH_SHORT).show();
                }
                else {
                    vaccinesData[currentVaccine].setPlaceTaken(dialogEtPlace.getText().toString());
                    Toast.makeText(activityContext, "Vaccine " + (currentVaccine + 1) + " saved!", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_add_student);

        initViews();
    }

    @Override
    protected void onStart(){
        super.onStart();

        getSavedIds();
    }

    private void initViews() {
        etPrivateName = findViewById(R.id.EditTextFirstName);
        etFamilyName = findViewById(R.id.EditTextLastName);
        etId = findViewById(R.id.EditTextID);
        etClass = findViewById(R.id.etClass);
        spGrades = findViewById(R.id.spGrades);
        swCanImmune = findViewById(R.id.swCanImmune);

        spinnerAdp = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, grades);
        spGrades.setAdapter(spinnerAdp);

        currentVaccine = 0;
        vaccinesData = new Vaccine[2];
        vaccinesData[0] = new Vaccine();
        vaccinesData[1] = new Vaccine();

        activityContext = this;
        si = new Intent();
        idsList = new ArrayList<>();
    }

    /**
     * This function displays an alert dialog to input the details of a vaccine.
     * @param vaccineNum The number of the vaccine to get its details.
     */
    private void displayVaccineDialog(int vaccineNum) {
        vaccineDialog = (LinearLayout) getLayoutInflater().inflate(R.layout.vaccine_alert_dialog, null);

        dialogEtPlace = vaccineDialog.findViewById(R.id.vaccineDialogEditTextSite);
        dialogEtDate = vaccineDialog.findViewById(R.id.vaccineDialogEditTextDate);

        adb = new AlertDialog.Builder(this);

        adb.setView(vaccineDialog);
        adb.setTitle("Vaccine " + vaccineNum + " Info");
        adb.setCancelable(false);

        adb.setPositiveButton("Save", onDialogBtnClick);
        adb.setNegativeButton("Cancel", onDialogBtnClick);

        adb.show();
    }

    /**
     * This function displays the first vaccine alert dialog when the suitable button is clicked.
     * @param view The view object of the button that was clicked.
     */
    public void getFirstVaccineData(View view) {
        if(swCanImmune.isChecked()) {
            currentVaccine = 0;
            displayVaccineDialog(1);
        }
        else {
            Toast.makeText(activityContext, "Student can't immune!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This function displays the second vaccine alert dialog when the suitable button is clicked.
     * @param view The view object of the button that was clicked.
     */
    public void getSecondVaccineData(View view) {
        if(swCanImmune.isChecked()) {
            currentVaccine = 1;
            displayVaccineDialog(2);
        }
        else {
            Toast.makeText(activityContext, "Student can't immune!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This function opens a date picker dialog when the user clicks on the select date edit text
     * in the vaccine alert dialog. It saves the user's choice in the current vaccine instance.
     * @param view The view object of the select date edit text.
     */
    public void dialogChooseDate(View view) {
        // Saves the current time details, in order to display it in the picker
        Calendar nowCalendar = Calendar.getInstance();
        int year = nowCalendar.get(Calendar.YEAR);
        int month = nowCalendar.get(Calendar.MONTH);
        int day = nowCalendar.get(Calendar.DAY_OF_MONTH);
        Calendar chosenDate = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AddStudentActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    /**
                     * This function saves the user's choice in the date picker.
                     * @param view The view object of the date picker.
                     * @param year The selected year.
                     * @param monthOfYear The selected month.
                     * @param dayOfMonth The selected day.
                     */
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        chosenDate.set(year, monthOfYear, dayOfMonth);

                        if(chosenDate.after(nowCalendar)) {
                            Toast.makeText(activityContext, "You can't choose a future date!",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            dialogEtDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            vaccinesData[currentVaccine].setDate(chosenDate.getTimeInMillis());
                        }
                    }
                },
                year, month, day);

        datePickerDialog.show();
    }

    /**
     * This function checks if all the edit text fields contain content in them.
     * @return Whether all the edit text fields contain content, or not.
     */
    private boolean areFieldsFull() {
        return (!etPrivateName.getText().toString().isEmpty()) &&
                (!etFamilyName.getText().toString().isEmpty()) &&
                (!etId.getText().toString().isEmpty()) &&
                (!etClass.getText().toString().isEmpty());
    }

    /**
     * This function checks if the class the user entered is valid(bigger than 0).
     * @return Whether the class the user entered is valid, or not.
     */
    private boolean isValidClass() {
        return Integer.parseInt(etClass.getText().toString()) > 0;
    }

    /**
     * This function gets a Student instance initialized with the current data saved in the
     * activity views.
     * @return A Student instance initialized with the current data saved in the activity views.
     */
    private Student getCurrentStudent() {
        return new Student(etPrivateName.getText().toString(),
                etFamilyName.getText().toString(), etId.getText().toString(),
                getSelectedGrade(),
                Integer.parseInt(etClass.getText().toString()), swCanImmune.isChecked(),
                vaccinesData[0], vaccinesData[1]
        );
    }

    /**
     * This function resets the date field of the saved vaccines if there isn't a place saved in
     * them - may occur when the user chose only date and then closed the alert dialog.
     */
    private void resetEmptyVaccines() {
        if(vaccinesData[0].getPlaceTaken() == null) {
            vaccinesData[0] = null;
        }
        if(vaccinesData[1].getPlaceTaken() == null) {
            vaccinesData[1] = null;
        }
    }

    /**
     * This function gets the selected grade in the grades spinner.
     * @return The selected grade in the grades spinner.
     */
    private int getSelectedGrade() {
        return spGrades.getSelectedItemPosition() + 7;
    }

    /**
     * This function gets all the students ids from the DB, and saves it in the ids array list.
     */
    private void getSavedIds() {
        REF_STUDENTS.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idsList.clear();

                for(DataSnapshot gradeData : snapshot.getChildren())
                {
                    for(DataSnapshot classData : gradeData.getChildren())
                    {
                        for(DataSnapshot studData : classData.getChildren())
                        {
                            idsList.add(studData.getKey());
                        }
                    }
                }
            }
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activityContext, "Failed reading from the DB!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * This function saves the current Student's data in the DB - if all the fields are valid.
     * @param view The view object of the button that was clicked in order to save the Student.
     */
    public void saveStudent(View view) {
        if(areFieldsFull())
        {
            if(isValidClass())
            {
                if(!idsList.contains(etId.getText().toString()))  // If id doesn't exist
                {
                    resetEmptyVaccines();
                    Student student = getCurrentStudent();

                    REF_STUDENTS.child("" + getSelectedGrade())
                            .child(etClass.getText().toString())
                            .child(etId.getText().toString()).setValue(student);

                    Toast.makeText(activityContext, "Student saved!",
                            Toast.LENGTH_SHORT).show();

                    idsList.add(etId.getText().toString());  // Adds the new student id to the list

                    vaccinesData[0] = new Vaccine();
                    vaccinesData[1] = new Vaccine();
                }

                else {
                    Toast.makeText(activityContext, "Student id already exists!",
                            Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(activityContext, "Class number isn't valid!",
                        Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(activityContext, "There is an empty field!",
                    Toast.LENGTH_SHORT).show();
        }
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

        if(id == R.id.menuAllStudents){
            si.setClass(this, AllStudentsActivity.class);
            startActivity(si);
        }

        return super.onOptionsItemSelected(item);
    }
}