package com.corrot.kwiatonomousapp.domain.model

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import androidx.glance.state.GlanceStateDefinition
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.time.LocalDateTime

sealed interface DevicesWidgetData {
    data object Loading : DevicesWidgetData
    data class Error(val message: String) : DevicesWidgetData
    data class Success(val updateTime: LocalDateTime, val devicesUpdates: Map<UserDevice, DeviceUpdate>) : DevicesWidgetData

    object StateDefinition : GlanceStateDefinition<DevicesWidgetData> {

        private const val DATA_STORE_FILENAME = "devicesWidgetData"

        private val Context.datastore by dataStore(DATA_STORE_FILENAME, DevicesWidgetDataSerializer)

        override suspend fun getDataStore(context: Context, fileKey: String): DataStore<DevicesWidgetData> {
            return context.datastore
        }

        override fun getLocation(context: Context, fileKey: String): File {
            return context.dataStoreFile(DATA_STORE_FILENAME)
        }

        object DevicesWidgetDataSerializer : Serializer<DevicesWidgetData> {
            override val defaultValue = Error("No place found")

            override suspend fun readFrom(input: InputStream): DevicesWidgetData = try {
                Gson().fromJson(input.readBytes().decodeToString(), DevicesWidgetData::class.java)
            } catch (exception: JsonSyntaxException) {
                throw CorruptionException("Could not read DevicesWidgetData: ${exception.message}")
            }

            override suspend fun writeTo(t: DevicesWidgetData, output: OutputStream) {
                output.use {
                    it.write(Gson().toJson(t).encodeToByteArray())
                }
            }
        }
    }
}
