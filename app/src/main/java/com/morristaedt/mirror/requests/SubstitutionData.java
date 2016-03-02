package com.morristaedt.mirror.requests;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Trollpower on 19.02.2016. Data model to hold pulled substitution data
 */
public class SubstitutionData {
    private String someText;
    private boolean substitutionFound;
    private Date requestedDate;

    public Date getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(Date requestedDate) {
        this.requestedDate = requestedDate;
    }

    public boolean isSubstitutionFound() {
        return substitutionFound;
    }

    public void setSubstitutionFound(boolean substitutionFound) {
        this.substitutionFound = substitutionFound;
    }

    public ArrayList<SubstitutionRow> getSubstitutionPlan() {
        return substitutionPlan;
    }

    public void setSubstitutionPlan(ArrayList<SubstitutionRow> substitutionPlan) {
        this.substitutionPlan = substitutionPlan;
    }

    public String getSomeText() {
        return someText;
    }

    public void setSomeText(String someText) {
        someText = someText;
    }

    private ArrayList<SubstitutionRow> substitutionPlan;

    public ArrayList<String> getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(ArrayList<String> announcements) {
        this.announcements = announcements;
    }

    private ArrayList<String> announcements;
}
