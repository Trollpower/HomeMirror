package com.morristaedt.mirror.modules;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.morristaedt.mirror.requests.SubstitutionData;
import com.morristaedt.mirror.requests.SubstitutionRow;

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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
import java.util.Locale;
import java.util.TimeZone;

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
                String substitutionSite = GetSubstitutionSite();
                if(substitutionSite == "")
                {
                    return null;
                }

                Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Germany"), Locale.GERMANY);
                Date sourceDate = c.getTime();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                if(hour >= 17 || !IsBusinessDay(sourceDate))
                    sourceDate = GetNextBusinessDay(sourceDate);

                SubstitutionData val = GetSubstitutionPartForDate(substitutionSite, sourceDate);
                return val;
            }
        }.execute();
    }

    private static SubstitutionData GetSubstitutionPartForDate(String substitutionCode, Date targetDate){
        org.jsoup.nodes.Document doc = Jsoup.parseBodyFragment(substitutionCode);
        SubstitutionData data = new SubstitutionData();
        data.setSubstitutionPlan(new ArrayList<SubstitutionRow>());
        data.setAnnouncements(new ArrayList<String>() );
        data.setRequestedDate(targetDate);

        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        //String targetSubstitutionDate = "10.03.2016";
        String targetSubstitutionDate = df.format(targetDate);

        Elements elements = doc.getElementsByTag("h4");
        int i = 0;
        for (Element element : elements) {
            i++;
            String text = element.text();
            if(text.contains(targetSubstitutionDate))
            {
                //Ziel-Plan gefunden, nun alles parsen bis zum nächsten <hr/>
                //Erst entweder <table> oder <span>. Bei yspan> gibts keine
                //Änderung
                Element table = GetNextElement(element, "table", "hr");

                if(table != null)
                {
                    ArrayList<SubstitutionRow> rows = GetTableRowData(table);
                    for (SubstitutionRow row : rows)
                    {
                        data.getSubstitutionPlan().add(row);
                    }
                }

                Element li = GetNextElement(element, "li", "hr");
                while(li != null)
                {
                    data.getAnnouncements().add(li.text());
                    li = GetNextElement(li.nextElementSibling(), "li", "hr");
                }

                if(data.getAnnouncements().size() > 0 || data.getSubstitutionPlan().size() > 0)
                {
                    data.setSubstitutionFound(true);
                }
            }
        }
        return data;
    }

    private static Element GetNextElement(Element source, String startTagName, String endTagName)
    {
        Element current = source;
        while (current != null) {
            if(current.tagName() == endTagName)
                return null;
            if(current.tagName() == startTagName)
                return current;
            current = current.nextElementSibling();
        }

        return null;
    }

    private static ArrayList<SubstitutionRow> GetTableRowData(Element tableElement){
        ArrayList<SubstitutionRow> rows = new ArrayList<SubstitutionRow>();
        Elements tableRows = tableElement.getElementsByTag("tr");
        for(int i = 1; i < tableRows.size(); i++)
        {
            Elements columns = tableRows.get(i).getElementsByTag("td");
            SubstitutionRow row = new SubstitutionRow();
            for (int j = 0; j < columns.size(); j++)
            {
                String tdText = columns.get(j).text();
                if(j == 0) {
                    row.setLesson(tdText);
                }
                else if(j == 1){
                    row.setClassName(tdText);
                }
                else if(j == 2)
                {
                    row.setRoom(tdText);
                }
                else if(j == 3)
                {
                    row.setSubject(tdText);
                }
                else if(j == 4)
                {
                    row.setTeacher(tdText);
                }
            }
            rows.add(row);
        }

        return rows;
    }

    private static String GetSubstitutionSite(){
        Header[] header = GetHeaderDataFromGet("http://intranet.gunzelin-realschule.de/embed/");
        try {
            HttpPost httppost = new HttpPost("http://intranet.gunzelin-realschule.de/embed/login.php");
            for(int i =0; i < header.length; i++) {
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
        }catch (ParseException e) {
            e.printStackTrace();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

        return "";
    }

    private static Header[] GetHeaderDataFromGet(String url){
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

    private static Date GetNextBusinessDay(Date sourceDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sourceDate);

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek == Calendar.FRIDAY) {
            calendar.add(Calendar.DATE, 3);
        } else if (dayOfWeek == Calendar.SATURDAY) {
            calendar.add(Calendar.DATE, 2);
        } else {
            calendar.add(Calendar.DATE, 1);
        }

        Date nextBusinessDay = calendar.getTime();
        return nextBusinessDay;
    }

    private static boolean IsBusinessDay(Date sourceDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sourceDate);

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek == Calendar.SUNDAY) {
            return false;
        } else if (dayOfWeek == Calendar.SATURDAY) {
            return false;
        }

        return true;
    }
}
