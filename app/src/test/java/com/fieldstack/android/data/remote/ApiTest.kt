package com.fieldstack.android.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class ApiTest {

    private lateinit var server: MockWebServer
    private lateinit var api: FieldStackApi

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()
        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .client(OkHttpClient())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(FieldStackApi::class.java)
    }

    @After
    fun teardown() = server.shutdown()

    @Test
    fun `login returns token`() = runTest {
        server.enqueue(MockResponse()
            .setBody("""{"token":"tok-123","user_id":"u1","name":"Alex"}""")
            .setResponseCode(200))
        val resp = api.login(LoginRequest("alex@fieldstack.com", "secret"))
        assertEquals("tok-123", resp.token)
        assertEquals("Alex", resp.name)
    }

    @Test
    fun `getTasks returns list`() = runTest {
        server.enqueue(MockResponse()
            .setBody("""[{"id":"t1","title":"Inspect Site A","description":"","location":"Downtown",
                |"assignee_id":"u1","priority":"high","status":"not_started",
                |"due_at":1700000000000,"created_at":1699000000000,"updated_at":1699000000000}]""".trimMargin())
            .setResponseCode(200))
        val tasks = api.getTasks("u1")
        assertEquals(1, tasks.size)
        assertEquals("t1", tasks[0].id)
    }

    @Test
    fun `submitReport returns serverId`() = runTest {
        server.enqueue(MockResponse()
            .setBody("""{"server_id":"srv-456"}""")
            .setResponseCode(200))
        val resp = api.submitReport(
            ReportDto("r1","t1","Test","inspection","Details",
                emptyList(),null,null,null,
                1700000000000L,1700000000000L)
        )
        assertEquals("srv-456", resp.serverId)
    }

    @Test
    fun `getTasksDelta returns only changed records`() = runTest {
        server.enqueue(MockResponse()
            .setBody("""[{"id":"t2","title":"Updated Task","description":"","location":"Site B",
                |"assignee_id":"u1","priority":"medium","status":"in_progress",
                |"due_at":1700000000000,"created_at":1699000000000,"updated_at":1700500000000}]""".trimMargin())
            .setResponseCode(200))
        val delta = api.getTasksDelta("u1", since = 1700000000000L)
        assertEquals(1, delta.size)
        assertEquals("t2", delta[0].id)
        // Verify the request included the since param
        val request = server.takeRequest()
        assertTrue(request.path?.contains("since=") == true)
    }
}
