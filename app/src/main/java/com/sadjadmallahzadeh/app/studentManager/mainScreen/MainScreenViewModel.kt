package com.sadjadmallahzadeh.app.studentManager.mainScreen

import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import com.sadjadmallahzadeh.app.studentManager.model.ApiRequestResult
import com.sadjadmallahzadeh.app.studentManager.model.MainRepository
import com.sadjadmallahzadeh.app.studentManager.model.Student
import java.util.concurrent.TimeUnit

class MainScreenViewModel(
    private val mainRepository: MainRepository
) {
    val progressBarSubject = BehaviorSubject.create<Boolean>()

    fun getAllStudents(): Single<List<Student>> {
        progressBarSubject.onNext(true)

        return mainRepository
            .getAllStudents()
            .delay(2, TimeUnit.SECONDS)
            .doFinally {
                progressBarSubject.onNext(false)
            }

    }

    fun removeStudent(studentName: String): Single<ApiRequestResult> {
        return mainRepository.removeStudent(studentName)
    }


}