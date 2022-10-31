package com.corrot.kwiatonomousapp.presentation.widget

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.layout.*
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.corrot.kwiatonomousapp.KwiatonomousWorkManager
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.common.toFormattedString
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate
import com.corrot.kwiatonomousapp.domain.model.DevicesWidgetData
import com.corrot.kwiatonomousapp.domain.model.UserDevice
import com.corrot.kwiatonomousapp.presentation.theme.DayPrimary
import java.time.LocalDateTime

class DevicesWidget : GlanceAppWidget() {

    override val stateDefinition = DevicesWidgetData.StateDefinition
    override val sizeMode: SizeMode = SizeMode.Single

    @Composable
    override fun Content() {
        when (val devicesWidgetData = currentState<DevicesWidgetData>()) {
            DevicesWidgetData.Loading -> Loading()
            is DevicesWidgetData.Success -> DevicesUpdatesContent(devicesWidgetData.updateTime, devicesWidgetData.devicesUpdates)
            is DevicesWidgetData.Error -> Error(devicesWidgetData.message)
        }
    }

    @Composable
    private fun DevicesUpdatesContent(
        updateTime: LocalDateTime,
        devicesUpdates: Map<UserDevice, DeviceUpdate>,
    ) {
        val context = LocalContext.current

        LazyColumn(
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
            modifier = GlanceModifier
                .padding(8.dp)
                .background(Color.White)
                .fillMaxSize()
        ) {
            devicesUpdates.forEach {
                item { Device(userDevice = it.key, deviceUpdate = it.value) }
                item { DevicesSpacer() }
            }
            item {
                Box(contentAlignment = Alignment.Center, modifier = GlanceModifier.fillMaxWidth()) {
                    RefreshButton()
                }
            }
            item {
                Box(contentAlignment = Alignment.Center, modifier = GlanceModifier.fillMaxWidth()) {
                    Text(
                        text = context.getString((R.string.last_update)) + ": ${updateTime.toFormattedString()}",
                        style = TextStyle(fontStyle = FontStyle.Italic, fontSize = 11.sp)
                    )
                }
            }
        }
    }

    @Composable
    private fun Loading() {
        Box(
            contentAlignment = Alignment.Center,
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color.White)
                .padding(8.dp)
        ) {
            CircularProgressIndicator(color = ColorProvider(DayPrimary))
        }
    }

    @Composable
    private fun Error(message: String) {
        Column(
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
            verticalAlignment = Alignment.Vertical.CenterVertically,
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color.White)
                .padding(8.dp)
        ) {
            val context = LocalContext.current

            Text(text = context.getString(R.string.error) + ": $message")
            Spacer(GlanceModifier.fillMaxWidth().height(4.dp))
            RefreshButton()
        }
    }

    @Composable
    private fun RefreshButton() {
        Image(
            provider = ImageProvider(resId = R.drawable.ic_baseline_refresh_24),
            contentDescription = null,
            modifier = GlanceModifier
                .clickable(onClick = actionRunCallback<RefreshActionCallback>())
                .padding(4.dp)
        )
    }

    @Composable
    private fun DevicesSpacer() {
        Column {
            Spacer(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(DayPrimary)
            )
            Spacer(GlanceModifier.height(4.dp))
        }
    }

    @Composable
    private fun Device(userDevice: UserDevice, deviceUpdate: DeviceUpdate) {

        fun getBatteryDrawable(batteryLevel: Int): Int {
            return when {
                batteryLevel >= 90 -> R.drawable.full_battery
                batteryLevel in 60..89 -> R.drawable.charged_battery
                batteryLevel in 30..60 -> R.drawable.half_battery
                else -> R.drawable.low_battery
            }
        }

        val context = LocalContext.current

        Row(
            modifier = GlanceModifier.fillMaxWidth().height(100.dp).padding(bottom = 4.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = GlanceModifier.fillMaxHeight().padding(end = 8.dp)
            ) {
                val imageId = context.resources.getIdentifier(userDevice.deviceImageName, "drawable", context.packageName)
                Image(
                    provider = ImageProvider(resId = imageId),
                    contentDescription = null,
                    modifier = GlanceModifier.fillMaxHeight().width(80.dp)
                )
            }
            Column(
                verticalAlignment = Alignment.Vertical.CenterVertically,
                modifier = GlanceModifier.fillMaxHeight()
            ) {
                Text(
                    text = userDevice.deviceName,
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    modifier = GlanceModifier.padding(bottom = 4.dp)
                )
                TextWithImageRow(
                    text = context.getString((R.string.battery_level_format)).format(deviceUpdate.batteryLevel),
                    imageRes = getBatteryDrawable(deviceUpdate.batteryLevel)
                )
                TextWithImageRow(
                    text = context.getString((R.string.temperature_format)).format(deviceUpdate.temperature),
                    imageRes = R.drawable.temperature
                )
                TextWithImageRow(
                    text = context.getString((R.string.humidity_format)).format(deviceUpdate.humidity),
                    imageRes = R.drawable.humidity
                )
            }
        }
    }

    @Composable
    fun TextWithImageRow(text: String, @DrawableRes imageRes: Int) {
        Row(verticalAlignment = Alignment.Vertical.CenterVertically, modifier = GlanceModifier.fillMaxWidth()) {
            Image(
                provider = ImageProvider(resId = imageRes),
                contentDescription = null,
                modifier = GlanceModifier.size(19.dp).padding(end = 4.dp)
            )
            Text(text)
        }
    }
}

class RefreshActionCallback : ActionCallback {

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        KwiatonomousWorkManager(context).enqueueDevicesWidgetUpdate(true)
    }
}