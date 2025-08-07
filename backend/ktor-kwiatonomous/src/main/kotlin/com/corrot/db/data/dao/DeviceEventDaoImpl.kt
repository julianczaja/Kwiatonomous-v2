package com.corrot.db.data.dao

import com.corrot.db.DeviceEvents
import com.corrot.db.KwiatonomousDatabase
import com.corrot.db.data.dto.DeviceEventDto
import com.corrot.db.data.model.DeviceEvent
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class DeviceEventDaoImpl(private val database: KwiatonomousDatabase) : DeviceEventDao {

    init {
        transaction(database.db) {
            arrayOf<Table>(DeviceEvents)
            SchemaUtils.create(DeviceEvents)
        }
    }

    override fun getAllDeviceEvents(): List<DeviceEvent> = transaction(database.db) {
        DeviceEvents.selectAll().map {
            DeviceEvent(
                eventId = it[DeviceEvents.eventId],
                deviceId = it[DeviceEvents.deviceId],
                timestamp = it[DeviceEvents.timestamp],
                type = it[DeviceEvents.type],
                data = it[DeviceEvents.data]
            )
        }
    }

    override fun getAllDeviceEvents(
        deviceId: String,
        limit: Int?,
        fromTimestamp: Long?,
        toTimestamp: Long?
    ): List<DeviceEvent> = transaction(database.db) {
        DeviceEvents
            .selectAll().where { DeviceEvents.deviceId eq deviceId }
            .apply {
                if (fromTimestamp != null && toTimestamp != null) {
                    andWhere { DeviceEvents.timestamp greaterEq fromTimestamp }
                    andWhere { DeviceEvents.timestamp lessEq toTimestamp }
                }
            }
            .apply {
                if (limit != null) {
                    limit(limit)
                }
            }
            .orderBy(DeviceEvents.timestamp, SortOrder.DESC)
            .map {
                DeviceEvent(
                    eventId = it[DeviceEvents.eventId],
                    deviceId = it[DeviceEvents.deviceId],
                    timestamp = it[DeviceEvents.timestamp],
                    type = it[DeviceEvents.type],
                    data = it[DeviceEvents.data]
                )
            }
    }

    override fun createDeviceEvent(
        deviceId: String,
        timestamp: Long,
        type: String,
        data: String
    ): Unit = transaction(database.db) {
        DeviceEvents.insert {
            it[DeviceEvents.deviceId] = deviceId
            it[DeviceEvents.timestamp] = timestamp
            it[DeviceEvents.type] = type
            it[DeviceEvents.data] = data
        }
    }

    override fun deleteDeviceEvent(eventId: Int): Unit = transaction(database.db) {
        DeviceEvents.deleteWhere { DeviceEvents.eventId eq eventId }
    }

    override fun deleteDeviceEvent(deviceEventDto: DeviceEventDto): Unit = transaction(database.db) {
        DeviceEvents.deleteWhere {
            timestamp eq deviceEventDto.timestamp and (type eq deviceEventDto.type) and (data eq deviceEventDto.data)
        }
    }

    override fun deleteDeviceEvent(timestamp: Long): Unit = transaction(database.db) {
        DeviceEvents.deleteWhere { DeviceEvents.timestamp eq timestamp }
    }
}
