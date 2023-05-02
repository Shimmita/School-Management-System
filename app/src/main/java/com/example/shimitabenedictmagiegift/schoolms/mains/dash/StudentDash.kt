package com.example.shimitabenedictmagiegift.schoolms.mains.dash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.example.shimitabenedictmagiegift.schoolms.R
import com.example.shimitabenedictmagiegift.schoolms.mains.main_activities.MainProfile
import com.example.shimitabenedictmagiegift.schoolms.mains.main_activities.MainProfile.Companion.SHARED_PREFERENCE_NAME
import com.example.shimitabenedictmagiegift.schoolms.mains.main_activities.StudentRegistration.Companion.COLLECTION_FEES
import com.example.shimitabenedictmagiegift.schoolms.mains.main_activities.StudentRegistration.Companion.COLLECTION_STUDENTS
import com.example.shimitabenedictmagiegift.schoolms.mains.main_activities.StudentRegistration.Companion.COLLECTION_STUDENT_RESULTS
import com.example.shimitabenedictmagiegift.schoolms.mains.students.ClassResults
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView

class StudentDash : AppCompatActivity() {
    companion object {
        private const val TAG = "StudentDash"
    }

    //declaration of the globals
    private lateinit var textViewStudentName: TextView
    private lateinit var textViewStudentAdmission: TextView
    private lateinit var textViewStudentGrade: TextView
    private lateinit var textViewStudentPoints: TextView
    private lateinit var textViewStudentForm: TextView
    private lateinit var appcompatButtonViewResults: AppCompatButton
    private lateinit var appCompatButtonViewFees: AppCompatButton
    private lateinit var appCompatButtonViewClassTeacher: AppCompatButton
    private lateinit var appCompatButtonLogout: AppCompatButton
    private lateinit var circleImageViewStudent: CircleImageView

    lateinit var schoolCode: String
    lateinit var schoolName: String
    lateinit var studentForm: String
    lateinit var studentDocumentIDCombination: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_dash)
        //init Globals
        funInitGlobals()
        //fetch Student details
        funFetchDetailsStudentStore()
        //get shared preference data
        funGetSharedPrefData()
        //fun init other
        funInitOtherGlobals()
    }

    private fun funGetSharedPrefData() {
        //code begins
        schoolCode =
            getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).getString("code", "")
                .toString()
        schoolName =
            getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).getString("name", "")
                .toString()

        studentForm =
            getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).getString("form", "")
                .toString()

        studentDocumentIDCombination =
            getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).getString("id", "")
                .toString()

        if (studentDocumentIDCombination.isEmpty()) {
            //reload data fetch since admission number missing
            funFetchDetailsStudentStore()
        }

        //code ends
    }

    private fun funFetchDetailsStudentStore() {
        //code
        val sweetAlertDialogDetails =
            SweetAlertDialog(this@StudentDash, SweetAlertDialog.PROGRESS_TYPE)
        sweetAlertDialogDetails.setCancelable(false)
        sweetAlertDialogDetails.titleText = "Fetching"
        sweetAlertDialogDetails.create()
        sweetAlertDialogDetails.show()

        val collectionStudent = FirebaseFirestore.getInstance()
        val uniqueUID = FirebaseAuth.getInstance().currentUser?.uid.toString()
        collectionStudent.collection(COLLECTION_STUDENTS).get().addOnCompleteListener {
            if (it.isSuccessful) {
                sweetAlertDialogDetails.titleText = "Retrieving"
                var isPresent = false
                var gottenStudentDocument = ""
                for (doc in it.result.documents) {
                    val docID = doc.id
                    if (docID.contains(uniqueUID)) {
                        isPresent = true
                        gottenStudentDocument = docID
                    }

                    //fetching the details of the student using the document
                    if (isPresent) {
                        //present student
                        funFetchDetailsNow(gottenStudentDocument, sweetAlertDialogDetails)
                    } else {
                        //not present student
                        sweetAlertDialogDetails.apply {
                            changeAlertType(SweetAlertDialog.ERROR_TYPE)
                            titleText = "ERROR"
                            confirmText = "Okay"
                            contentText = "something went wrong"
                            setConfirmClickListener {
                                funReturnHome()
                                dismissWithAnimation()
                            }
                        }
                    }
                }

            } else if (!it.isSuccessful) {
                sweetAlertDialogDetails.apply {
                    changeAlertType(SweetAlertDialog.ERROR_TYPE)
                    titleText = "ERROR"
                    confirmText = "Okay"
                    setConfirmClickListener {
                        funReturnHome()
                        dismissWithAnimation()
                    }
                }
            }
        }
        //code
    }

    private fun funFetchDetailsNow(
        gottenStudentDocument: String,
        sweetAlertDialogDetails: SweetAlertDialog
    ) {
        //code begins
        val store = FirebaseFirestore.getInstance()
        store.collection(COLLECTION_STUDENTS).document(gottenStudentDocument).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val studentAdmissionNumber = it.result["adm"].toString()
                    val form = it.result["form"].toString()
                    val sharedPreferences =
                        getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
                    sharedPreferences.edit().putString("id", gottenStudentDocument)
                        .putString("form", form).apply()

                    val studentName = it.result["name"].toString()
                    val studentForm = it.result["form"].toString()
                    val image = it.result["image"].toString()


                    funCompleteFetchingNow(
                        studentAdmissionNumber,
                        studentName,
                        gottenStudentDocument,
                        sweetAlertDialogDetails,
                        studentForm,
                        image
                    )

                } else if (!it.isSuccessful) {
                    sweetAlertDialogDetails.apply {
                        changeAlertType(SweetAlertDialog.ERROR_TYPE)
                        titleText = "ERROR"
                        confirmText = "Okay"
                        contentText = it.exception?.message
                        setConfirmClickListener {
                            funReturnHome()
                            dismissWithAnimation()
                        }
                    }
                }
            }
        //code ends
    }

    @SuppressLint("SetTextI18n")
    private fun funCompleteFetchingNow(
        studentAdmissionNumber: String,
        studentName: String,
        gottenStudentDocument: String,
        sweetAlertDialogDetails: SweetAlertDialog,
        studentForm: String,
        image: String
    ) {
        //code begins
        val storeStudentResults = FirebaseFirestore.getInstance()
        storeStudentResults.collection(COLLECTION_STUDENT_RESULTS).document(gottenStudentDocument)
            .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val grade = it.result["grade"].toString()
                    val points = it.result["points"].toString()

                    //setting the values to the data fields
                    sweetAlertDialogDetails.apply {
                        dismiss()

                        Glide.with(this@StudentDash).load(image).into(circleImageViewStudent)
                        textViewStudentAdmission.text = "Adm: $studentAdmissionNumber"
                        textViewStudentForm.text = studentForm
                        textViewStudentGrade.text = grade
                        textViewStudentName.text = studentName
                        textViewStudentPoints.text = points

                    }
                    //

                } else if (!it.isSuccessful) {
                    sweetAlertDialogDetails.apply {
                        changeAlertType(SweetAlertDialog.ERROR_TYPE)
                        titleText = "ERROR"
                        confirmText = "Okay"
                        contentText = it.exception?.message
                        setConfirmClickListener {
                            funReturnHome()
                            dismissWithAnimation()
                        }
                    }
                }
            }
        //code ends
    }

    private fun funReturnHome() {
        startActivity(Intent(this@StudentDash, MainProfile::class.java))
        finish()
    }

    @SuppressLint("InflateParams")
    private fun funInitOtherGlobals() {
        //events listeners
        appcompatButtonViewResults.setOnClickListener {
            val sharedPreferences =
                getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
            sharedPreferences.edit().putString("form", studentForm).apply()
            startActivity(Intent(this@StudentDash, ClassResults::class.java))
        }

        appCompatButtonViewFees.setOnClickListener {
            //fun fetchFees
            funViewFees()
        }

        appCompatButtonViewClassTeacher.setOnClickListener {


        }

        appCompatButtonLogout.setOnClickListener {

            funLogout()
        }


    }

    private fun funViewFees() {
        //code begins

        val sweetAlertDialogDetails =
            SweetAlertDialog(this@StudentDash, SweetAlertDialog.PROGRESS_TYPE)
        sweetAlertDialogDetails.setCancelable(false)
        sweetAlertDialogDetails.titleText = "Fetching"
        sweetAlertDialogDetails.create()
        sweetAlertDialogDetails.show()

        //path(schoolCode/adm/uiD=combinedDocID)
        val collectionFees = FirebaseFirestore.getInstance()
        collectionFees.collection(COLLECTION_FEES).document(studentDocumentIDCombination).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    sweetAlertDialogDetails.dismissWithAnimation()

                    val paid = it.result["paid"].toString()
                    val balance = it.result["balance"].toString()
                    val requiredPerYear = it.result["required"].toString()
                    val name = it.result["name"].toString()

                    alertUSerWithDataFees(paid, balance, requiredPerYear, name)

                } else if (!it.isSuccessful) {
                    sweetAlertDialogDetails.apply {
                        changeAlertType(SweetAlertDialog.ERROR_TYPE)
                        titleText = "ERROR"
                        confirmText = "Okay"
                        contentText = it.exception?.message
                        setConfirmClickListener {
                            funReturnHome()
                            dismissWithAnimation()
                        }
                    }
                }
            }
        //code ends
    }

    private fun alertUSerWithDataFees(
        paid: String,
        balance: String,
        requiredPerYear: String,
        name: String
    ) {
        //code begins
        val stringMessage =
            "You Paid: $paid\n\nYour Balance: $balance\n\n Fees Per Year: $requiredPerYear\n"

        val materialAlertLogout = MaterialAlertDialogBuilder(this@StudentDash)
        materialAlertLogout.setCancelable(false)
        materialAlertLogout.setTitle(name)
        materialAlertLogout.setMessage(stringMessage)
        materialAlertLogout.setIcon(R.drawable.school_msi_1)
        materialAlertLogout.background =
            ResourcesCompat.getDrawable(resources, R.drawable.background_main_profile, theme)
        materialAlertLogout.setPositiveButton("Okay") { dg, _ ->
            dg.dismiss()

        }
        materialAlertLogout.create()
        materialAlertLogout.show()
        //code ends
    }

    private fun funLogout() {
        //code begins
        val materialAlertLogout = MaterialAlertDialogBuilder(this@StudentDash)
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

        //code ends
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
        funReturnHome()
    }

    private fun funProceedViewingResultSelection(textWhich: String) {

        //code begins
        //todo:begin here
        //code ends
    }

    private fun funInitGlobals() {
        this.title = "STUDENT DASHBOARD"
        //code begins
        textViewStudentAdmission = findViewById(R.id.studentAdmission)
        textViewStudentName = findViewById(R.id.studentName)
        textViewStudentGrade = findViewById(R.id.studentGrade)
        textViewStudentPoints = findViewById(R.id.studentPoints)
        textViewStudentForm = findViewById(R.id.studentForm)
        appCompatButtonViewClassTeacher = findViewById(R.id.btnClassTeacherDetails)
        appCompatButtonViewFees = findViewById(R.id.btnFeesPaid)
        appcompatButtonViewResults = findViewById(R.id.btnExamResults)
        circleImageViewStudent = findViewById(R.id.imgStudentProfile)
        appCompatButtonLogout = findViewById(R.id.btnLogoutStudentParent)

        //code ends
    }
}