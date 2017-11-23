package com.rahulxyz.foodish;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.rahulxyz.foodish.Activity.MainActivity;
import com.rahulxyz.foodish.Utils.FoodItem;
import com.rahulxyz.foodish.Utils.Menu;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        String allOrders = "", bill = "";
        Integer sum = 0;
        if (Menu.lastOrder == null) {
            allOrders = context.getString(R.string.widgetDefaultMessage);
            bill = context.getString(R.string.indianCurrency) + context.getString(R.string.amountForNoBill);
        } else {
            for (FoodItem food : Menu.lastOrder) {
                allOrders += food.getName() + "\n";
                sum += (food.getPrice() * food.getQuantity());
            }
            bill = context.getString(R.string.indianCurrency) + sum.toString();
        }

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        views.setOnClickPendingIntent(R.id.appwidget_layout, pendingIntent);
        views.setTextViewText(R.id.appwidget_allOrder, allOrders);
        views.setTextViewText(R.id.appwidget_totalAmount, bill);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

