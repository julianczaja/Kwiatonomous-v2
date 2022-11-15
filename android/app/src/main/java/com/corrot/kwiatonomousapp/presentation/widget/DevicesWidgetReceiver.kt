package com.corrot.kwiatonomousapp.presentation.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.corrot.kwiatonomousapp.domain.workmanager.KwiatonomousWorkManager

class DevicesWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget = DevicesWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        KwiatonomousWorkManager(context).enqueueDevicesWidgetUpdate()
    }

    override fun onDisabled(context: Context) {
        KwiatonomousWorkManager(context).cancelDevicesWidgetUpdates()
        super.onDisabled(context)
    }
}