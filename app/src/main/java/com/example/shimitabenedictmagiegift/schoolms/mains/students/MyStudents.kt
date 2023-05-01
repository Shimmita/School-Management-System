package com.example.shimitabenedictmagiegift.schoolms.mains.students

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.shimitabenedictmagiegift.schoolms.R
import com.example.shimitabenedictmagiegift.schoolms.mains.adapters.MyAdapterMyStudents
import com.example.shimitabenedictmagiegift.schoolms.mains.data_class_main.DataClassMyStudents
import com.example.shimitabenedictmagiegift.schoolms.mains.main_activities.MainProfile.Companion.SHARED_PREFERENCE_NAME
import com.example.shimitabenedictmagiegift.schoolms.mains.main_activities.StudentRegistration.Companion.COLLECTION_STUDENTS
import com.google.firebase.firestore.FirebaseFirestore

class MyStudents : AppCompatActivity() {
    companion object {
        private const val TAG = "MyStudents"
    }

    private lateinit var schoolCode: String
    lateinit var schoolName: String
    lateinit var teacherForm: String


    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_students)
        funInitGlobals()
        funInitOther()

    }

    private fun funInitOther() {
        funLoadMyStudents()
    }

    private fun funInitGlobals() {
        this.title = getString(R.string.my_students_details)
        schoolCode =
            getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).getString("code", "")
                .toString()
        schoolName =
            getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).getString("name", "")
                .toString()
        teacherForm =
            getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).getString("form", "")
                .toString()
        recyclerView = findViewById(R.id.rvMyStudentsRetrieval)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun funLoadMyStudents() {

        //code begins
        val sweetAlertDialogProgress =
            SweetAlertDialog(this@MyStudents, SweetAlertDialog.PROGRESS_TYPE)
        sweetAlertDialogProgress.titleText = "Collecting Students"
        sweetAlertDialogProgress.setCancelable(false)
        sweetAlertDialogProgress.create()
        sweetAlertDialogProgress.show()


        //load all school students then filter by their form

        val storeStudents = FirebaseFirestore.getInstance()
        storeStudents.collection(COLLECTION_STUDENTS).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result.isEmpty) {
                        //no students
                        //students for thee school absent/not registered to the system
                        sweetAlertDialogProgress.apply {
                            changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                            titleText = "No Students"
                            contentText = "students not registered"
                            confirmText = "okay"
                            setConfirmClickListener {
                                dismissWithAnimation()
                            }
                        }

                    } else {
                        sweetAlertDialogProgress.apply {
                            dismissWithAnimation()
                            //students presents
                            var tempArray = arrayListOf<DataClassMyStudents>()
                            for (doc in it.result.documents) {
                                val classFilter: DataClassMyStudents? =
                                    doc.toObject(DataClassMyStudents::class.java)
                                if (classFilter != null) {
                                    tempArray.add(classFilter)
                                }

                            }

                            val adapterMyStudents=
                                MyAdapterMyStudents(this@MyStudents,tempArray,teacherForm,schoolCode)
                            recyclerView.apply {
                                adapter=adapterMyStudents
                                adapterMyStudents.notifyDataSetChanged()
                                layoutManager= LinearLayoutManager(this@MyStudents)
                            }
                        }

                    }

                } else if (!it.isSuccessful) {

                    sweetAlertDialogProgress.apply {
                        changeAlertType(SweetAlertDialog.WARNING_TYPE)
                        titleText = "Error"
                        contentText = it.exception?.message
                        confirmText = "okay"
                        setConfirmClickListener {
                            dismiss()
                        }
                    }
                }
            }
    }

}