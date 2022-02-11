package com.example.polyenergy.data

import android.content.Context
import com.example.polyenergy.SERVER_URL
import com.example.polyenergy.SessionManager
import com.example.polyenergy.domain.BackResponse
import com.example.polyenergy.domain.ChargeInfo
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
import retrofit2.http.*
import java.io.IOException

interface EnergyService {

    @POST("auth/login")
    fun postLoginAsync(@Body login: LoginParam): Deferred<LoginResponse>

    @POST("auth/register")
    fun postRegisterAsync(@Body login: LoginParam): Deferred<LoginResponse>

    @GET("OpenCharge/poi")
    fun getOpenCharges(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("distance") distance: Double
    ): Deferred<List<ChargeInfo>>

    @GET("OpenCharge/liked")
    fun getFavoritesList(@Header("Cookie") cookie: String): Deferred<List<ChargeInfo>>

    @POST("OpenCharge/toggleLike")
    fun postFavorites(@Body charge: ChargeInfo, @Header("Cookie") cookie: String): Deferred<BackResponse>

}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(SERVER_URL)
    .build()


object OpenChargeApi {
    val retrofitService: EnergyService by lazy { retrofit.create(EnergyService::class.java) }
}

object LoginApi {
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

            retrofit = Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .client(httpClient)
                .baseUrl(SERVER_URL)
                .build()
            retrofitService = retrofit!!.create(EnergyService::class.java)
        }
    }
}
