package org.d3if0098.assesment3mobpro.model

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.d3if0098.assesment3mobpro.network.BarangAPI
import org.d3if0098.assesment3mobpro.network.BarangStatus
import java.io.ByteArrayOutputStream

class BarangViewModel : ViewModel() {
    private val _barangData = MutableLiveData<List<Barang>>()
    val barangData: LiveData<List<Barang>> get() = _barangData

    var barangStatus = MutableStateFlow(BarangStatus.LOADING)
        private set

//    init {
//        getBarang()
//    }

    fun addBarang(email: String, namaBarang: String, hargaBarang: String, image: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            barangStatus.value = BarangStatus.LOADING
            try {
                val emailPart = email.toRequestBody("text/plain".toMediaTypeOrNull())
                val namaBarangPart = namaBarang.toRequestBody("text/plain".toMediaTypeOrNull())
                val hargaBarangPart = hargaBarang.toRequestBody("text/plain".toMediaTypeOrNull())

                val byteArrayOutputStream = ByteArrayOutputStream()
                image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArrayOutputStream.toByteArray())
                val imagePart = MultipartBody.Part.createFormData("foto", "image.jpg", requestBody)

                val response = BarangAPI.retrofitService.addBarang(emailPart, namaBarangPart, hargaBarangPart, imagePart)

                _barangData.postValue(response.results)
                barangStatus.value = BarangStatus.SUCCESS
                getBarang(email)
                Log.d("BarangViewModel", "[Barang] Success: ${response.results}")
            } catch (e: Exception) {
                Log.e("BarangViewModel", "[Barang] Error: ${e.message}")
                barangStatus.value = BarangStatus.FAILED
            }
        }
    }

    fun getBarang(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            barangStatus.value = BarangStatus.LOADING
            Log.d("statusSekarang", "Masih loading")
            try {
                val response = BarangAPI.retrofitService.getBarang(email)
                _barangData.postValue(response.results)
                barangStatus.value = BarangStatus.SUCCESS
                Log.d("statusSekarang", "Udah success")
            } catch (e: Exception) {
                Log.e("BarangViewModel", "[Barang] Error: ${e.message}")
                barangStatus.value = BarangStatus.FAILED
            }
        }
    }

    fun deleteBarang(email: String, id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            barangStatus.value = BarangStatus.LOADING
            try {
                val response = BarangAPI.retrofitService.deleteBarang(id)
                _barangData.postValue(response.results)
                barangStatus.value = BarangStatus.SUCCESS
                getBarang(email)
                Log.d("BarangViewModel", "[Barang] Deleted: $id")
            } catch (e: Exception) {
                Log.e("BarangViewModel", "[Barang] Error: ${e.message}")
                barangStatus.value = BarangStatus.FAILED
            }
        }
    }
}