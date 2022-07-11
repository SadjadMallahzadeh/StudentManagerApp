package com.sadjadmallahzadeh.app.studentManager.addEditStudent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import io.reactivex.SingleObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import com.sadjadmallahzadeh.app.studentManager.model.Student
import com.sadjadmallahzadeh.app.studentManager.databinding.ActivityMain2Binding
import com.sadjadmallahzadeh.app.studentManager.model.ApiRequestResult
import com.sadjadmallahzadeh.app.studentManager.model.MainRepository
import com.sadjadmallahzadeh.app.studentManager.util.asyncRequest
import com.sadjadmallahzadeh.app.studentManager.util.showToast
import java.lang.Exception
import java.lang.NumberFormatException

class AddEditStdView : AppCompatActivity() {
    lateinit var binding: ActivityMain2Binding
    lateinit var addEditStdViewModel: AddEditStdViewModel
    private val compositeDisposable = CompositeDisposable()
    lateinit var dataFromIntent: Student
    var isInserting = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarMain2)
        addEditStdViewModel = AddEditStdViewModel(MainRepository())


        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.edtFirstName.requestFocus()


        val testMode = intent.getParcelableExtra<Student>("student")
        isInserting = (testMode == null)
        if (!isInserting) {

            dataFromIntent = intent.getParcelableExtra<Student>("student")!!

            binding.toolbarMain2.setTitle(dataFromIntent.name)

            binding.edtFirstNameLayout.visibility = View.GONE
            binding.edtLastNameLayout.visibility = View.GONE


            logicUpdateStudent()
        }

        binding.btnDone.setOnClickListener {

            if (isInserting) {
                addNewStudent()
            } else {
                updateStudent()
            }

        }


    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    private fun logicUpdateStudent() {
        binding.btnDone.text = "update"
        val a = intent.getParcelableExtra<Student>("student")!!

        binding.edtScore.setText(dataFromIntent.score.toString())
        binding.edtCourse.setText(dataFromIntent.course)

        val splitedName = dataFromIntent.name.split(" ")
        binding.edtFirstName.setText(splitedName[0])
        binding.edtLastName.setText(splitedName[(splitedName.size - 1)])
    }

    private fun updateStudent() {


        val score = binding.edtScore.text.toString()
        val course = binding.edtCourse.text.toString()

        if (
            course.isNotEmpty() &&
            score.isNotEmpty()
        ) {


            try {
                addEditStdViewModel
                    .updateStudent(
                        Student(dataFromIntent.name, course, score.toInt())
                    )
                    .asyncRequest()
                    .subscribe(object : SingleObserver<ApiRequestResult> {
                        override fun onSubscribe(d: Disposable) {
                            compositeDisposable.add(d)
                        }

                        override fun onSuccess(t: ApiRequestResult) {
                            showToast(t.serverRequestResult)
                            onBackPressed()
                        }

                        override fun onError(e: Throwable) {
                            showToast(e.message ?: "Update Failed!")
                        }

                    })
            } catch (e: NumberFormatException) {
                showToast("Fill the Course field with an integer value!")
            }


        } else {
            showToast("Please enter all the required data!")
        }
    }

    private fun addNewStudent() {

        val firstName = binding.edtFirstName.text.toString()
        val lastName = binding.edtLastName.text.toString()
        val score = binding.edtScore.text.toString()
        val course = binding.edtCourse.text.toString()

        if (
            firstName.isNotEmpty() &&
            lastName.isNotEmpty() &&
            course.isNotEmpty() &&
            score.isNotEmpty()
        ) {
            try{
                addEditStdViewModel
                    .insertNewStudent(
                        Student(firstName + " " + lastName, course, score.toInt())
                    )
                    .asyncRequest()
                    .subscribe(object : SingleObserver<ApiRequestResult> {
                        override fun onSubscribe(d: Disposable) {
                            compositeDisposable.add(d)
                        }

                        override fun onSuccess(t: ApiRequestResult) {
                            showToast(t.serverRequestResult)
                            onBackPressed()
                        }

                        override fun onError(e: Throwable) {
                            showToast(e.message ?: "Insert Failed")
                        }

                    })
            }
            catch(e: NumberFormatException){
                showToast("Fill the Course field with an integer value!")
            }



        } else {
            showToast("Please enter all the required data!")
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }

        return true
    }

}