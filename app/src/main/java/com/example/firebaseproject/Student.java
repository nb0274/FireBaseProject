package com.example.firebaseproject;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
public class Student implements Parcelable {
    private String privateName, familyName, id;
    private int grade, classNum;
    private boolean canImmune;
    private Vaccine firstVaccine, secondVaccine;

    public Student(String privateName, String familyName, String id, int grade, int classNum, boolean canImmune, Vaccine firstVaccine, Vaccine secondVaccine) {
        this.privateName = privateName;
        this.familyName = familyName;
        this.id = id;
        this.grade = grade;
        this.classNum = classNum;
        this.canImmune = canImmune;
        this.firstVaccine = new Vaccine(firstVaccine);
        this.secondVaccine = new Vaccine(secondVaccine);
    }

    public Student() {
        this.privateName = "";
        this.familyName = "";
        this.id = "";
        this.grade = 0;
        this.classNum = 0;
        this.canImmune = false;
        this.firstVaccine = new Vaccine();
        this.secondVaccine = new Vaccine();
    }

    public String getPrivateName() {
        return this.privateName;
    }

    public String getFamilyName() {
        return this.familyName;
    }

    public String getId() {
        return this.id;
    }

    public int getGrade() {
        return this.grade;
    }

    public int getClassNum() {
        return this.classNum;
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

    public void setPrivateName(String name) {
        this.privateName = name;
    }

    public void setFamilyName(String name) {
        this.familyName = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public void setClassNum(int classNum) {
        this.classNum = classNum;
    }

    public void setCanImmune(boolean canImmune) {
        this.canImmune = canImmune;
    }

    public void setFirstVaccine(Vaccine vaccine) {
        this.firstVaccine = new Vaccine(vaccine);
    }

    public void setSecondVaccine(Vaccine vaccine) {
        this.secondVaccine = new Vaccine(vaccine);
    }

    protected Student(Parcel in) {
        privateName = in.readString();
        familyName = in.readString();
        id = in.readString();
        grade = in.readInt();
        classNum = in.readInt();
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
        parcel.writeString(privateName);
        parcel.writeString(familyName);
        parcel.writeString(id);
        parcel.writeInt(grade);
        parcel.writeInt(classNum);
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
