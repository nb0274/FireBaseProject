package com.example.firebaseproject;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * Student class:
 * saves data about each student - its name, grade, class, and 2 vaccines info.
 * @author Ori Roitzaid <or1901 @ bs.amalnet.k12.il>
 * @version	1
 * @since 12/4/2024
 */
public class Student implements Parcelable {
    private String firstName, lastName, id;
    private int grade, classNumber;
    private boolean canImmune;
    private Vaccine firstVaccine, secondVaccine;

    public Student(String privateName, String familyName, String id, int grade, int classNumber, boolean canImmune, Vaccine firstVaccine, Vaccine secondVaccine) {
        this.firstName = privateName;
        this.lastName = familyName;
        this.id = id;
        this.grade = grade;
        this.classNumber = classNumber;
        this.canImmune = canImmune;
        this.firstVaccine = firstVaccine;
        this.secondVaccine = secondVaccine;
    }

    public Student() {
        this.firstName = "";
        this.lastName = "";
        this.id = "";
        this.grade = 0;
        this.classNumber = 0;
        this.canImmune = false;
        this.firstVaccine = new Vaccine();
        this.secondVaccine = new Vaccine();
    }

    public Student(Student other) {
        this.firstName = other.firstName;
        this.lastName = other.lastName;
        this.id = other.id;
        this.grade = other.grade;
        this.classNumber = other.classNumber;
        this.canImmune = other.canImmune;
        this.firstVaccine = new Vaccine(other.firstVaccine);
        this.secondVaccine = new Vaccine(other.secondVaccine);
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getID() {
        return this.id;
    }

    public int getGrade() {
        return this.grade;
    }

    public int getClassNumber() {
        return this.classNumber;
    }

    public boolean getCanImmune() {
        return this.canImmune;
    }

    public Vaccine getFirstVaccine() {
        return this.firstVaccine;
    }

    public Vaccine getSecondVaccine() {
        return this.secondVaccine;
    }

    public void setFirstName(String name) {
        this.firstName = name;
    }

    public void setLastName(String name) {
        this.lastName = name;
    }

    public void setID(String id) {
        this.id = id;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public void setClassNumber(int classNum) {
        this.classNumber = classNum;
    }

    public void setCanImmune(boolean canImmune) {
        this.canImmune = canImmune;
    }

    public void setFirstVaccine(Vaccine vaccine) {
        this.firstVaccine = vaccine;
    }

    public void setSecondVaccine(Vaccine vaccine) {
        this.secondVaccine = vaccine;
    }

    // Parcelable implementation
    protected Student(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        id = in.readString();
        grade = in.readInt();
        classNumber = in.readInt();
        canImmune = in.readByte() != 0;
        firstVaccine = in.readParcelable(Vaccine.class.getClassLoader());
        secondVaccine = in.readParcelable(Vaccine.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeString(id);
        parcel.writeInt(grade);
        parcel.writeInt(classNumber);
        parcel.writeByte((byte) (canImmune ? 1 : 0));
        parcel.writeParcelable(firstVaccine, i);
        parcel.writeParcelable(secondVaccine, i);
    }

    public static final Creator<Student> CREATOR = new Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel in) {
            return new Student(in);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };
}