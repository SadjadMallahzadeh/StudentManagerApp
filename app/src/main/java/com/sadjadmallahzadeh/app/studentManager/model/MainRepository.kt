package com.sadjadmallahzadeh.app.studentManager.model

import io.reactivex.Single
import com.sadjadmallahzadeh.app.studentManager.util.BASE_URL
import com.sadjadmallahzadeh.app.studentManager.util.studentToJsonObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MainRepository {
    private val apiService: ApiService

    init {

        val retrofit = Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

    }

    fun getAllStudents(): Single<List<Student>> {
        return apiService.getAllStudents()
    }

    fun insertStudent(student: Student): Single<ApiRequestResult> {
        return apiService.insertStudent(studentToJsonObject(student))
    }

    fun updateStudent(student: Student): Single<ApiRequestResult> {
        return apiService.updateStudent(student.name, studentToJsonObject(student))
    }

    fun removeStudent(studentName: String): Single<ApiRequestResult> {
        return apiService.deleteStudent(studentName)
    }

}