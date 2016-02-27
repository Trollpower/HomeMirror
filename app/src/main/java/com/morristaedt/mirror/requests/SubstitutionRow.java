package com.morristaedt.mirror.requests;

/**
 * Created by Trollpower on 27.02.2016.
 */
public class SubstitutionRow {
    private String teacher;
    private String lesson;
    private String className;
    private String subject;
    private String room;

    public String getLesson() {
        return lesson;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String toString(){
        return "Stunde: " + this.lesson + "\r\nFach: " + this.subject + "\r\nLehrer: " + this.teacher + "\r\nRaum: " + this.room;
    }
}
