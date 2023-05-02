package com.example.shimitabenedictmagiegift.schoolms.mains.dash

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
import com.example.shimitabenedictmagiegift.schoolms.mains.main_activities.MainProfile
import com.example.shimitabenedictmagiegift.schoolms.mains.main_activities.StudentRegistration
import com.google.firebase.firestore.FirebaseFirestore

class AcademicsDash : AppCompatActivity() {
    lateinit var schoolCode: String
    lateinit var schoolName: String
    lateinit var whichForm: String
    lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_academics_dash)
        funInitGlobals()
        funFetchData()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun funFetchData() {


        //code begins
        val sweetAlertDialogProgress =
            SweetAlertDialog(this@AcademicsDash, SweetAlertDialog.PROGRESS_TYPE)
        sweetAlertDialogProgress.titleText = "Collecting Students"
        sweetAlertDialogProgress.setCancelable(false)
        sweetAlertDialogProgress.create()
        sweetAlertDialogProgress.show()


        //load all school students then filter by their form
        val storeStudents = FirebaseFirestore.getInstance()
        storeStudents.collection(StudentRegistration.COLLECTION_STUDENTS).get()
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
                            val tempArray = arrayListOf<DataClassMyStudents>()
                            for (doc in it.result.documents) {
                                val classFilter: DataClassMyStudents? =
                                    doc.toObject(DataClassMyStudents::class.java)
                                if (classFilter != null) {
                                    tempArray.add(classFilter)
                                }

                            }

                            val adapterMyStudents =
                                MyAdapterMyStudents(
                                    this@AcademicsDash,
                                    tempArray,
                                    whichForm,
                                    schoolCode,
                                    false
                                )
                            recyclerView.apply {
                                adapter = adapterMyStudents
                                adapterMyStudents.notifyDataSetChanged()
                                layoutManager = LinearLayoutManager(this@AcademicsDash)
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

    private fun funInitGlobals() {
        recyclerView = findViewById(R.id.rvStudentsPerClass)

        schoolCode =
            getSharedPreferences(
                MainProfile.SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE
            ).getString("code", "")
                .toString()
        schoolName =
            getSharedPreferences(
                MainProfile.SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE
            ).getString("name", "")
                .toString()

        whichForm =
            getSharedPreferences(
                MainProfile.SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE
            ).getString("which", "")
                .toString()


        this.title="ACADEMICS ${whichForm.uppercase()}"

    }
}