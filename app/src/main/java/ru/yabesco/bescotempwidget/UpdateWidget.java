package ru.yabesco.bescotempwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by sanai on 24.05.2016.
 */

public class UpdateWidget {
    Context context;
    AppWidgetManager appWidgetManager;
    SharedPreferences sp;
    int widgetID;

    PendingIntent pIntent1;
    PendingIntent pIntent2;

    UpdateWidget(Context context, AppWidgetManager appWidgetManager,
                 SharedPreferences sp, int widgetID) {

        this.context = context;
        this.appWidgetManager = appWidgetManager;
        this.sp = sp;
        this.widgetID = widgetID;

        RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        // Сайт yabesco.ru
        Uri uri = Uri.parse(context.getString(R.string.site_URL));
        Intent siteIntent = new Intent(Intent.ACTION_VIEW, uri);
        pIntent1 = PendingIntent.getActivity(context, widgetID, siteIntent, 0);
        widgetView.setOnClickPendingIntent(R.id.img, pIntent1);

        // Конфигурационный экран
        Intent configIntent = new Intent(context, ConfigWidget.class);
        configIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
        configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        pIntent2 = PendingIntent.getActivity(context, widgetID, configIntent, 0);
        widgetView.setOnClickPendingIntent(R.id.tempText, pIntent2);

        appWidgetManager.updateAppWidget(widgetID, widgetView);

        String urlDate = sp.getString(ConfigWidget.BESCOTEMP_URL + widgetID, null);
        if (urlDate == null) {
            urlDate = context.getString(R.string.first_URL);
        }

        Log.d("UPD LOG: ", "from site: " + urlDate);

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            ParseURL parseURL = new ParseURL();
            parseURL.setContext(context);
            parseURL.setAm(appWidgetManager);
            parseURL.setWidgetID(widgetID);
            parseURL.execute(urlDate);
        } else {
            Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }

    }

    static class ParseURL extends AsyncTask<String, Void, String[]> {
        Context context;
        AppWidgetManager am;
        int widgetID;
        int currentTemp;

        void setContext(Context context) {this.context = context;}
        void setAm(AppWidgetManager am) {this.am = am;}
        void setWidgetID(int widgetID) {this.widgetID = widgetID;}

        @Override
        protected String[] doInBackground(String... strings) {
            String[] a = new String[]{"","",""};
            BufferedReader reader = null;
            StringBuilder buffer = new StringBuilder();
            try {
                URL site = new URL(strings[0]);
                reader = new BufferedReader(new InputStreamReader(site.openStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    assert reader != null;
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            String[] fields = buffer.toString().split(";");
            currentTemp = Integer.valueOf(fields[0]);
            if (currentTemp > 0) {
                a[0] = "+" + currentTemp + context.getString(R.string.celsius) ;
            } else {
                if (currentTemp < 0){
                    a[0] = "-" + currentTemp + context.getString(R.string.celsius);
                } else {
                    a[0] = currentTemp + context.getString(R.string.celsius);
                }
            }
            return a;
        }

        @Override
        protected void onPostExecute(String s[]) {
            super.onPostExecute(s);
            RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            widgetView.setTextViewText(R.id.tempText,s[0]);
            am.updateAppWidget(widgetID, widgetView);
        }
    }

}
