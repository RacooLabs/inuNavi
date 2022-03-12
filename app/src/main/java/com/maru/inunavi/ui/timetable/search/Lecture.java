package com.maru.inunavi.ui.timetable.search;

public class Lecture {

    int id; // 학과
    String department;
    String grade; // 학년
    String category;
    String number;
    String lecturename;
    String professor;
    String classroom_raw;
    String classtime_raw;
    String classroom;
    String classtime;
    String how;
    int point;


    String color;
    String realTime;


    public Lecture(int id, String department, String grade, String category, String number, String lecturename, String professor, String classroom_raw, String classtime_raw, String classroom, String classtime, String how, int point, String realTime) {
        this.id = id;
        this.department = department;
        this.grade = grade;
        this.category = category;
        this.number = number;
        this.lecturename = lecturename;
        this.professor = professor;
        this.classroom_raw = classroom_raw;
        this.classtime_raw = classtime_raw;
        this.classroom = classroom;
        this.classtime = classtime;
        this.how = how;
        this.point = point;
        this.realTime = realTime;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLecturename() {
        return lecturename;
    }

    public void setLecturename(String lecturename) {
        this.lecturename = lecturename;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public String getClassroom_raw() {
        return classroom_raw;
    }

    public void setClassroom_raw(String classroom_raw) {
        this.classroom_raw = classroom_raw;
    }

    public String getClasstime_raw() {
        return classtime_raw;
    }

    public void setClasstime_raw(String classtime_raw) {
        this.classtime_raw = classtime_raw;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getClasstime() {
        return classtime;
    }

    public void setClasstime(String classtime) {
        this.classtime = classtime;
    }

    public String getHow() {
        return how;
    }

    public void setHow(String how) {
        this.how = how;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setRealTime(String realTime) {this.realTime = realTime;}

    public String getRealTime() { return realTime;}
}
