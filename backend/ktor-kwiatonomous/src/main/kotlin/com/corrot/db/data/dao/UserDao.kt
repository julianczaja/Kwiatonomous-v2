package com.corrot.db.data.dao

import com.corrot.db.data.model.User
import com.corrot.db.data.model.UserDevice

interface UserDao {

    fun getAllUsers(): List<User>

    fun getUser(userId: String): User?

    fun createUser(userId: String, userName: String, ha1: ByteArray)

    fun updateUserDevices(userId: String, devices: List<UserDevice>)

    fun deleteUser(userId: String)
}