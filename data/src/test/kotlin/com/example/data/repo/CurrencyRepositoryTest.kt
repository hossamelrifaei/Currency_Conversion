package com.example.data.repo

import android.content.SharedPreferences
import com.example.data.model.CurrenciesDto
import com.example.data.model.LocalCurrenciesDTO
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import database.CurrenciesDao
import database.CurrenciesDataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File

@ExperimentalCoroutinesApi
class CurrencyRepositoryTest {
    private val TIME_TO_UPDATE = 3600000L
    private val TIME_STAMP_KEY = "TimestampKey"

    private val dispatcher = UnconfinedTestDispatcher()
    private val mockWebServer: MockWebServer = MockWebServer()
    private lateinit var openExchangeRatesService: OpenExchangeRatesService
    private val sharedPreferences: SharedPreferences = mock()
    private val currenciesDataBase: CurrenciesDataBase = mock()
    private val currenciesDao: CurrenciesDao = mock()
    private val moshi = MoshiConverterFactory.create(
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    )
    lateinit var repository: CurrencyRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        openExchangeRatesService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(moshi)
            .build()
            .create(OpenExchangeRatesService::class.java)

        repository =
            CurrencyRepository(openExchangeRatesService, sharedPreferences, currenciesDataBase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when fetch new currencies rates success`() = runTest {
        prepareServer("exchange_api_response.json", 200)

        val response = expectedResponse()

        val result = repository.getFreshCurrencies()

        assertEquals(result, response)

        verifyNoMoreInteractions(sharedPreferences, currenciesDataBase, currenciesDao)
    }


    @Test
    fun `when fetch new currencies rates error`() = runTest {
        prepareServer(null, 404)

        runCatching {
            repository.getFreshCurrencies()
        }.onFailure {
            Assert.assertTrue(it is HttpException)
        }

        verifyNoMoreInteractions(sharedPreferences, currenciesDataBase, currenciesDao)
    }

    @Test
    fun `return local stored rates`() = runTest {
        val expectedList = expectedCachedRates()

        whenever(currenciesDataBase.currenciesDao()).thenReturn(currenciesDao)
        whenever(currenciesDao.getLocalCurrencies()).thenReturn(expectedList)

        val localList = repository.getLocalCurrencies()


        assertEquals(expectedList, localList)

        verify(currenciesDataBase).currenciesDao()
        verify(currenciesDao).getLocalCurrencies()

        verifyNoMoreInteractions(sharedPreferences, currenciesDataBase, currenciesDao)
    }

    @Test
    fun `when store new list of rates to database`() = runTest {
        val ratesList = expectedCachedRates()

        whenever(currenciesDataBase.currenciesDao()).thenReturn(currenciesDao)

        repository.storeLocalCurrencies(ratesList)

        verify(currenciesDataBase).currenciesDao()
        verify(currenciesDao).insertRates(ratesList)

        verifyNoMoreInteractions(sharedPreferences, currenciesDataBase, currenciesDao)
    }


    @Test
    fun `should return true when last fetch was before 30 minutes`() {
        val timestamp = System.currentTimeMillis() - TIME_TO_UPDATE - 100
        whenever(sharedPreferences.getLong(TIME_STAMP_KEY, 0)).thenReturn(timestamp)

        assertTrue(repository.shouldFetchRemote())
        verify(sharedPreferences).getLong(any(), any())

        verifyNoMoreInteractions(sharedPreferences, currenciesDataBase, currenciesDao)
    }


    @Test
    fun `should return false when last fetch was less than 30 minutes`() {
        val timestamp = System.currentTimeMillis() - 100
        whenever(sharedPreferences.getLong(TIME_STAMP_KEY, 0)).thenReturn(timestamp)

        assertFalse(repository.shouldFetchRemote())
        verify(sharedPreferences).getLong(any(), any())

        verifyNoMoreInteractions(sharedPreferences, currenciesDataBase, currenciesDao)
    }

    @Test
    fun `should save time stamp of last fetch`() {
        val editor: SharedPreferences.Editor = mock()
        whenever(sharedPreferences.edit()).thenReturn(editor)
        whenever(editor.putLong(any(), any())).thenReturn(editor)

        repository.saveTimeStamp()

        verify(sharedPreferences).edit()
        verify(editor).putLong(any(), any())
        verify(editor).apply()
        verifyNoMoreInteractions(sharedPreferences, currenciesDataBase, currenciesDao)
    }

    private fun expectedCachedRates(): List<LocalCurrenciesDTO> {
        return listOf(
            LocalCurrenciesDTO("AED", 3.672972),
            LocalCurrenciesDTO("USD", 1.0)
        )
    }


    private fun expectedResponse(): CurrenciesDto {
        return CurrenciesDto(
            mapOf(
                "AED" to 3.672972,
                "USD" to 1.0
            )
        )
    }


    private fun prepareServer(fileName: String?, code: Int) {
        mockWebServer.enqueue(
            MockResponse()
                .apply {
                    setResponseCode(code)
                    fileName?.let { setBody(readResourceAsString(it)) }

                }
        )
    }

    private fun readResourceAsString(fileName: String): String {
        val url = javaClass.classLoader?.getResource(fileName)
        val file = File(url?.path.orEmpty())
        return file.readText()
    }


}