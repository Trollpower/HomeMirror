package com.morristaedt.mirror.modules;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.morristaedt.mirror.requests.SubstitutionData;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
                InputStream substitutionSite = GetSubstitutionSiteStream();
                if (substitutionSite == null) {
                    return null;
                }

                Calendar c = Calendar.getInstance();
                String val = ParseSubstitutionSite(substitutionSite, c.getTime());
                SubstitutionData sd = new SubstitutionData();
                Random r = new Random();
                int i1 = r.nextInt(1000);
                //sd.SomeText = "Neue Zahl: " + i1;
                sd.SomeText = val;
                return sd;
            }
        }.execute();
    }

    private static String ParseSubstitutionSite(InputStream siteStream, Date targetDate) {
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        return df.format(targetDate);
    }

    private static String GetSubstitutionSite() {
        Header[] header = GetHeaderDataFromGet("http://gunzelinrs.silberkamp.de/embed/index.php");
        try {
            HttpPost httppost = new HttpPost("http://gunzelinrs.silberkamp.de/embed/login.php");
            for (int i = 0; i < header.length; i++) {
                httppost.addHeader("Set-Cookie", (header[i].getValue()));
            }
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("username", "ki.meyer"));
            nameValuePairs.add(new BasicNameValuePair("password", "IsgMa2009"));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpClient postClient = new DefaultHttpClient();
            HttpResponse response2 = postClient.execute(httppost);

            HttpEntity entity = response2.getEntity();
            InputStream mstream = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(mstream, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String result = sb.toString();
            return result;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "";
    }

    private static InputStream GetSubstitutionSiteStream() {
        Header[] header = GetHeaderDataFromGet("http://gunzelinrs.silberkamp.de/embed/index.php");
        try {
            HttpPost httppost = new HttpPost("http://gunzelinrs.silberkamp.de/embed/login.php");
            for (int i = 0; i < header.length; i++) {
                httppost.addHeader("Set-Cookie", (header[i].getValue()));
            }
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("username", "ki.meyer"));
            nameValuePairs.add(new BasicNameValuePair("password", "IsgMa2009"));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpClient postClient = new DefaultHttpClient();
            HttpResponse response2 = postClient.execute(httppost);

            HttpEntity entity = response2.getEntity();
            InputStream mstream = entity.getContent();
            return mstream;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private static Header[] GetHeaderDataFromGet(String url) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = null;
        try {
            response = httpclient.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Response des Gets verarbeiten
        //Cookies holen
        Header[] header = response.getHeaders("Set-Cookie");
        return header;
    }
}
