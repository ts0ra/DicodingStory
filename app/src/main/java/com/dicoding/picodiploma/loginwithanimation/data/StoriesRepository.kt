package com.dicoding.picodiploma.loginwithanimation.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.FileUploadResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.StoriesResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.retrofit.ApiService
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class StoriesRepository private constructor(
    private val apiService: ApiService
) {

    fun getAllStories(): LiveData<Result<StoriesResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getAllStories()
            emit(Result.Success(response))
        } catch (e: HttpException) {
            Log.e("getAllStories", "HTTP Exception: ${e.message}")
            try {
                val errorResponse = e.response()?.errorBody()?.string()
                val gson = Gson()
                val parsedError = gson.fromJson(errorResponse, StoriesResponse::class.java)
                emit(Result.Success(parsedError))
            } catch (e: Exception) {
                Log.e("getAllStories", "Error parsing error response: ${e.message}")
                emit(Result.Error("Error: ${e.message}"))
            }
        } catch (e: Exception) {
            Log.e("getAllStories", "General Exception: ${e.message}")
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun getListStory(): List<ListStoryItem?>? {
        return apiService.getAllStories().listStory
    }

    fun uploadStory(
        imageFile: File,
        description: String
    ): LiveData<Result<FileUploadResponse>> = liveData {
        emit(Result.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            val response = apiService.uploadStory(multipartBody, requestBody)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            Log.e("uploadStory", "HTTP Exception: ${e.message}")
            try {
                val errorResponse = e.response()?.errorBody()?.string()
                val gson = Gson()
                val parsedError = gson.fromJson(errorResponse, FileUploadResponse::class.java)
                emit(Result.Success(parsedError))
            } catch (e: Exception) {
                Log.e("uploadStory", "Error parsing error response: ${e.message}")
                emit(Result.Error("Error: ${e.message}"))
            }
        } catch (e: Exception) {
            Log.e("uploadStory", "General Exception: ${e.message}")
            emit(Result.Error(e.message.toString()))
        }
    }


    companion object {
        @Volatile
        private var instance: StoriesRepository? = null
        fun getInstance(
            apiService: ApiService
        ): StoriesRepository =
            instance ?: synchronized(this) {
                instance ?: StoriesRepository(apiService)
            }.also { instance = it }
    }
}