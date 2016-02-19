package com.morristaedt.mirror.modules;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.morristaedt.mirror.requests.SubstitutionData;

import java.util.Random;

/**
 * Created by Trollpower on 19.02.2016.
 * Module to frequently pull substitution data from Killians school.
 */
public class SubstitutionModule {
    public interface SubstitutionListener {
        void onNewSubstitution(SubstitutionData substitutionData);
    }

    public static void getSubstitutions(final SubstitutionListener substitutionListener) {
        new AsyncTask<Void, Void, SubstitutionData>() {
            String title = null;
            String details = null;

            @Override
            protected void onPostExecute(@Nullable SubstitutionData substitutionData) {
                substitutionListener.onNewSubstitution(substitutionData);
            }

            @Override
            protected SubstitutionData doInBackground(Void... params) {
                SubstitutionData sd = new SubstitutionData();
                Random r = new Random();
                int i1 = r.nextInt(1000);
                sd.SomeText = "Neue Zahl: " + i1;
                return sd;
            }
        }.execute();
    }
}
