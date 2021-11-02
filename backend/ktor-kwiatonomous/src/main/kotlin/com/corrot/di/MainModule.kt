package com.corrot.di

import com.corrot.db.KwiatonomousDatabase
import com.corrot.db.data.dao.DeviceDao
import com.corrot.db.data.dao.DeviceDaoImpl
import com.corrot.db.data.dao.DeviceUpdateDao
import com.corrot.db.data.dao.DeviceUpdateDaoImpl
import org.koin.dsl.module

val MainModule = module {

    single { KwiatonomousDatabase() }

    single { DeviceDaoImpl(get() as KwiatonomousDatabase) as DeviceDao }

    single { DeviceUpdateDaoImpl(get() as KwiatonomousDatabase) as DeviceUpdateDao }
}