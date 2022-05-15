package com.corrot.kwiatonomousapp.presentation.widget

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.*
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.layout.*
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.data.remote.api.KwiatonomousApi
import com.corrot.kwiatonomousapp.data.repository.DeviceUpdateRepositoryImpl
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate
import com.corrot.kwiatonomousapp.domain.repository.DeviceUpdateRepository
import com.corrot.kwiatonomousapp.presentation.widget.components.Humidity
import com.corrot.kwiatonomousapp.presentation.widget.components.Temperature
import com.google.gson.Gson
import java.time.LocalDateTime
import javax.inject.Inject


val actionWidgetRefreshKey = ActionParameters.Key<String>("action-widget-refresh-key")
private val deviceUpdatePreferenceKey = stringPreferencesKey("device-update-key")

class DeviceWidget : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition
    override val sizeMode: SizeMode = SizeMode.Single

//    @Inject
//    lateinit var gson: Gson

    private val gson: Gson = Gson()

    @Composable
    override fun Content() {
        val prefs = currentState<Preferences>()
        val deviceUpdateJson = prefs[deviceUpdatePreferenceKey]

        Column(
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
            verticalAlignment = Alignment.Vertical.CenterVertically,
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ImageProvider(R.drawable.widget_shape))
        ) {
            if (deviceUpdateJson.isNullOrEmpty()) {
                NoData()
            } else {
                val deviceUpdate = gson.fromJson(deviceUpdateJson, DeviceUpdate::class.java)

                Image(
                    provider = ImageProvider(R.drawable.flower_1),
                    contentDescription = "",
                    contentScale = ContentScale.Fit,
                    modifier = GlanceModifier.size(50.dp)
                )
                Text(text = deviceUpdate.deviceId)
                Spacer(GlanceModifier.fillMaxWidth().height(4.dp))

//        Spacer(
//            GlanceModifier
//                .background(R.color.primaryDark)
//                .fillMaxWidth()
//                .height(2.dp)
//                .padding(horizontal = 16.dp)
//        )
                Spacer(GlanceModifier.fillMaxWidth().height(4.dp))
                Temperature(temperature = 12.26f)
                Spacer(GlanceModifier.fillMaxWidth().height(4.dp))
                Humidity(humidity = 74.23f)
                Spacer(GlanceModifier.fillMaxWidth().height(4.dp))

                Spacer(GlanceModifier.fillMaxWidth().height(4.dp))

                Image(
                    provider = ImageProvider(resId = R.drawable.ic_baseline_refresh_24),
                    contentDescription = "",
                    modifier = GlanceModifier
                        .clickable(
                            onClick = actionRunCallback<RefreshActionCallback>(
                                parameters = actionParametersOf(
                                    actionWidgetRefreshKey to "AAAAA"
                                )
                            )
                        )
                        .padding(4.dp)
                )
            }
        }
    }

    @Composable
    private fun NoData() {
        Text(text = "No data")
        Spacer(GlanceModifier.fillMaxWidth().height(4.dp))
        Image(
            provider = ImageProvider(resId = R.drawable.ic_baseline_refresh_24),
            contentDescription = "",
            modifier = GlanceModifier
                .clickable(
                    onClick = actionRunCallback<RefreshActionCallback>(
                        parameters = actionParametersOf(
                            actionWidgetRefreshKey to "AAAAA"
                        )
                    )
                )
                .padding(4.dp)
        )
    }
}


class RefreshActionCallback : ActionCallback {

//    @Inject
//    lateinit var deviceUpdateRepository: DeviceUpdateRepository

    private val gson: Gson = Gson()

    override suspend fun onRun(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        Log.i("TAG", "Item with id $glanceId and params $parameters clicked.")

//        deviceUpdateRepository.fetchAllDeviceUpdates()
        val du = DeviceUpdate("AA", LocalDateTime.now(), 55, 3.52f, 26.4f, 67.21f)

        updateAppWidgetState(
            context = context,
            definition = PreferencesGlanceStateDefinition,
            glanceId = glanceId
        ) { preferences ->
            preferences.toMutablePreferences()
                .apply {
                    this[deviceUpdatePreferenceKey] = gson.toJson(du)
                }
        }

        DeviceWidget().update(context, glanceId)
    }
}