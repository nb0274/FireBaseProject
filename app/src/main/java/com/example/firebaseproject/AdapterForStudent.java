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

import java.util.ArrayList;

public class AdapterForStudent extends BaseAdapter {
    private ArrayList<Student> lst;
    LayoutInflater inflater;

    /**
     * This function is called when the view is created.
     */
    public AdapterForStudent(@NonNull Context context, ArrayList<Student> studentsList) {
        this.lst = studentsList;
        inflater = (LayoutInflater.from(context));
    }

    /**
     * This function is called when the view is created.
     */
    @Override
    public int getCount() {
        return lst.size();
    }

    /**
     * This function is called when the view is created.
     */
    @Override
    public Object getItem(int i) {
        return lst.get(i);
    }

    /**
     * This function is called when the view is created.
     */
    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * This function is called when the view is created.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        view = inflater.inflate(R.layout.sudent_list_view, parent, false);

        TextView lvTvName = view.findViewById(R.id.stName);
        TextView lvTvGrade = view.findViewById(R.id.stGrade);
        TextView lvTvClass = view.findViewById(R.id.stClass);
        TextView lvTvId = view.findViewById(R.id.stId);
        ImageView lvImVaccine1 = view.findViewById(R.id.firstVac);
        ImageView lvImVaccine2 = view.findViewById(R.id.secondVac);

        Student student = lst.get(position);

        lvTvName.setText(student.getFirstName() + " " + student.getLastName());
        lvTvGrade.setText(student.getGrade() + "th grade");
        lvTvClass.setText("Class " + student.getClassNumber());
        lvTvId.setText("Id: " + student.getID());

        if(!student.getCanImmune())
        {
            lvImVaccine1.setImageResource(R.drawable.nuhuh);
        }
        else
        {
            if(!student.getFirstVaccine().getPlaceTaken().isEmpty())
            {
                lvImVaccine1.setImageResource(R.drawable.goodjob);
            }
            if(!student.getSecondVaccine().getPlaceTaken().isEmpty())
            {
                lvImVaccine2.setImageResource(R.drawable.goodjob);
            }
        }

        return view;
    }
}
