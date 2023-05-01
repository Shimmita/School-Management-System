package com.example.shimitabenedictmagiegift.schoolms.mains.dash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.example.shimitabenedictmagiegift.schoolms.R
import com.example.shimitabenedictmagiegift.schoolms.mains.dash.BursaDash.Companion.COLLECTION_NEWS
import com.example.shimitabenedictmagiegift.schoolms.mains.main_activities.MainProfile
import com.example.shimitabenedictmagiegift.schoolms.mains.main_activities.MainProfile.Companion.SHARED_PREFERENCE_NAME
import com.example.shimitabenedictmagiegift.schoolms.mains.main_activities.StaffRegistration.Companion.COLLECTION_TEACHER
import com.example.shimitabenedictmagiegift.schoolms.mains.main_activities.StudentRegistration.Companion.COLLECTION_STUDENTS
import com.example.shimitabenedictmagiegift.schoolms.mains.main_activities.StudentRegistration.Companion.COLLECTION_STUDENT_RESULTS
import com.example.shimitabenedictmagiegift.schoolms.mains.students.MyStudents
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import de.hdodenhof.circleimageview.CircleImageView

class TeacherDash : AppCompatActivity() {
    companion object {
        private const val TAG = "TeacherDash"
    }

    lateinit var textViewTeacherName: TextView
    lateinit var textViewClassTeacherFor: TextView
    lateinit var circleImageViewTeacher: CircleImageView
    lateinit var appCompatButtonUpdateMarks: AppCompatButton
    lateinit var appCompatButtonMyStudents: AppCompatButton
    lateinit var appCompatButtonPostInformation: AppCompatButton
    lateinit var appCompatButtonLogout: AppCompatButton

    lateinit var classTeacherName: String
    lateinit var classTeacherForm: String

    lateinit var schoolCode: String
    lateinit var schoolName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_dash)
        funInitGlobals()
        funInitOther()
    }

    private fun funInitOther() {
        appCompatButtonLogout.setOnClickListener {
            it.apply {
                startAnimation(AnimationUtils.loadAnimation(this@TeacherDash, R.anim.push_down_out))
                postDelayed({ funLogoutWarn() }, 500)
            }
        }
        appCompatButtonPostInformation.setOnClickListener {
            it.apply {
                startAnimation(AnimationUtils.loadAnimation(this@TeacherDash, R.anim.push_down_out))
                postDelayed({ funPostSchoolNews() }, 500)
            }
        }

        appCompatButtonUpdateMarks.setOnClickListener {
            it.apply {
                startAnimation(AnimationUtils.loadAnimation(this@TeacherDash, R.anim.push_down_out))
                postDelayed({ updateMarksStudent() }, 500)
            }
        }

        appCompatButtonMyStudents.setOnClickListener {

            it.apply {
                startAnimation(
                    AnimationUtils.loadAnimation(
                        this@TeacherDash,
                        R.anim.push_down_out
                    )
                )
                postDelayed({ funLoadMyStudents() }, 500)

            }
        }

    }

    private fun funLoadMyStudents() {
        startActivity(Intent(this@TeacherDash, MyStudents::class.java))
    }

    private fun updateMarksStudent() {
        //code begins

        val view: View = layoutInflater.inflate(R.layout.layout_student_adm_no, null, false)
        val editText: EditText = view.findViewById(R.id.edtStudentAdmissionNumber)

        val materialAlertAdmissionNumber = MaterialAlertDialogBuilder(this@TeacherDash)
        materialAlertAdmissionNumber.setCancelable(false)
        materialAlertAdmissionNumber.setView(view)
        materialAlertAdmissionNumber.setTitle("admission number")
        materialAlertAdmissionNumber.setIcon(R.drawable.school_msi_1)
        materialAlertAdmissionNumber.background =
            ResourcesCompat.getDrawable(resources, R.drawable.background_main_profile, theme)
        materialAlertAdmissionNumber.setPositiveButton("check results") { dg, _ ->

            val text = editText.text.toString().trim()

            if (text.isEmpty()) {
                Toast.makeText(this@TeacherDash, "cannot submit empty data!", Toast.LENGTH_SHORT)
                    .show()

            } else if (text.isNotEmpty()) {
                funSearchStudentUpdateMarks(text)
            }

            dg.dismiss()
        }
        materialAlertAdmissionNumber.setNegativeButton("dismiss") { dg, _ ->
            dg.dismiss()
        }
        materialAlertAdmissionNumber.create()
        materialAlertAdmissionNumber.show()
        //code ends
    }

    private fun funSearchStudentUpdateMarks(text: String) {

        //code begins
        val sweetAlertDialogMarksProgress =
            SweetAlertDialog(this@TeacherDash, SweetAlertDialog.PROGRESS_TYPE)
        sweetAlertDialogMarksProgress.titleText = "searching"
        sweetAlertDialogMarksProgress.setCancelable(false)
        sweetAlertDialogMarksProgress.create()
        sweetAlertDialogMarksProgress.show()


        val searchString = "$schoolCode$text"
        Log.d(TAG, "funSearchStudentUpdateFees: string search->$searchString")
        //search in the collection student /document=schoolCode/studentAdmissionNumber.
        val storeStudentFees = FirebaseFirestore.getInstance()
        storeStudentFees.collection(COLLECTION_STUDENTS).get().addOnCompleteListener {
            if (it.isSuccessful) {

                //obtain the document ids
                val dataDocuments = it.result.documents

                var isStudentPresent = false
                var gottenStudentDocumentID = ""
                for (doc in dataDocuments) {
                    val docId = doc.id
                    Log.d(TAG, "funSearchStudentUpdateFees: docID=>$docId\n")
                    if (docId.contains(searchString)) {
                        isStudentPresent = true
                        gottenStudentDocumentID = docId
                    }
                }

                //check the results
                if (isStudentPresent) {
                    //student found thus proceed with update of the fees
                    Log.d(TAG, "funSearchStudentUpdateFees: CONGRATS FOUND")
                    val storeStudentFees = FirebaseFirestore.getInstance()
                    storeStudentFees.collection(COLLECTION_STUDENTS)
                        .document(gottenStudentDocumentID)
                        .get().addOnCompleteListener {
                            if (it.isSuccessful) {
                                val studentName = it.result["name"].toString()
                                val studentForm = it.result["form"].toString()
                                val studentID = it.result["id"].toString()

                                if (studentForm.lowercase() != classTeacherForm.lowercase()) {
                                    //teacher cannot update marks of student not belonging to his class deny
                                    sweetAlertDialogMarksProgress.apply {
                                        changeAlertType(SweetAlertDialog.WARNING_TYPE)
                                        titleText = "NOT PERMITTED"
                                        contentText = "not your student"
                                        confirmText = "okay"
                                        setConfirmClickListener {
                                            dismiss()
                                        }
                                    }
                                } else {
                                    //continue update marks process
                                    sweetAlertDialogMarksProgress.apply {
                                        changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                                        titleText = studentName
                                        contentText = studentForm
                                        confirmText = "update Marks"
                                        cancelText = "cancel"
                                        setConfirmClickListener {
                                            funAlertEnterMarks(
                                                gottenStudentDocumentID,
                                                studentName,
                                                studentForm,
                                                studentID,
                                                sweetAlertDialogMarksProgress
                                            )
                                        }
                                        setCancelClickListener {
                                            it.dismiss()
                                        }
                                    }
                                }


                            } else if (!it.isSuccessful) {
                                sweetAlertDialogMarksProgress.apply {
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
                    //
                } else {
                    //student not found alert
                    sweetAlertDialogMarksProgress.apply {
                        changeAlertType(SweetAlertDialog.ERROR_TYPE)
                        titleText = "Zero Results"
                        contentText = "student not found"
                        confirmText = "try again"
                        setConfirmClickListener {
                            dismiss()
                        }
                    }
                }

            } else if (!it.isSuccessful) {
                sweetAlertDialogMarksProgress.apply {
                    changeAlertType(SweetAlertDialog.ERROR_TYPE)
                    titleText = "Failed"
                    contentText = it.exception?.message
                    confirmText = "okay"
                    setConfirmClickListener {
                        it.dismiss()
                    }
                }
            }

        }
        //code ends
    }

    private fun funAlertEnterMarks(
        gottenStudentDocumentID: String,
        studentName: String,
        studentForm: String,
        studentID: String,
        sweetAlertDialogMarksProgress: SweetAlertDialog
    ) {
        val viewEnterFees: View = layoutInflater.inflate(R.layout.layout_grade_points, null, false)
        val editTextPoints: EditText = viewEnterFees.findViewById(R.id.edtStudentPoints)

        val stringMessage =
            "Form:$studentForm\n\n"

        val materialAlertEnterFees = MaterialAlertDialogBuilder(this@TeacherDash)
        materialAlertEnterFees.setCancelable(false)
        materialAlertEnterFees.setTitle(studentName)
        materialAlertEnterFees.setMessage(stringMessage)
        materialAlertEnterFees.setView(viewEnterFees)
        materialAlertEnterFees.setIcon(R.drawable.school_msi_1)
        materialAlertEnterFees.background =
            ResourcesCompat.getDrawable(resources, R.drawable.background_main_profile, theme)
        materialAlertEnterFees.setPositiveButton("update Marks") { dg, _ ->

            val textPoints = editTextPoints.text.toString().trim()
            if (textPoints.isEmpty()) {
                Toast.makeText(this@TeacherDash, "cannot submit empty field!", Toast.LENGTH_SHORT)
                    .show()
            } else if (textPoints.toInt() > 100) {
                Toast.makeText(this@TeacherDash, "points exceeded 100!", Toast.LENGTH_SHORT)
                    .show()
            } else if (textPoints.toInt() < 0) {
                Toast.makeText(this@TeacherDash, "points less than zero !", Toast.LENGTH_SHORT)
                    .show()
            } else {
                funProceedFinalUpdate(
                    textPoints,
                    gottenStudentDocumentID,
                    sweetAlertDialogMarksProgress
                )
            }
            dg.dismiss()
        }
        materialAlertEnterFees.setNegativeButton("dismiss") { dg, _ ->
            dg.dismiss()
        }
        materialAlertEnterFees.create()
        materialAlertEnterFees.show()
    }

    private fun funProceedFinalUpdate(
        textPoints: String,
        gottenStudentDocumentID: String,
        sweetAlertDialogMarksProgress: SweetAlertDialog
    ) {
        //show progress
        sweetAlertDialogMarksProgress.show()

        val grade = calculateGrade(textPoints.toInt())
        val keyGrade = "grade"
        val keyPoints = "points"

        val mapData = hashMapOf(keyGrade to grade, keyPoints to textPoints)
        val storeStudentResults = FirebaseFirestore.getInstance()
        storeStudentResults.collection(COLLECTION_STUDENT_RESULTS).document(gottenStudentDocumentID)
            .update(
                mapData as Map<String, Any>
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    //update the grade points in the profile of student too
                    storeStudentResults.collection(COLLECTION_STUDENTS)
                        .document(gottenStudentDocumentID)
                        .set(mapData as Map<String, Any>, SetOptions.merge())
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                sweetAlertDialogMarksProgress.apply {
                                    changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                                    titleText = "Updated Successfully"
                                    confirmText = "okay"
                                    setConfirmClickListener {
                                        it.dismiss()
                                    }
                                }
                            } else if (!it.isSuccessful) {
                                sweetAlertDialogMarksProgress.apply {
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
                } else if (!it.isSuccessful) {
                    sweetAlertDialogMarksProgress.apply {
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

    private fun calculateGrade(pointsNumber: Int): String {
        if (pointsNumber > 80) {
            return "A"
        } else if (pointsNumber in 74..79) {
            return "A-"
        } else if (pointsNumber in 66..73) {
            return "B+"
        } else if (pointsNumber in 60..65) {
            return "B"
        } else if (pointsNumber in 53..59) {
            return "B-"
        } else if (pointsNumber in 46..52) {
            return "C+"
        } else if (pointsNumber in 39..45) {
            return "C"
        } else if (pointsNumber in 32..38) {
            return "C-"
        } else if (pointsNumber in 25..31) {
            return "D+"
        } else if (pointsNumber in 18..24) {
            return "D"
        } else if (pointsNumber in 11..17) {
            return "D-"
        } else if (pointsNumber in 0..10) {
            return "E"
        } else return ""
    }

    private fun funPostSchoolNews() {

        //code begins

        val viewPostNews: View = layoutInflater.inflate(R.layout.layout_news_post, null, false)
        val editTextTitle: EditText = viewPostNews.findViewById(R.id.edtTitlePost)
        val editTextMessage: EditText = viewPostNews.findViewById(R.id.edtMessagePost)

        val materialAlertAdmissionNumber = MaterialAlertDialogBuilder(this@TeacherDash)
        materialAlertAdmissionNumber.setCancelable(false)
        materialAlertAdmissionNumber.setView(viewPostNews)
        materialAlertAdmissionNumber.setTitle("Post Information")
        materialAlertAdmissionNumber.setIcon(R.drawable.school_msi_1)
        materialAlertAdmissionNumber.background =
            ResourcesCompat.getDrawable(resources, R.drawable.background_main_profile, theme)
        materialAlertAdmissionNumber.setPositiveButton("Post") { dg, _ ->

            val title = editTextTitle.text.toString().trim()
            val message = editTextMessage.text.toString().trim()
            if (title.isEmpty() || message.isEmpty()) {
                Toast.makeText(this@TeacherDash, "cannot submit empty data!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                funPostNow(title, message)
            }

            dg.dismiss()
        }
        materialAlertAdmissionNumber.setNegativeButton("dismiss") { dg, _ ->
            dg.dismiss()
        }
        materialAlertAdmissionNumber.create()
        materialAlertAdmissionNumber.show()
    }

    private fun funPostNow(title: String, message: String) {

        //code begins
        val sweetAlertDialogPostNews =
            SweetAlertDialog(this@TeacherDash, SweetAlertDialog.PROGRESS_TYPE)
        sweetAlertDialogPostNews.titleText = "posting"
        sweetAlertDialogPostNews.setCancelable(false)
        sweetAlertDialogPostNews.create()
        sweetAlertDialogPostNews.show()

        //path(schoolCode/UID)
        val postNews = FirebaseFirestore.getInstance()
        val uniqueUID = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val path = "$schoolCode$uniqueUID"
        val keyTitle = "title"
        val keyMessage = "message"
        val keySender = "sender"
        val keySchoolCode = "code"

        val hashMap =
            hashMapOf(
                keyTitle to title,
                keyMessage to message,
                keySender to "Teacher $classTeacherName ($classTeacherForm)",
                keySchoolCode to schoolCode
            )
        postNews.collection(COLLECTION_NEWS).document(path).set(hashMap)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    sweetAlertDialogPostNews.apply {
                        changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                        titleText = "POSTED SUCCESSFULLY"
                        contentText = it.exception?.message
                        confirmText = "okay"
                        setConfirmClickListener {
                            dismiss()
                        }
                    }
                } else if (!it.isSuccessful) {
                    sweetAlertDialogPostNews.apply {
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
        //code ends
    }


    private fun funLogoutWarn() {

        //code begins

        val materialAlertLogout = MaterialAlertDialogBuilder(this@TeacherDash)
        materialAlertLogout.setCancelable(false)
        materialAlertLogout.setTitle("Logout")
        materialAlertLogout.setMessage("your current session will be ended.\nyou will have to login again to access this session")
        materialAlertLogout.setIcon(R.drawable.school_msi_1)
        materialAlertLogout.background =
            ResourcesCompat.getDrawable(resources, R.drawable.background_main_profile, theme)
        materialAlertLogout.setPositiveButton("yes") { dg, _ ->
            funProcessLogout()
            dg.dismiss()

        }
        materialAlertLogout.setNegativeButton("no") { dg, _ ->
            dg.dismiss()
        }
        materialAlertLogout.create()
        materialAlertLogout.show()

    }

    private fun funProcessLogout() {

        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null) {
            firebaseAuth.signOut()

            funReturnDashMainProfile()
        } else {

            funReturnDashMainProfile()
        }
    }

    private fun funReturnDashMainProfile() {
        startActivity(Intent(this@TeacherDash, MainProfile::class.java))
        finish()
    }

    private fun funInitGlobals() {
        //code begins

        this.title = getString(R.string.teacher_dash)

        appCompatButtonLogout = findViewById(R.id.btnLogoutTeacher)
        appCompatButtonMyStudents = findViewById(R.id.btnViewMyStudents)
        appCompatButtonPostInformation = findViewById(R.id.btnPostInformationTeacher)
        appCompatButtonUpdateMarks = findViewById(R.id.btnUpdateStudentMarks)
        circleImageViewTeacher = findViewById(R.id.imgTeacher)
        textViewTeacherName = findViewById(R.id.tvTeacherName)
        textViewClassTeacherFor = findViewById(R.id.tvTeacherClass)

        schoolCode =
            getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).getString("code", "")
                .toString()
        schoolName =
            getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).getString("name", "")
                .toString()


        //fetch data of the teacher from the store
        funFetchTeachDetailsStore()

        //code ends
    }

    @SuppressLint("SetTextI18n")
    private fun funFetchTeachDetailsStore() {
        //init of pgDialog
        val sweetAlertDialogProgress =
            SweetAlertDialog(this@TeacherDash, SweetAlertDialog.PROGRESS_TYPE)
        sweetAlertDialogProgress.setCancelable(false)
        sweetAlertDialogProgress.titleText = "Fetching Data"
        sweetAlertDialogProgress.create()
        sweetAlertDialogProgress.show()

        //path(schoolCode/UID)
        val uniqueUID = FirebaseAuth.getInstance().currentUser?.uid.toString()

        val path = "$schoolCode$uniqueUID"
        val storeFetch = FirebaseFirestore.getInstance()
        storeFetch.collection(COLLECTION_TEACHER).document(path).get().addOnCompleteListener {
            if (it.isSuccessful) {
                sweetAlertDialogProgress.apply {
                    dismissWithAnimation()
                    val name = it.result["name"].toString()
                    val classTeacherFor = it.result["form"].toString()
                    val image = it.result["image"].toString()

                    //updating globals used for news post
                    classTeacherForm = classTeacherFor
                    classTeacherName = name
                    //saving teacher for in shared preference for use when viewing my students
                    val sharedPreferences =
                        getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
                    sharedPreferences.edit().putString("form", classTeacherFor).apply()
                    //

                    //setting the data
                    Glide.with(this@TeacherDash).load(image).into(circleImageViewTeacher)
                    textViewTeacherName.text = name
                    textViewClassTeacherFor.text = "$classTeacherFor class teacher"
                    //


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