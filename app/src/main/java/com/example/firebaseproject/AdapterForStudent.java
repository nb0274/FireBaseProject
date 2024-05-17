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

public class AdapterForStudent extends BaseAdapter {
    private ArrayList<Student> studentsList;
    LayoutInflater inflater;

    /**
     * This function initializes the adapter.
     * @param context The context of the activity.
     * @param studentsList The list of students.
     */
    public AdapterForStudent(@NonNull Context context, ArrayList<Student> studentsList) {
        this.studentsList = studentsList;
        inflater = (LayoutInflater.from(context));
    }

    /**
     * This function returns the number of students in the list.
     * @return The number of students in the list.
     */
    @Override
    public int getCount() {
        return studentsList.size();
    }

    /**
     * This function returns the student at the given position.
     * @param i The position of the student.
     * @return The student at the given position.
     */
    @Override
    public Object getItem(int i) {
        return studentsList.get(i);
    }

    /**
     * This function returns the ID of the student at the given position.
     * @param i The position of the student.
     * @return The ID of the student at the given position.
     */
    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * This function returns the view of the student at the given position.
     * @param position The position of the student.
     * @param view The view of the student.
     * @param parent The parent of the view.
     * @return The view of the student at the given position.
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
