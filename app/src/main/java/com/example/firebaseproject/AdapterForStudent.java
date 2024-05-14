package com.example.firebaseproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.firebaseproject.Student;

import java.util.ArrayList;

/**
 * StateStudent class:
 * a custom adapter between a list of Student objects, and a given spinner/list view.
 * @author Ori Roitzaid <or1901 @ bs.amalnet.k12.il>
 * @version	1
 * @since 7/5/2024
 */
public class AdapterForStudent extends BaseAdapter {
    private ArrayList<Student> studentsList;
    LayoutInflater inflater;

    /**
     * A constructor which initializes the adapter with a given context, and students list.
     * @param context The context to use the adapter in.
     * @param studentsList The list of students to use in the adapter.
     */
    public AdapterForStudent(@NonNull Context context, ArrayList<Student> studentsList) {
        this.studentsList = studentsList;
        inflater = (LayoutInflater.from(context));
    }

    /**
     * This function gets the number of students in the adapter.
     * @return The number of students in the adapter
     */
    @Override
    public int getCount() {
        return studentsList.size();
    }

    /**
     * This function gets the item in a given position in the Students list.
     * @param i The given position.
     * @return The item in the given position in the Students list.
     */
    @Override
    public Object getItem(int i) {
        return studentsList.get(i);
    }

    /**
     * This function gets the id of a students from the list(basically its position).
     * @param i The student position in the list.
     * @return The id of the given Student's position.
     */
    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * This function makes a view object for a given Student in the list(it makes each row in the
     * spinner).
     * @param position The position of the Student in the list.
     * @param view The view object to use.
     * @param parent The ViewGroup - probably the spinner view.
     * @return A view object for the given Student in the list.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        view = inflater.inflate(R.layout.sudent_list_view, parent, false);

        TextView lvTvName = view.findViewById(R.id.lvTvName);
        TextView lvTvGrade = view.findViewById(R.id.lvTvGrade);
        TextView lvTvClass = view.findViewById(R.id.lvTvClass);
        TextView lvTvId = view.findViewById(R.id.lvTvId);
        ImageView lvImVaccine1 = view.findViewById(R.id.lvImVaccine1);
        ImageView lvImVaccine2 = view.findViewById(R.id.lvImVaccine2);

        Student student = studentsList.get(position);

        lvTvName.setText(student.getFirstName() + " " + student.getLastName());
        lvTvGrade.setText(student.getGrade() + "th grade");
        lvTvClass.setText("Class " + student.getClassNumber());
        lvTvId.setText("Id: " + student.getID());

        if(!student.getCanImmune())
        {
            lvImVaccine1.setImageResource(R.drawable.cannot_immune);
        }
        else
        {
            if(!student.getFirstVaccine().getPlaceTaken().isEmpty())
            {
                lvImVaccine1.setImageResource(R.drawable.vaccine);
            }
            if(!student.getSecondVaccine().getPlaceTaken().isEmpty())
            {
                lvImVaccine2.setImageResource(R.drawable.vaccine);
            }
        }

        return view;
    }
}
