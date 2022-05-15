package com.corrot.kwiatonomousapp.presentation

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate
import com.corrot.kwiatonomousapp.presentation.widget.DeviceWidget
import java.time.LocalDateTime

class DeviceWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget =
//        DeviceWidget(DeviceUpdate("AA", LocalDateTime.now(), 55, 3.52f, 26.4f, 67.21f))
        DeviceWidget()

    override fun onReceive(context: Context, intent: Intent) {
        Log.i("DeviceWidgetReceiver", "onReceive")
        super.onReceive(context, intent)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Log.i("DeviceWidgetReceiver", "onUpdate")
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }
}