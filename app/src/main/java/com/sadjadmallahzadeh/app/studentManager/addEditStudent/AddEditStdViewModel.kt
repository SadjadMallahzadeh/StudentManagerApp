package com.sadjadmallahzadeh.app.studentManager.addEditStudent

import io.reactivex.Single
import com.sadjadmallahzadeh.app.studentManager.model.ApiRequestResult
import com.sadjadmallahzadeh.app.studentManager.model.MainRepository
import com.sadjadmallahzadeh.app.studentManager.model.Student

class AddEditStdViewModel
    (private val mainRepository: MainRepository) {

    fun insertNewStudent(student: Student): Single<ApiRequestResult> {
        return mainRepository.insertStudent(student)
    }

    fun updateStudent(student: Student): Single<ApiRequestResult> {
        return mainRepository.updateStudent(student)
    }

}