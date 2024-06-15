package org.d3if0098.assesment3mobpro.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.d3if0098.assesment3mobpro.model.Barang
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query


private const val BASE_URL = "https://ryanbanjarnahor.000webhostapp.com/files/"

// Initialize Moshi
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

// Initialize Retrofit
private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()

data class BarangResponse(
    val results: List<Barang>
)

interface BarangApiService {
    @Multipart
    @POST("add_data.php")
    suspend fun addBarang(
        @Part("email") email: RequestBody,
        @Part("nama_barang") namaBarang: RequestBody,
        @Part("harga_barang") hargaBarang: RequestBody,
        @Part foto: MultipartBody.Part
    ): BarangResponse

    @FormUrlEncoded
    @POST("delete_data.php")
    suspend fun deleteBarang(
        @Field("id") id: String
    ): BarangResponse

    @GET("get_data.php")
    suspend fun getBarang(
        @Header("Authorization") email: String
    ): BarangResponse
}

object BarangAPI {
    val retrofitService: BarangApiService by lazy {
        retrofit.create(BarangApiService::class.java)
    }
    fun imgUrl(imageId: String): String {
        return "$BASE_URL$imageId"
    }
}

enum class BarangStatus { LOADING, SUCCESS,FAILED}