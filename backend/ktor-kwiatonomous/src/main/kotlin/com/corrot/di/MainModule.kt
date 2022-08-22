package com.corrot.di

import com.corrot.db.KwiatonomousDatabase
import com.corrot.db.data.dao.*
import org.koin.dsl.module

val MainModule = module {

    single { KwiatonomousDatabase() }

    single { UserDaoImpl(get() as KwiatonomousDatabase) as UserDao }

    single { DeviceDaoImpl(get() as KwiatonomousDatabase) as DeviceDao }

    single { DeviceUpdateDaoImpl(get() as KwiatonomousDatabase) as DeviceUpdateDao }

    single { DeviceConfigurationDaoImpl(get() as KwiatonomousDatabase) as DeviceConfigurationDao }

    single { DeviceEventDaoImpl(get() as KwiatonomousDatabase) as DeviceEventDao }
}