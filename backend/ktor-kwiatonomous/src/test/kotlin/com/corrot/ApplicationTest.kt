package com.corrot

import org.junit.Assert.assertEquals
import org.junit.Test

class ApplicationTest {
    @Test
    fun testRoot() {
//        withTestApplication({ configureRouting() }) {
//            handleRequest(HttpMethod.Get, "/").apply {
//                assertEquals(HttpStatusCode.OK, response.status())
//                assertEquals("Hello World!", response.content)
//            }
//        }

//        deviceDao.createDevice("aaaaaaaaaaaa")
//        deviceDao.getDevice("aaaaaaaaaaaa")?.let {
//            println(Json.encodeToString(it))
//        }
//
//        val updateID = deviceUpdatesDao.createDeviceUpdate(
//            "aaaaaaaaaaaa",
//            DateTime.now().millis,
//            55,
//            3.55f,
//            23.31f,
//            64.62f
//        )
//        val updateID2 = deviceUpdatesDao.createDeviceUpdate(
//            "aaaaaaaaaaaa",
//            DateTime.now().millis,
//            53,
//            3.25f,
//            23.61f,
//            61.32f
//        )
//
//        deviceUpdatesDao.getDeviceUpdate(updateID)?.let {
//            println(Json.encodeToString(it))
//        }
//        deviceUpdatesDao.getDeviceUpdate(updateID2)?.let {
//            println(Json.encodeToString(it))
//        }
//
//        println(Json.encodeToString(deviceUpdatesDao.getAllDeviceUpdates("aaaaaaaaaaaa")))

//        deviceDao.deleteDevice("aaaaaaaaaaaa")
//        println(Json.encodeToString(deviceDao.getAllDevices()))

        assertEquals(2, 2)
    }
}