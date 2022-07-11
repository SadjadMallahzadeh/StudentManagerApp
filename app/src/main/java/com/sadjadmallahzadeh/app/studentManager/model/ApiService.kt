package com.sadjadmallahzadeh.app.studentManager.model

import com.google.gson.JsonObject
import io.reactivex.Single
import retrofit2.http.*

interface ApiService {

    @GET("/student")
    fun getAllStudents(): Single<List<Student>>

    @POST("/student")
    fun insertStudent(@Body body: JsonObject): Single<ApiRequestResult>

    @PUT("/student/updating{name}")
    fun updateStudent(@Path("name") name: String, @Body body: JsonObject): Single<ApiRequestResult>

    @DELETE("/student/deleting{name}")
    fun deleteStudent(@Path("name") name: String): Single<ApiRequestResult>

}