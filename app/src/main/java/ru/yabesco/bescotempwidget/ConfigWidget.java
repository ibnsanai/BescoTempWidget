package ru.yabesco.bescotempwidget;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by sanai on 24.05.2016.
 */

public class ConfigWidget extends Activity {

    int widgetID = AppWidgetManager.INVALID_APPWIDGET_ID;
    Intent resultValue;
    SharedPreferences sp;
    Context context;
    AppWidgetManager am;

    final static boolean DEBUG = true;

    public final static String BESCOTEMP_PREF = "bescotemp_pref";
    public final static String BESCOTEMP_URL  = "bescotemp_url" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // извлекаем ID конфигурируемого виджета
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        // и проверяем его корректность
        if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        // отрицательный ответ
        setResult(RESULT_CANCELED, resultValue);
        // положительный ответ
        setResult(RESULT_OK, resultValue);

        sp = getSharedPreferences(BESCOTEMP_PREF, MODE_PRIVATE);

        context = getBaseContext();
        am = AppWidgetManager.getInstance(context);

        setContentView(R.layout.conf_layout);
    }

    public void setSharedPreferences(String refresh) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(BESCOTEMP_URL + widgetID, refresh);
        editor.apply();
        TextView url = (TextView) findViewById(R.id.tempUrl);
        url.setText(refresh);
    }

    public void onClickUrl(View v) {
        Dialog dialog = onCreateUrlDialog();
        dialog.show();
    }

    public Dialog onCreateUrlDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String[] array = getResources().getStringArray(R.array.urls);

        builder.setTitle(context.getString(R.string.change_url))
                .setCancelable(false)
                .setSingleChoiceItems(array, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (DEBUG) Toast.makeText(
                                getApplicationContext(),
                                context.getString(R.string.change_url)
                                        + " " + array[which],
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton(context.getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ListView lv = ((AlertDialog) dialog).getListView();
                        setSharedPreferences(array[lv.getCheckedItemPosition()]);
                        setResult(RESULT_OK, resultValue);
                        dialog.cancel();
                    }
                })
                .setNegativeButton(context.getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }


}
