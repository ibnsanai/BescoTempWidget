package ru.yabesco.bescotempwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * Created by sanai on 24.05.2016.
 */

public class BescoTemp extends AppWidgetProvider {

    final static String ACTION_UPDATE = "ru.yabesco.bescotempwidget.update";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        SharedPreferences sp = context.getSharedPreferences(
                ConfigWidget.BESCOTEMP_PREF, Context.MODE_PRIVATE);

        for (int id : appWidgetIds) {
            UpdateWidget updateWidget = new UpdateWidget(context, appWidgetManager, sp, id);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);

        SharedPreferences.Editor editor = context.getSharedPreferences(
                ConfigWidget.BESCOTEMP_PREF, Context.MODE_PRIVATE).edit();
        for (int widgetID : appWidgetIds) {
            editor.remove(ConfigWidget.BESCOTEMP_URL + widgetID);
        }
        editor.apply();
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);

        int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        Bundle extras = intent.getExtras();

        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            // Читаем значения
            SharedPreferences sp = context.getSharedPreferences(
                    ConfigWidget.BESCOTEMP_PREF, Context.MODE_PRIVATE);

            // Проверяем, что это intent от нажатия на третью зону
            if (intent.getAction().equalsIgnoreCase(ACTION_UPDATE)) {
                UpdateWidget updateWidget = new UpdateWidget(context, AppWidgetManager.getInstance(context), sp, mAppWidgetId);
            }
        }
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

}
