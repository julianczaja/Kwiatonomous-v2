package com.corrot.kwiatonomousapp.presentation.widget

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.wrapContentHeight
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.common.toFormattedString
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate
import com.corrot.kwiatonomousapp.domain.model.UserDevice
import com.corrot.kwiatonomousapp.domain.usecase.GetDevicesWidgetDataUseCase
import com.corrot.kwiatonomousapp.domain.workmanager.KwiatonomousWorkManager
import com.corrot.kwiatonomousapp.presentation.theme.DayOnSurface
import com.corrot.kwiatonomousapp.presentation.theme.DayPrimary
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors.fromApplication
import dagger.hilt.components.SingletonComponent


class DevicesWidget : GlanceAppWidget() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface GlanceEntryPoint {
        fun getDevicesWidgetDataUseCase(): GetDevicesWidgetDataUseCase
    }

    override val sizeMode: SizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val entryPoint = fromApplication(context, GlanceEntryPoint::class.java)
            val useCase = entryPoint.getDevicesWidgetDataUseCase()
            val state by useCase.execute().collectAsState(emptyMap())

            DevicesUpdatesContent(state)
        }
    }

    @Composable
    private fun DevicesUpdatesContent(state: Map<UserDevice, DeviceUpdate>) {
        LazyColumn(
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
            modifier = GlanceModifier
                .background(Color.White)
                .fillMaxSize()
        ) {
            state.forEach {
                item { Device(userDevice = it.key, deviceUpdate = it.value) }
                item { DevicesSpacer() }
            }
            item {
                Box(
                    modifier = GlanceModifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    RefreshButton()
                }
            }
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
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            val imageId = context.resources.getIdentifier(
                userDevice.deviceImageName,
                "drawable",
                context.packageName
            )
            Image(
                provider = ImageProvider(resId = imageId),
                contentDescription = null,
                modifier = GlanceModifier
                    .size(100.dp)
                    .padding(8.dp)
            )
            Column(
                verticalAlignment = Alignment.Vertical.CenterVertically,
                modifier = GlanceModifier.wrapContentHeight()
            ) {
                Text(
                    text = userDevice.deviceName,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        color = ColorProvider(DayOnSurface)
                    ),
                    modifier = GlanceModifier.padding(bottom = 4.dp)
                )
                TextWithImageRow(
                    text = context.getString((R.string.battery_level_format))
                        .format(deviceUpdate.batteryLevel),
                    imageRes = getBatteryDrawable(deviceUpdate.batteryLevel)
                )
                TextWithImageRow(
                    text = context.getString((R.string.temperature_format))
                        .format(deviceUpdate.temperature),
                    imageRes = R.drawable.temperature
                )
                TextWithImageRow(
                    text = context.getString((R.string.humidity_format))
                        .format(deviceUpdate.humidity),
                    imageRes = R.drawable.humidity
                )
                TextWithImageRow(
                    text = deviceUpdate.updateTime.toFormattedString(),
                    imageRes = R.drawable.ic_baseline_refresh_24
                )
            }
        }
    }

    @Composable
    private fun TextWithImageRow(text: String, @DrawableRes imageRes: Int) {
        Row(
            verticalAlignment = Alignment.Vertical.CenterVertically,
            modifier = GlanceModifier.fillMaxWidth()
        ) {
            Image(
                provider = ImageProvider(resId = imageRes),
                contentDescription = null,
                modifier = GlanceModifier
                    .size(19.dp)
                    .padding(end = 4.dp)
            )
            Text(
                text = text,
                style = TextStyle(color = ColorProvider(DayOnSurface)),
            )
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
