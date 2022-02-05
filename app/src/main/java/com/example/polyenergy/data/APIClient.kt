package com.example.polyenergy.data

import android.content.Context
import com.example.polyenergy.SessionManager
import com.example.polyenergy.domain.LoginParam
import com.example.polyenergy.domain.LoginResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import java.io.IOException

interface EnergyService {

    @POST()
    fun postLoginAsync(@Body login: LoginParam): Deferred<LoginResponse>

    @POST()
    fun postRegisterAsync(@Body login: LoginParam): Deferred<LoginResponse>

    @GET()
    fun getObjectsAsync(@Header("Cookie") cookie: String): Deferred<List<String>>

    @POST()
    fun getObjectsLogsAsync(@Header("Cookie", ) cookie: String, @Body id: String): Deferred<List<String>>
}

object EnergyAPI {

    lateinit var retrofitService: EnergyService

    private var retrofit: Retrofit? = null

    fun setRetrofit(context: Context) {
        if (retrofit == null) {
            val httpClient = OkHttpClient.Builder()
                .addInterceptor(object : Interceptor {
                    @Throws(IOException::class)
                    override fun intercept(chain: Interceptor.Chain): Response {
                        val resp: Response = chain.proceed(chain.request())
                        val sm = SessionManager(context)
                        if (resp.headers("Set-Cookie").isNotEmpty()) {
                            val headerCookie = resp.headers("Set-Cookie")[0]
                            val list = headerCookie.split(";")
                            sm.saveAuthCookie(list[0])
                        }
                        return resp
                    }
                })
                .build()

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()


            retrofit = Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .client(httpClient)
                .baseUrl("BASE URL")
                .build()
            retrofitService = retrofit!!.create(EnergyService::class.java)
        }
    }
}
