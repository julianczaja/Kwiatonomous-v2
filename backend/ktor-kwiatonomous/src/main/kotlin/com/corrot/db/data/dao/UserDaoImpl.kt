package com.corrot.db.data.dao

import com.corrot.db.KwiatonomousDatabase
import com.corrot.db.Users
import com.corrot.db.data.model.User
import com.corrot.db.data.model.UserDevice
import com.corrot.utils.TimeUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class UserDaoImpl(private val database: KwiatonomousDatabase) : UserDao {

    init {
        transaction(database.db) {
            arrayOf<Table>(Users)
            SchemaUtils.create(Users)
        }
    }

    override fun getAllUsers(): List<User> = transaction(database.db) {
        Users.selectAll().map {
            User(
                userId = it[Users.userId],
                userName = it[Users.userName],
                ha1 = it[Users.ha1],
                devices = Gson().fromJson(it[Users.devices], object : TypeToken<List<UserDevice>>() {}.type),
                registrationTimestamp = it[Users.registrationTimestamp],
                lastActivityTimestamp = it[Users.lastActivityTimestamp],
            )
        }
    }

    override fun getUser(userId: String): User? = transaction(database.db) {
        Users.selectAll().where { Users.userId eq userId }.map {
            User(
                userId = it[Users.userId],
                userName = it[Users.userName],
                ha1 = it[Users.ha1],
                devices = Gson().fromJson(it[Users.devices], object : TypeToken<List<UserDevice>>() {}.type),
                registrationTimestamp = it[Users.registrationTimestamp],
                lastActivityTimestamp = it[Users.lastActivityTimestamp],
            )
        }.singleOrNull()
    }

    override fun createUser(userId: String, userName: String, ha1: ByteArray): Unit = transaction(database.db) {
        println("Creating user : $userId  |  ${ha1.contentToString()}")
        Users.insert {
            it[Users.userId] = userId
            it[Users.userName] = userName
            it[Users.ha1] = ha1
            it[devices] = "[]"
            it[registrationTimestamp] = TimeUtils.getCurrentTimestamp()
            it[lastActivityTimestamp] = TimeUtils.getCurrentTimestamp()
        }
    }

    override fun updateUserDevices(userId: String, devices: List<UserDevice>): Unit = transaction(database.db) {
        Users.update(
            where = { Users.userId eq userId },
            body = {
                it[Users.devices] = Gson().toJson(devices, object : TypeToken<List<UserDevice>>() {}.type)
            }
        )
    }

    override fun deleteUser(userId: String): Unit = transaction(database.db) {
        Users.deleteWhere { Users.userId eq userId }
    }
}
