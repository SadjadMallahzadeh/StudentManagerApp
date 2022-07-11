package com.sadjadmallahzadeh.app.studentManager.mainScreen

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import io.reactivex.SingleObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import com.sadjadmallahzadeh.app.studentManager.addEditStudent.AddEditStdView
import com.sadjadmallahzadeh.app.studentManager.databinding.ActivityMainBinding
import com.sadjadmallahzadeh.app.studentManager.model.ApiRequestResult
import com.sadjadmallahzadeh.app.studentManager.model.MainRepository
import com.sadjadmallahzadeh.app.studentManager.model.Student
import com.sadjadmallahzadeh.app.studentManager.util.asyncRequest
import com.sadjadmallahzadeh.app.studentManager.util.showToast

class MainScreenView : AppCompatActivity(), StudentAdapter.StudentEvent {
    lateinit var binding: ActivityMainBinding
    lateinit var myAdapter: StudentAdapter
    private val compositeDisposable = CompositeDisposable()
    lateinit var mainScreenViewModel: MainScreenViewModel

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarMain)
        mainScreenViewModel = MainScreenViewModel(MainRepository())

        binding.btnAddStudent.setOnClickListener {
            val intent = Intent(this, AddEditStdView::class.java)
            startActivity(intent)
        }

        compositeDisposable.add(
            mainScreenViewModel.progressBarSubject.subscribe {
                if (it) {
                    runOnUiThread {
                        binding.progressMain.visibility = View.VISIBLE
                        binding.recyclerMain.visibility = View.INVISIBLE
                    }
                } else {
                    runOnUiThread {
                        binding.progressMain.visibility = View.INVISIBLE
                        binding.recyclerMain.visibility = View.VISIBLE
                    }
                }
            }
        )

    }

    override fun onResume() {
        super.onResume()

        mainScreenViewModel
            .getAllStudents()
            .asyncRequest()
            .subscribe(object : SingleObserver<List<Student>> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(t: List<Student>) {
                    setDataToRecycler(t)
                }

                override fun onError(e: Throwable) {
                    showToast("error -> " + e.message ?: "null")
                }

            })

    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    override fun onItemClicked(student: Student, position: Int) {
        val intent = Intent(this, AddEditStdView::class.java)
        intent.putExtra("student", student)
        startActivity(intent)
    }

    override fun onItemLongClicked(student: Student, position: Int) {
        val dialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
        dialog.contentText = "Delete this Item?"
        dialog.cancelText = "cancel"
        dialog.confirmText = "confirm"

        dialog.setOnCancelListener {
            dialog.dismiss()
        }

        dialog.setConfirmClickListener {
            deleteDataFromServer(student, position)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun deleteDataFromServer(student: Student, position: Int) {

        mainScreenViewModel
            .removeStudent(student.name)
            .asyncRequest()
            .subscribe(object : SingleObserver<ApiRequestResult> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(t: ApiRequestResult) {
                    showToast(t.serverRequestResult)
                }

                override fun onError(e: Throwable) {
                    showToast(e.message ?: "Remove Failed")
                }

            })


        myAdapter.removeItem(student, position)

    }

    private fun setDataToRecycler(data: List<Student>) {
        val myData = ArrayList(data)
        myAdapter = StudentAdapter(myData, this)
        binding.recyclerMain.adapter = myAdapter
        binding.recyclerMain.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }
}