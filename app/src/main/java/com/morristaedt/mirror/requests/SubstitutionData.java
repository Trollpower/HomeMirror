package com.morristaedt.mirror.requests;

import java.util.ArrayList;

/**
 * Created by Trollpower on 19.02.2016. Data model to hold pulled substitution data
 */
public class SubstitutionData {
    private String SomeText;

    public ArrayList<SubstitutionRow> getSubstitutionPlan() {
        return substitutionPlan;
    }

    public void setSubstitutionPlan(ArrayList<SubstitutionRow> substitutionPlan) {
        this.substitutionPlan = substitutionPlan;
    }

    public String getSomeText() {
        return SomeText;
    }

    public void setSomeText(String someText) {
        SomeText = someText;
    }

    private ArrayList<SubstitutionRow> substitutionPlan;
}
