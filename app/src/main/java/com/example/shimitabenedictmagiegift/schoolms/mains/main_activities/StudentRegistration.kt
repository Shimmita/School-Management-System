package com.example.shimitabenedictmagiegift.schoolms.mains.main_activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.NestedScrollView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.example.shimitabenedictmagiegift.schoolms.R
import com.example.shimitabenedictmagiegift.schoolms.mains.main_activities.MainProfile.Companion.SHARED_PREFERENCE_NAME
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.util.*

class StudentRegistration : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    companion object {
        private const val TAG = "StudentRegistration"
        private const val CAMERA_REQUEST_PARENT = 986;
        private const val CAMERA_REQUEST_STUDENT = 906;
        private const val PERMISSION_REQUEST_STUDENT_STORAGE_ACCESS = 776
        private const val PERMISSION_REQUEST_PARENT_STORAGE_ACCESS = 733
    }

    //sharedPreference declaration
    lateinit var sharedPreferences: SharedPreferences

    //declaration of the globals student
    lateinit var editTextStudentEnrolKey: EditText
    lateinit var editTextStudentFullName: EditText
    lateinit var editTextStudentEmail: EditText
    lateinit var editTextStudentPhone: EditText
    lateinit var editTextStudentDOB: EditText
    lateinit var editTextStudentPassword: EditText
    lateinit var editTextStudentDisability: EditText
    lateinit var editTextCountyOfBirth: EditText
    lateinit var editTextStudentAdmission: EditText
    lateinit var circleImageViewStudent: CircleImageView
    lateinit var spinnerStudentForm: Spinner
    lateinit var appCompatButtonPickStudentPhoto: AppCompatButton

    //
    //declaration of Globals parent
    lateinit var editTextParentFullName: EditText
    lateinit var editTextParentEmail: EditText
    lateinit var editTextParentPassword: EditText
    private lateinit var editTextParentPhone: EditText
    lateinit var circleImageViewParent: CircleImageView
    lateinit var appCompatButtonPickParentPhoto: AppCompatButton

    //
    private lateinit var linearLayoutParentDetails: LinearLayout
    private lateinit var linearLayoutStudentDetails: LinearLayout
    lateinit var appCompatButtonNext: AppCompatButton
    lateinit var appCompatButtonRegister: AppCompatButton
    lateinit var appCompatButtonBack: AppCompatButton

    //other globals
    lateinit var formSelection: String
    private var parentPhotoUri: Uri? = null
    private var studentPhotoUr: Uri? = null
    private lateinit var datePickerDialog: DatePickerDialog
    lateinit var calendar: Calendar
    //

    //declaration of bitmaps in case camera is used
    lateinit var bitmapParent: Bitmap
    lateinit var bitmapStudent: Bitmap

    //global student details in String
    lateinit var enrolKey: String
    lateinit var studentFullName: String
    lateinit var studentEmail: String
    lateinit var studentPhone: String
    lateinit var studentPass: String
    lateinit var studentDisability: String
    lateinit var studentAdmissionNumber: String
    lateinit var studentCounty: String
    lateinit var studentForm: String
    lateinit var studentDOB: String
    lateinit var nestedScrollViewStudent: NestedScrollView
    //

    private var schoolCodeIntent = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_registration)
        //title update
        title = "New Student Admission Form"
        //fun init Globals
        funInitGlobals()
        //other fun
        funInitOthersFun()
        //fetch intent data
        funGetDataFromSharedPreference()
        //end of onCreate
    }

    private fun funGetDataFromSharedPreference() {
        //code begins
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        val codeFromSharedPreference = sharedPreferences.getString("code", "").toString()
        if (codeFromSharedPreference.isNotEmpty()) {
            //save the value in the global schoolCode variable
            schoolCodeIntent = codeFromSharedPreference
            Log.d(TAG, "funGetDataIntent: $schoolCodeIntent")

        } else if (codeFromSharedPreference.isEmpty()) {
            //return main profile
            startActivity(Intent(this@StudentRegistration, MainProfile::class.java))
            finish()
        }

        //code ends
    }

    private fun funInitOthersFun() {
        //code begins

        editTextStudentDOB.setOnClickListener {
            //show datePicker dialog
            datePickerDialog.show()
        }

        //setting on listener on the btn
        appCompatButtonPickParentPhoto.setOnClickListener {
            //show photo options parent
            funShowParentPhotoOptions()
        }

        appCompatButtonPickStudentPhoto.setOnClickListener {
            //show photo options student
            funShowPhotoOptionsStudent()

        }


        appCompatButtonNext.setOnClickListener {
            //call fun evaluate entered student details before  continuation
            funEvaluateStudentDetailsFirst()
        }

        appCompatButtonRegister.setOnClickListener {
            //register student based on the legitimacy of the data
            funEvaluateParentDetailsGenerate()
        }

        appCompatButtonBack.setOnClickListener {
            //gone parent layout visible student layout
            linearLayoutParentDetails.visibility = View.GONE
            linearLayoutStudentDetails.visibility = View.VISIBLE
            //the title change to the student
            title = "New Student Admission Form"

        }


        //setting listener on the spinner
        spinnerStudentForm.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @SuppressLint("InflateParams")
            override fun onItemSelected(
                parent: AdapterView<*>?,
                p1: View?,
                position: Int,
                p3: Long
            ) {
                formSelection = parent?.getItemAtPosition(position).toString()

                val viewFormLevel = layoutInflater.inflate(R.layout.form_level_other, null, false)
                val edtStudentOtherLevel: EditText =
                    viewFormLevel.findViewById(R.id.edtStudentFormLevelOther) as EditText

                if (formSelection.contains("CBC", true) || formSelection.contains("other", true)) {
                    //show alert user to enter the other level

                    Toast.makeText(this@StudentRegistration, formSelection, Toast.LENGTH_SHORT)
                        .show()
                    //if the form level is other then show alert for the user to input the other level
                    val materialAlertOtherLevel =
                        MaterialAlertDialogBuilder(this@StudentRegistration)
                    materialAlertOtherLevel.setTitle("Other (CBC)")
                    materialAlertOtherLevel.setIcon(R.drawable.school_msi_1)
                    materialAlertOtherLevel.background = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.background_main_profile,
                        theme
                    )
                    materialAlertOtherLevel.setCancelable(false)
                    materialAlertOtherLevel.setView(viewFormLevel)
                    materialAlertOtherLevel.setPositiveButton("submit") { dg, _ ->
                        //obtain the text from the alert and save it in the form
                        formSelection = edtStudentOtherLevel.text.toString().trim()
                        dg.dismiss()
                    }
                    materialAlertOtherLevel.create()
                    materialAlertOtherLevel.show()
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                //leave empty
            }
        }


        //code ends

    }

    private fun funEvaluateParentDetailsGenerate() {
        //extract the parent details and evaluate them too
        val textParentName = editTextParentFullName.text.toString().trim().uppercase(Locale.ROOT)
        val textParentEmail = editTextParentEmail.text.toString().trim()
        val textParentPass = editTextParentPassword.text.toString().trim()
        val textParentPhone = editTextParentPhone.text.toString().trim()

        if (textParentEmail.isEmpty() || textParentName.isEmpty() || textParentPass.isEmpty() || textParentPhone.isEmpty()) {
            Toast.makeText(
                this@StudentRegistration,
                "some parent details are empty!",
                Toast.LENGTH_LONG
            ).show()

            //animate the parent linear layout
            linearLayoutParentDetails.apply {
                startAnimation(
                    AnimationUtils.loadAnimation(
                        this@StudentRegistration,
                        R.anim.shake
                    )
                )
            }
        } else if (Regex("\\d").containsMatchIn(textParentName)) {
            editTextParentFullName.error = "name should not contain numbers"
            editTextParentFullName.requestFocus()
            Toast.makeText(
                this@StudentRegistration,
                "name should not contain number!",
                Toast.LENGTH_LONG
            ).show()
        } else if (parentPhotoUri == null) {
            //require parent photo
            funRequireParentPhoto()
        } else {
            Log.d(TAG, "funEvaluateParentDetailsGenerate: begin 1")
            //call fun to register the student begin registering the parent first
            funRegisterStudent(
                textParentName, textParentEmail, textParentPass, textParentPhone,
                parentPhotoUri!!
            )
        }
    }

    private fun funRegisterStudent(
        textParentName: String,
        textParentEmail: String,
        textParentPass: String,
        textParentPhone: String,
        parentPhotoUri: Uri
    ) {
        //code begins
        //register the parent and extract his UId and transfer it to the student also after student registration
        //obtain student UID and update it in the parent

        //pg
        val sweetAlertDialogProgress =
            SweetAlertDialog(this@StudentRegistration, SweetAlertDialog.PROGRESS_TYPE)
        sweetAlertDialogProgress.setCancelable(false)
        sweetAlertDialogProgress.setTitle("Registering")
        sweetAlertDialogProgress.create()
        sweetAlertDialogProgress.show()

        //parent registration first
        val firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(textParentEmail, textParentPass)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG, "funRegisterStudent: begin 2")
                    //registered parent successfully obtain UID and proceed also get the schoolCode
                    sweetAlertDialogProgress.contentText = "processing"
                    val parentUID = firebaseAuth.currentUser?.uid.toString()
                    funPostParentImageStorage(
                        textParentName,
                        textParentEmail,
                        textParentPhone,
                        parentPhotoUri,
                        parentUID,
                        sweetAlertDialogProgress
                    )

                } else if (!it.isSuccessful) {
                    Log.d(TAG, "funRegisterStudent: failure 1")
                    //dismiss the main progress
                    sweetAlertDialogProgress.apply {
                        //dismiss the dialogProgress
                        dismiss()
                        //
                        funAlertFailure(it)
                        //
                    }

                }
            }
        //code ends
    }

    private fun funAlertFailure(task: Task<AuthResult>) {
        //

        val sweetAlertDialog =
            SweetAlertDialog(this@StudentRegistration, SweetAlertDialog.ERROR_TYPE)
        sweetAlertDialog.confirmText = "retry"
        sweetAlertDialog.cancelText = "return"
        sweetAlertDialog.setCancelable(false)
        sweetAlertDialog.titleText = "Registration Failed"
        sweetAlertDialog.contentText = task.exception?.message
        sweetAlertDialog.setConfirmClickListener {
            it.dismiss()

        }
        sweetAlertDialog.setCancelClickListener {
            it.dismiss()
            startActivity(Intent(this@StudentRegistration, MainProfile::class.java))
            finish()
        }
        sweetAlertDialog.create()
        sweetAlertDialog.show()
    }

    private fun funPostParentImageStorage(
        textParentName: String,
        textParentEmail: String,
        textParentPhone: String,
        parentPhotoUri: Uri,
        parentUID: String,
        sweetAlertDialogProgress: SweetAlertDialog
    ) {
        //convert the image into the bitmap format to compress it for lightweight upload
        val baos = ByteArrayOutputStream()
        returnBitmapImage(
            this@StudentRegistration,
            parentPhotoUri
        )?.compress(Bitmap.CompressFormat.JPEG, 25, baos)

        val byteArrayImageAfterCompression = baos.toByteArray()

        //path to parent(Parents/email/dataImage)
        val pathImageParent = "Parent/$textParentEmail"
        val storageParentImage = FirebaseStorage.getInstance().reference
        storageParentImage.child(pathImageParent).putBytes(byteArrayImageAfterCompression)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG, "funPostParentImageStorage: begin 3")
                    it.result.storage.downloadUrl.addOnCompleteListener {
                        if (it.isSuccessful) {
                            val downloadUriParent = it.result.toString()
                            funUploadStoreParentDetails(
                                downloadUriParent,
                                textParentName,
                                textParentEmail,
                                textParentPhone,
                                parentUID,
                                sweetAlertDialogProgress
                            )
                        } else if (!it.isSuccessful) {
                            sweetAlertDialogProgress.apply {
                                changeAlertType(SweetAlertDialog.ERROR_TYPE)
                                contentText = it.exception?.message
                                title = "Registration Failed"
                                confirmText = "retry"
                                cancelText = "return"
                                setConfirmClickListener {
                                    it.dismiss()
                                }
                                setCancelClickListener {
                                    dismiss()
                                    startActivity(
                                        Intent(
                                            this@StudentRegistration,
                                            MainProfile::class.java
                                        )
                                    )
                                    finish()
                                }

                            }
                        }
                    }

                } else if (!it.isSuccessful) {
                    Log.d(TAG, "funPostParentImageStorage: failure 2")
                    sweetAlertDialogProgress.apply {
                        //dismiss the progress
                        dismiss()
                        //alert error
                        funAlertFailureTaskSnapshot(it)
                    }
                }
            }
        //code ends
    }

    private fun funUploadStoreParentDetails(
        downloadUriParent: String,
        textParentName: String,
        textParentEmail: String,
        textParentPhone: String,
        parentUID: String,
        sweetAlertDialogProgress: SweetAlertDialog
    ) {
        //code begins
        //child will hold the UID of the child the parent is guardian for
        val keyName = "name"
        val keyPhone = "phone"
        val keyEmail = "email"
        val keyImage = "image"
        val keyUID = "uid"
        val keyChild = "child"
        val mapData = hashMapOf(
            keyName to textParentName,
            keyPhone to textParentPhone,
            keyEmail to textParentEmail,
            keyUID to parentUID,
            keyImage to downloadUriParent,
            keyChild to ""
        )

        //path parent data store(Parents/SchoolCode+UID)
        val collectionParents = "PARENTS"
        val documentParents = "$schoolCodeIntent$parentUID"

        val storeParents = FirebaseFirestore.getInstance()
        storeParents.collection(collectionParents).document(documentParents).set(mapData)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG, "funUploadStoreParentDetails: begin 4")
                    //proceed now registering the student
                    sweetAlertDialogProgress.contentText = "validating"
                    funProceedStudentSave(parentUID, sweetAlertDialogProgress)
                    //
                } else if (!it.isSuccessful) {
                    Log.d(TAG, "funUploadStoreParentDetails: failure 3")
                    sweetAlertDialogProgress.apply {
                        dismiss()
                        //alert failure
                        funAlertFailureTaskVoid(it)
                    }
                }
            }

        //code ends
    }

    private fun funProceedStudentSave(
        parentUID: String,
        sweetAlertDialogProgress: SweetAlertDialog
    ) {
        //code begins
        if (FirebaseAuth.getInstance().currentUser != null) {
            FirebaseAuth.getInstance().signOut()
            val firebaseAuthStudent = FirebaseAuth.getInstance()

            firebaseAuthStudent.createUserWithEmailAndPassword(studentEmail, studentPass)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(TAG, "funProceedStudentSave: begin 5")
                        val studentUID = firebaseAuthStudent.currentUser?.uid
                        funPostStudentImageStore(studentUID, parentUID, sweetAlertDialogProgress)
                    } else if (!it.isSuccessful) {
                        Log.d(TAG, "funProceedStudentSave: failure 4")
                        sweetAlertDialogProgress.apply {
                            dismiss()
                            //alert failure
                            funAlertFailure(it)
                        }
                    }
                }
        }

        //code ends
    }

    private fun funPostStudentImageStore(
        studentUID: String?,
        parentUID: String,
        sweetAlertDialogProgress: SweetAlertDialog
    ) {
        //convert the image into the bitmap format to compress it for lightweight upload
        val baos = ByteArrayOutputStream()
        studentPhotoUr?.let {
            returnBitmapImage(
                this@StudentRegistration,
                it
            )?.compress(Bitmap.CompressFormat.JPEG, 25, baos)
        }

        val byteArrayImageAfterCompression = baos.toByteArray()
        //path(Students/email)
        val pathStudentImage = "Students/$studentEmail"
        val storageParent = FirebaseStorage.getInstance().reference
        studentPhotoUr?.let {
            storageParent.child(pathStudentImage).putBytes(byteArrayImageAfterCompression).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG, "funPostStudentImageStore: begin 6")
                    it.result.storage.downloadUrl.addOnCompleteListener {
                        if (it.isSuccessful)
                        {
                            val downloadUriStudent=it.result.toString()
                            funPostStudentDetailsStore(
                                downloadUriStudent,
                                parentUID,
                                sweetAlertDialogProgress,
                                studentUID
                            )
                        }
                        else if (!it.isSuccessful)
                        {
                            //failed
                            sweetAlertDialogProgress.apply {
                                changeAlertType(SweetAlertDialog.ERROR_TYPE)
                                contentText = it.exception?.message
                                title = "Registration Failed"
                                confirmText = "retry"
                                cancelText = "return"
                                setConfirmClickListener {
                                    it.dismiss()
                                }
                                setCancelClickListener {
                                    dismiss()
                                    startActivity(
                                        Intent(
                                            this@StudentRegistration,
                                            MainProfile::class.java
                                        )
                                    )
                                    finish()
                                }

                            }
                        }
                    }

                } else if (!it.isSuccessful) {
                    Log.d(TAG, "funPostStudentImageStore: failure 5")
                    sweetAlertDialogProgress.dismiss()
                    //alert failure
                    funAlertFailureTaskSnapshot(it)
                }
            }
        }
        //code ends
    }

    private fun funPostStudentDetailsStore(
        downloadUriStudent: String,
        parentUID: String,
        sweetAlertDialogProgress: SweetAlertDialog,
        studentUID: String?
    ) {
        studentUID.toString()
        //code begins
        //pathStudentDetails(schoolCode/Adm/uniqueUID)
        val pathDocumentStudent = "$schoolCodeIntent$studentAdmissionNumber$studentUID"
        val keyName = "name"
        val keyEmail = "email"
        val keyPhone = "phone"
        val keyDisability = "disability"
        val keyAdmission = "adm"
        val keyParentUId = "parent"
        val keyCounty = "county"
        val keyForm = "form"
        val keyDOB = "dob"
        val keyImage = "image"
        val mapData = hashMapOf(
            keyName to studentFullName.uppercase(Locale.ROOT),
            keyEmail to studentEmail,
            keyPhone to studentPhone,
            keyDisability to studentDisability,
            keyAdmission to studentAdmissionNumber,
            keyParentUId to parentUID,
            keyCounty to studentCounty,
            keyForm to studentForm,
            keyDOB to studentDOB,
            keyImage to downloadUriStudent
        )

        val storeStudentDetails = FirebaseFirestore.getInstance()
        storeStudentDetails.collection("STUDENTS").document(pathDocumentStudent).set(mapData)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG, "funPostStudentDetailsStore: begin 7")
                    //student registered successfully update parent child and also add more collections
                    funUpdateParentWithChild(
                        studentUID,
                        parentUID,
                        sweetAlertDialogProgress
                    )
                } else if (!it.isSuccessful) {
                    Log.d(TAG, "funPostStudentDetailsStore: failure 6")
                    sweetAlertDialogProgress.apply {
                        dismiss()
                        //alert error
                        funAlertFailureTaskVoid(it)
                    }
                }
            }
        //code ends
    }

    private fun funUpdateParentWithChild(
        studentUID: String?,
        parentUID: String,
        sweetAlertDialogProgress: SweetAlertDialog,
    ) {
        val keyChild = "child"
        val mapDataUpdate = hashMapOf(keyChild to studentUID.toString())

        val storeUpdateParent = FirebaseFirestore.getInstance()
        storeUpdateParent.collection("PARENTS").document("$schoolCodeIntent$parentUID").update(
            mapDataUpdate as Map<String, Any>
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d(TAG, "funUpdateParentWithChild: begin 8")
                //updated parent successfully
                sweetAlertDialogProgress.apply {
                    contentText = "saving"
                    //call fun proceed creation of other student Collections i.e exams,fees,classTeacher
                    val stringStudentID = studentUID.toString()
                    funCreateCollectionStudentExam(
                        sweetAlertDialogProgress,
                        stringStudentID)
                }
            }
            if (!it.isSuccessful) {
                Log.d(TAG, "funUpdateParentWithChild: failure 7")
                sweetAlertDialogProgress.apply {
                    dismiss()
                    funAlertFailureTaskVoid(it)
                }
            }
        }
    }

    private fun funCreateCollectionStudentExam(
        sweetAlertDialogProgress: SweetAlertDialog,
        stringStudentID: String
    ) {
        //code begins
        //create collection studentExam
        val keyGrade = "grade"
        val keyPoints = "points"
        val keyMath = "math"
        val keyEnglish = "english"
        val keyKiswahili = "kiswahili"
        val keyPhysics = "physics"
        val keyChemistry = "chemistry"
        val keyBiology = "biology"
        val keyGeography = "geography"
        val keyHistory = "history"
        val keyCRE = "cre"
        val keyAgriculture = "agriculture"
        val keyComputer = "computer"
        val keyBusiness = "business"

        val mapData = hashMapOf(
            keyGrade to "",
            keyPoints to "",
            keyMath to "",
            keyEnglish to "",
            keyKiswahili to "",
            keyPhysics to "",
            keyChemistry to "",
            keyBiology to "",
            keyGeography to "",
            keyHistory to "",
            keyCRE to "",
            keyAgriculture to "",
            keyComputer to "",
            keyBusiness to ""
        )

        val store = FirebaseFirestore.getInstance()
        store.collection("STUDENT RESULTS")
            .document("$schoolCodeIntent$studentAdmissionNumber$stringStudentID").set(mapData)
            .addOnCompleteListener {

                if (it.isSuccessful) {
                    Log.d(TAG, "funCreateCollectionStudentExam: begin 9")
                    //create the fees collection
                    funCreateCollectionFees(
                        stringStudentID,
                        sweetAlertDialogProgress,
                        store
                    )

                } else if (!it.isSuccessful) {
                    Log.d(TAG, "funCreateCollectionStudentExam: failure 8")
                    sweetAlertDialogProgress.apply {
                        dismiss()
                        //alert failure
                        funAlertFailureTaskVoid(it)
                    }
                }
            }
        //code ends
    }

    private fun funCreateCollectionClassTeacher(
        sweetAlertDialogProgress: SweetAlertDialog,
        stringStudentID: String,
        store: FirebaseFirestore,
    ) {

        //code
        val keyTeacherName = "name"
        val keyTeacherPhone = "phone"
        val keyTeacherSubject = "subject"
        val keyTeacherImage = "image"
        val keyTeacherForm="form"
        val mapData = hashMapOf(
            keyTeacherName to "",
            keyTeacherPhone to "",
            keyTeacherSubject to "",
            keyTeacherImage to "",
            keyTeacherForm to ""
        )
        store.collection("CLASS TEACHER")
            .document("$schoolCodeIntent$studentAdmissionNumber$stringStudentID")
            .set(mapData)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG, "funCreateCollectionClassTeacher: begin 11")
                    //completely registered
                    sweetAlertDialogProgress.apply {
                        changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                        titleText="Successful"
                        contentText="student registered"
                        confirmText = "ok"
                        setConfirmClickListener {
                            //sign out the current user(student registered
                            if (FirebaseAuth.getInstance().currentUser != null) {
                                FirebaseAuth.getInstance().signOut()
                            }
                            //

                            it.dismiss()
                            //migrate to main profile
                            startActivity(Intent(this@StudentRegistration, MainProfile::class.java))
                            finish()
                        }
                    }
                    //
                } else if (!it.isSuccessful) {
                    Log.d(TAG, "funCreateCollectionClassTeacher: failure 10")
                    sweetAlertDialogProgress.apply {
                        dismiss()
                        //alert error
                        funAlertFailureTaskVoid(it)
                    }
                }

            }
        //code ends
    }

    private fun funCreateCollectionFees(
        stringStudentID: String,
        sweetAlertDialogProgress: SweetAlertDialog,
        store: FirebaseFirestore,
    ) {
        //code begins
        val keyPaid = "paid"
        val keyBalance = "balance"
        val keyWhole = "required"

        val mapData = hashMapOf(keyPaid to "", keyBalance to "", keyWhole to "")
        store.collection("FEES")
            .document("$schoolCodeIntent$studentAdmissionNumber$stringStudentID").set(mapData)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG, "funCreateCollectionFees: begin 10")
                    sweetAlertDialogProgress.contentText = "finishing"
                    //finalize creating class teacher collection
                    funCreateCollectionClassTeacher(
                        sweetAlertDialogProgress,
                        stringStudentID,
                        store
                    )
                } else if (!it.isSuccessful) {
                    Log.d(TAG, "funCreateCollectionFees: failure 9")
                    sweetAlertDialogProgress.apply {
                        dismiss()
                        //alert error
                        funAlertFailureTaskVoid(it)
                    }
                }
            }
        //code ends
    }


    private fun funAlertFailureTaskVoid(it: Task<Void>) {
        //

        val sweetAlertDialog =
            SweetAlertDialog(this@StudentRegistration, SweetAlertDialog.ERROR_TYPE)
        sweetAlertDialog.confirmText = "retry"
        sweetAlertDialog.cancelText = "return"
        sweetAlertDialog.setCancelable(false)
        sweetAlertDialog.titleText = "Registration Failed"
        sweetAlertDialog.contentText = it.exception?.message
        sweetAlertDialog.setConfirmClickListener {
            it.dismiss()

        }
        sweetAlertDialog.setCancelClickListener {
            it.dismiss()
            startActivity(Intent(this@StudentRegistration, MainProfile::class.java))
            finish()
        }
        sweetAlertDialog.create()
        sweetAlertDialog.show()
    }

    private fun funAlertFailureTaskSnapshot(it: Task<UploadTask.TaskSnapshot>) {
        //

        val sweetAlertDialog =
            SweetAlertDialog(this@StudentRegistration, SweetAlertDialog.ERROR_TYPE)
        sweetAlertDialog.confirmText = "retry"
        sweetAlertDialog.cancelText = "return"
        sweetAlertDialog.setCancelable(false)
        sweetAlertDialog.titleText = "Registration Failed"
        sweetAlertDialog.contentText = it.exception?.message
        sweetAlertDialog.setConfirmClickListener {
            it.dismiss()

        }
        sweetAlertDialog.setCancelClickListener {
            it.dismiss()
            startActivity(Intent(this@StudentRegistration, MainProfile::class.java))
            finish()
        }
        sweetAlertDialog.create()
        sweetAlertDialog.show()
    }

    private fun funRequireParentPhoto() {
        //alert student photo required
        val materialAlertPhoto = MaterialAlertDialogBuilder(this@StudentRegistration)
        materialAlertPhoto.setIcon(R.drawable.ic_insert_photo)
        materialAlertPhoto.setTitle("student photo")
        materialAlertPhoto.setMessage(
            "detected student photo is not provided\n" +
                    "\nFix:provide parent photo from the gallery!"
        )
        materialAlertPhoto.setCancelable(false)
        materialAlertPhoto.setPositiveButton("okay") { dg, _ ->
            dg.dismiss()

            //animate the student photo
            circleImageViewStudent.apply {
                postDelayed({
                    circleImageViewStudent.borderColor =
                        getColor(cn.pedant.SweetAlert.R.color.red_btn_bg_color)
                    startAnimation(
                        AnimationUtils.loadAnimation(
                            this@StudentRegistration,
                            R.anim.shake
                        )
                    )
                    circleImageViewStudent.requestFocus()
                }, 2000)
            }
        }
        materialAlertPhoto.background = ResourcesCompat.getDrawable(
            resources,
            R.drawable.background_main_profile,
            theme
        )
        materialAlertPhoto.create()
        materialAlertPhoto.show()
    }

    private fun funEvaluateStudentDetailsFirst() {

        //move next to the layout parent details only if the student details have been filled
        //obtaining the text from the edt
        enrolKey = editTextStudentEnrolKey.text.toString().trim()
        studentFullName = editTextStudentFullName.text.toString().trim().uppercase(Locale.ROOT)
        studentEmail = editTextStudentEmail.text.toString().trim()
        studentPhone = editTextStudentPhone.text.toString().trim()
        studentDOB = editTextStudentDOB.text.toString().trim()
        studentPass = editTextStudentPassword.text.toString().trim()
        studentDisability = editTextStudentDisability.text.toString().trim()
        studentCounty = editTextCountyOfBirth.text.toString().trim()
        studentForm = formSelection.trim()
        studentAdmissionNumber = editTextStudentAdmission.text.toString().trim()

        //legitimacy check only student disability can be empty
        if (enrolKey.isEmpty() || studentFullName.isEmpty() || studentEmail.isEmpty()
            || studentPhone.isEmpty() || studentDOB.isEmpty()
            || studentPass.isEmpty() || studentCounty.isEmpty() || studentForm.isEmpty()
            || studentAdmissionNumber.isEmpty()
        ) {
            Toast.makeText(
                this@StudentRegistration,
                "cannot submit empty fields!",
                Toast.LENGTH_SHORT
            ).show()

            //animate student layout
            linearLayoutStudentDetails.apply {
                startAnimation(AnimationUtils.loadAnimation(this@StudentRegistration, R.anim.shake))
            }
        } else if (studentPhotoUr == null) {
            //call fun warn student photo required
            funStudentPhotoRequired()
        } else if (Regex("\\d").containsMatchIn(studentFullName)) {
            //code ends
            funShowAlertNameNoLetters()
            //

        } else if (Regex("\\d").containsMatchIn(studentCounty)) {
            //code begins
            funAlertShowCountyNoNumbers()
            //
        } else if (studentPhone.contains(",") || studentPhone.contains(".")) {
            //admission number contains invalid characters
            Toast.makeText(
                this@StudentRegistration,
                "admission number is invalid!",
                Toast.LENGTH_SHORT
            ).show()
            editTextStudentPhone.error = "should not contain , or ."

            //animate student layout
            linearLayoutStudentDetails.apply {
                startAnimation(AnimationUtils.loadAnimation(this@StudentRegistration, R.anim.shake))
            }

            //
        } else {
            //disable the student view linear and then show the  parent details layout
            linearLayoutStudentDetails.visibility = View.GONE
            linearLayoutParentDetails.visibility = View.VISIBLE
            //set the title action bar title to parent
            this.title = "Parent/Guardian Details Form"
        }


    }

    private fun funStudentPhotoRequired() {

        //alert student photo required
        val materialAlertPhoto = MaterialAlertDialogBuilder(this@StudentRegistration)
        materialAlertPhoto.setIcon(R.drawable.ic_insert_photo)
        materialAlertPhoto.setTitle("student photo")
        materialAlertPhoto.setMessage(
            "detected student photo is not provided\n" +
                    "\nFix:provide student photo from the gallery!"
        )
        materialAlertPhoto.setCancelable(false)
        materialAlertPhoto.setPositiveButton("okay") { dg, _ ->
            dg.dismiss()

            //animate the student photo
            circleImageViewStudent.apply {
                postDelayed({
                    circleImageViewStudent.borderColor =
                        getColor(cn.pedant.SweetAlert.R.color.red_btn_bg_color)
                    startAnimation(
                        AnimationUtils.loadAnimation(
                            this@StudentRegistration,
                            R.anim.shake
                        )
                    )
                    circleImageViewStudent.requestFocus()
                }, 2000)
            }
        }
        materialAlertPhoto.background = ResourcesCompat.getDrawable(
            resources,
            R.drawable.background_main_profile,
            theme
        )
        materialAlertPhoto.create()
        materialAlertPhoto.show()
    }

    private fun funShowPhotoOptionsStudent() {
        val materialAlert = MaterialAlertDialogBuilder(this)
        materialAlert.setCancelable(false)
        materialAlert.setTitle("Photo Options")
        materialAlert.setMessage(
            "Camera: opens the camera to capture the student image\n" +
                    "\nGallery: opens device gallery or photos from where you can select the image of the student"
        )
        materialAlert.background =
            ResourcesCompat.getDrawable(resources, R.drawable.background_main_profile, theme)
        materialAlert.setPositiveButton("Camera") { dg, _ ->
            dg.dismiss()
            //call camera fun
            Toast.makeText(
                this@StudentRegistration,
                "currently unavailable use gallery",
                Toast.LENGTH_LONG
            ).show()
            //todo: implement/add functionality of how the image captured by the camera will be saved
            // funStartCameraCapture("student")
            //

        }
        materialAlert.setIcon(R.drawable.ic_insert_photo)
        materialAlert.setNegativeButton("Gallery") { dg, _ ->

            dg.dismiss()
            //call gallery fun to pick images from
            funPickFromGallery("student")
            //
        }
        materialAlert.create()
        materialAlert.show()

    }

    private fun funShowParentPhotoOptions() {


        val materialAlert = MaterialAlertDialogBuilder(this)
        materialAlert.setCancelable(false)
        materialAlert.setTitle("Photo Options")
        materialAlert.setMessage(
            "Camera: opens the camera to capture the parent image\n" +
                    "\nGallery: opens device gallery or photos from where you can select the image of the parent"
        )
        materialAlert.background =
            ResourcesCompat.getDrawable(resources, R.drawable.background_main_profile, theme)
        materialAlert.setPositiveButton("Camera") { dg, _ ->
            dg.dismiss()
            //call camera fun
            Toast.makeText(
                this@StudentRegistration,
                "currently unavailable use gallery",
                Toast.LENGTH_LONG
            ).show()
            //todo: implement/add functionality of how the image captured by the camera will be saved
            // funStartCameraCapture("parent")
            //
        }
        materialAlert.setIcon(R.drawable.ic_insert_photo)
        materialAlert.setNegativeButton("Gallery") { dg, _ ->

            dg.dismiss()
            //call gallery fun to pick images from
            funPickFromGallery("parent")
            //
        }
        materialAlert.create()
        materialAlert.show()
    }

    private fun funStartCameraCapture(s: String) {
        //check if the camera permission is granted or not
        if (ContextCompat.checkSelfPermission(
                this@StudentRegistration,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //permission granted begin capture
            cameraCaptureBegin(s)

        } else {
            //permission not granted request
            funRequestCameraPermission(s)
        }
    }

    private fun funRequestCameraPermission(s: String) {
        if (s.contains("student")) {
            ActivityCompat.requestPermissions(
                this@StudentRegistration, arrayOf(Manifest.permission.CAMERA),
                CAMERA_REQUEST_STUDENT
            )
        } else if (s.contains("parent")) {
            ActivityCompat.requestPermissions(
                this@StudentRegistration, arrayOf(Manifest.permission.CAMERA),
                CAMERA_REQUEST_PARENT
            )
        }
    }

    private fun funPickFromGallery(s: String) {

        /*if ( ActivityCompat.checkSelfPermission(
                this@StudentRegistration,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(
                this@StudentRegistration,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //call fun to request permissions
            funRequestPermissions(s)
        } else {
            //permissions already granted launch intent pick image gallery
            funPickImage(s)
        }*/

        if (s.contains("parent", true)) {
            //intent launch load parent image
            val intentParentLaunch = Intent()
            intentParentLaunch.type = "image/*"
            intentParentLaunch.action = Intent.ACTION_GET_CONTENT
            galleryActivityImageParent.launch(intentParentLaunch)

        } else if (s.contains("student", true)) {
            //intent launch student
            val intentStudentPick = Intent()
            intentStudentPick.type = "image/*"
            intentStudentPick.action = Intent.ACTION_GET_CONTENT
            galleryRequestPickStudentImage.launch(intentStudentPick)
        }

    }

    private fun funPickImage(s: String) {
        if (s.contains("parent", true)) {
            Log.d(TAG, "funPickImage: parent pick img intent")

            val intentParentPhoto = Intent()
            intentParentPhoto.action = Intent.ACTION_GET_CONTENT
            intentParentPhoto.type = "image/*"
            galleryRequestPickParentImage.launch(intentParentPhoto)
        } else if (s.contains("student", true)) {
            Log.d(TAG, "funPickImage: student pick img intent")

            val intentStudentPhoto = Intent()
            intentStudentPhoto.action = Intent.ACTION_GET_CONTENT
            intentStudentPhoto.type = "image/*"
            galleryRequestPickStudentImage.launch(intentStudentPhoto)
        }
    }

    private fun funInitGlobals() {
        spinnerStudentForm = findViewById(R.id.spinnerStudentForm)
        circleImageViewStudent = findViewById(R.id.imgStudentPhoto)
        appCompatButtonPickStudentPhoto = findViewById(R.id.btnPickStudentPhoto)
        editTextStudentEnrolKey = findViewById(R.id.edtStudentEnrolKey)
        editTextStudentFullName = findViewById(R.id.edtStudentFullNameReg)
        editTextStudentEmail = findViewById(R.id.edtStudentEmailReg)
        editTextStudentPhone = findViewById(R.id.edtStudentPhonReg)
        editTextCountyOfBirth = findViewById(R.id.edtStudentCounty)
        editTextStudentDOB = findViewById(R.id.edtStudentDOB)
        editTextStudentPassword = findViewById(R.id.edtStudentPassword)
        editTextStudentDisability = findViewById(R.id.edtStudentDisability)
        editTextStudentAdmission = findViewById(R.id.edtStudentAdmissionNumberReg)

        circleImageViewParent = findViewById(R.id.imgParentPhoto)
        appCompatButtonPickParentPhoto = findViewById(R.id.btnPickParentPhoto)
        editTextParentFullName = findViewById(R.id.edtParentNameReg)
        editTextParentEmail = findViewById(R.id.edtParentEmail)
        editTextParentPassword = findViewById(R.id.edtParentPassReg)
        editTextParentPhone = findViewById(R.id.edtParentPhonReg)
        linearLayoutParentDetails = findViewById(R.id.linearParentDetails)
        linearLayoutStudentDetails = findViewById(R.id.linearStudentDetails)
        appCompatButtonRegister = findViewById(R.id.btnGenRegStudent)
        appCompatButtonNext = findViewById(R.id.btnNext)
        appCompatButtonBack = findViewById(R.id.tvBack)
        nestedScrollViewStudent = findViewById(R.id.nestedStudentDetails)

        calendar = Calendar.getInstance()
        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH)
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
        datePickerDialog = DatePickerDialog(this, this, year, month, day)


        //adding form data on to the spinner
        val arrayAdapterForms = ArrayAdapter.createFromResource(
            this@StudentRegistration,
            R.array.form_level,
            android.R.layout.simple_list_item_1
        )
        spinnerStudentForm.adapter = arrayAdapterForms


        //shared preferences
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        //
    }

    private val galleryRequestPickParentImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                parentPhotoUri = it.data?.data!!
                //using the Glide library to load the image
                Glide.with(this@StudentRegistration).load(parentPhotoUri)
                    .into(circleImageViewParent)
            } else
                if (it.resultCode == RESULT_CANCELED) {
                    AlertDialog.Builder(this@StudentRegistration)
                        .setCancelable(false)
                        .setTitle("Cancelled!")
                        .setMessage("parent photo is required and is essential element during the process of data capture")
                        .setPositiveButton("ok") { dg, _ ->
                            dg.dismiss()
                            //return to main profile
                            startActivity(Intent(this@StudentRegistration, MainProfile::class.java))
                            finish()
                            //
                        }
                        .show()
                        .create()
                }

        }

    private val galleryRequestPickStudentImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                studentPhotoUr = it.data?.data!!
                //using the glide library to load the image
                Glide.with(this@StudentRegistration).load(studentPhotoUr)
                    .into(circleImageViewStudent)
            } else
                if (it.resultCode == RESULT_CANCELED) {
                    AlertDialog.Builder(this@StudentRegistration)
                        .setCancelable(false)
                        .setTitle("Cancelled!")
                        .setMessage("student photo is required and is essential element during the data capture process")
                        .setPositiveButton("ok") { dg, _ ->
                            dg.dismiss()
                            //return to main profile
                            startActivity(Intent(this@StudentRegistration, MainProfile::class.java))
                            finish()
                            //
                        }
                        .show()
                        .create()
                }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //student request
        if (requestCode == PERMISSION_REQUEST_STUDENT_STORAGE_ACCESS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permissions granted launch intent pick image
                funPickImage("student")
                //
            } else {
                //permissions completely denied
                AlertDialog.Builder(this@StudentRegistration)
                    .setCancelable(false)
                    .setMessage("allow the requested permissions in the application info (APP INFO) in order to use the service")
                    .setPositiveButton("ok") { dg, _ ->
                        dg.dismiss()
                        //return to main profile
                        startActivity(Intent(this@StudentRegistration, MainProfile::class.java))
                        finish()
                        //
                    }
                    .show()
                    .create()
            }
        }
        //parent request
        else if (requestCode == PERMISSION_REQUEST_PARENT_STORAGE_ACCESS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permissions granted launch intent pick image
                funPickImage("parent")
                //
            } else {
                //permissions completely denied
                AlertDialog.Builder(this@StudentRegistration)
                    .setCancelable(false)
                    .setTitle("Permissions Not Granted")
                    .setMessage("allow the requested permissions in the application info (APP INFO) in order to use the service")
                    .setPositiveButton("ok") { dg, _ ->
                        dg.dismiss()
                        //return to main profile
                        startActivity(Intent(this@StudentRegistration, MainProfile::class.java))
                        finish()
                        //
                    }
                    .show()
                    .create()
            }

        }
        //camera request student
        else if (requestCode == CAMERA_REQUEST_STUDENT) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission has been grant capture image
                cameraCaptureBegin("student")
            } else {
                //permission completely denied
                AlertDialog.Builder(this@StudentRegistration)
                    .setCancelable(false)
                    .setTitle("Permissions Not Granted")
                    .setMessage("allow the requested permissions in the application info (APP INFO) in order to use the service")
                    .setPositiveButton("ok") { dg, _ ->
                        dg.dismiss()
                        //return to main profile
                        startActivity(Intent(this@StudentRegistration, MainProfile::class.java))
                        finish()
                        //
                    }
                    .show()
                    .create()
            }
        } else if (requestCode == CAMERA_REQUEST_PARENT) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission granted
                cameraCaptureBegin("parent")
            } else {
                //permission completely denied
                AlertDialog.Builder(this@StudentRegistration)
                    .setCancelable(false)
                    .setTitle("Permissions Not Granted")
                    .setMessage("allow the requested permissions in the application info (APP INFO) in order to use the service")
                    .setPositiveButton("ok") { dg, _ ->
                        dg.dismiss()
                        //return to main profile
                        startActivity(Intent(this@StudentRegistration, MainProfile::class.java))
                        finish()
                        //
                    }
                    .show()
                    .create()
            }
        }
    }

    @Suppress("deprecation")
    private fun cameraCaptureBegin(s: String) {
        if (s.contains("student")) {
            val intentCamera = Intent()
            intentCamera.action = MediaStore.ACTION_IMAGE_CAPTURE
            startActivityForResult(intentCamera, CAMERA_REQUEST_STUDENT)
        } else if (s.contains("parent")) {
            val intentCamera = Intent()
            intentCamera.action = MediaStore.ACTION_IMAGE_CAPTURE
            startActivityForResult(intentCamera, CAMERA_REQUEST_PARENT)
        }
    }

    private fun funRequestPermissions(s: String) {
        if (s.contains("student")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ActivityCompat.requestPermissions(
                    this@StudentRegistration, arrayOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_STUDENT_STORAGE_ACCESS
                )
            } else {
                ActivityCompat.requestPermissions(
                    this@StudentRegistration, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_STUDENT_STORAGE_ACCESS
                )
            }
        } else if (s.contains("parent")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ActivityCompat.requestPermissions(
                    this@StudentRegistration, arrayOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_PARENT_STORAGE_ACCESS
                )
            } else {
                ActivityCompat.requestPermissions(
                    this@StudentRegistration, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_PARENT_STORAGE_ACCESS
                )
            }
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_STUDENT) {
            if (resultCode == RESULT_OK) {
                val bundleData = data?.extras
                val bitmapImage: Bitmap = bundleData?.get("data") as Bitmap
                //load image with the glide
                circleImageViewStudent.setImageBitmap(bitmapImage)
            } else if (resultCode == RESULT_CANCELED) {
                //cancelled by user
                //cancelled by the user
                AlertDialog.Builder(this@StudentRegistration)
                    .setCancelable(false)
                    .setTitle("Cancelled!")
                    .setMessage("student photo is required and essential element during the data capture process")
                    .setPositiveButton("ok") { dg, _ ->
                        dg.dismiss()
                        //return to main profile
                        startActivity(Intent(this@StudentRegistration, MainProfile::class.java))
                        finish()
                        //
                    }
                    .show()
                    .create()

            }


        } else if (requestCode == CAMERA_REQUEST_PARENT) {
            if (resultCode == RESULT_OK) {
                val bundleData = data?.extras
                val bitmapImage: Bitmap = bundleData?.get("data") as Bitmap
                //load image with the glide
                circleImageViewParent.setImageBitmap(bitmapImage)

            } else if (resultCode == RESULT_CANCELED) {
                //cancelled by the user
                AlertDialog.Builder(this@StudentRegistration)
                    .setCancelable(false)
                    .setTitle("Cancelled!")
                    .setMessage("parent photo is required and essential element during the data capture process")
                    .setPositiveButton("ok") { dg, _ ->
                        dg.dismiss()
                        //return to main profile
                        startActivity(Intent(this@StudentRegistration, MainProfile::class.java))
                        finish()
                        //
                    }
                    .show()
                    .create()
            }

        }
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        val finalMonth = month + 1
        var stringDate = ""
        stringDate = if (finalMonth > 9) {
            "$day/$finalMonth/$year"
        } else {
            "$day/0$finalMonth/$year"
        }

        editTextStudentDOB.setText(stringDate)
    }

    private fun funAlertShowCountyNoNumbers() {
        //code begins
        val materialAlertPhoto = MaterialAlertDialogBuilder(this@StudentRegistration)
        materialAlertPhoto.setCancelable(false)
        materialAlertPhoto.background =
            ResourcesCompat.getDrawable(resources, R.drawable.background_main_profile, theme)
        materialAlertPhoto.setIcon(R.drawable.school_msi_1)
        materialAlertPhoto.setMessage("county name provided contains numbers!\n\nfix: provide a valid county name. It should not contain numbers")
        materialAlertPhoto.setTitle("county name")
        materialAlertPhoto.setPositiveButton("okay") { dg, _ ->

            //dismiss the dialog
            editTextCountyOfBirth.error = "provide valid name"
            editTextCountyOfBirth.requestFocus()
            dg.dismiss()
            //
        }
        materialAlertPhoto.create()
        materialAlertPhoto.show()
        //code ends
    }

    private fun funShowAlertNameNoLetters() {

        //alert user profile photo is required
        val materialAlertPhoto = MaterialAlertDialogBuilder(this@StudentRegistration)
        materialAlertPhoto.setCancelable(false)
        materialAlertPhoto.background =
            ResourcesCompat.getDrawable(resources, R.drawable.background_main_profile, theme)
        materialAlertPhoto.setIcon(R.drawable.school_msi_1)
        materialAlertPhoto.setMessage("the name provided contains numbers!\n\nfix: provide a valid name. It should not contain numbers")
        materialAlertPhoto.setTitle("staff name")
        materialAlertPhoto.setPositiveButton("okay") { dg, _ ->

            //dismiss the dialog
            editTextStudentFullName.error = "provide valid name"
            editTextStudentFullName.requestFocus()
            dg.dismiss()
            //
        }
        materialAlertPhoto.create()
        materialAlertPhoto.show()
    }


    private val galleryActivityImageParent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            //pick image for the parent
            if (it.resultCode == RESULT_OK) {
                parentPhotoUri = it.data?.data!!

                Glide.with(this@StudentRegistration).load(parentPhotoUri)
                    .into(circleImageViewParent)
            } else if (it.resultCode == RESULT_CANCELED) {
                //process halted
                AlertDialog.Builder(this@StudentRegistration)
                    .setCancelable(false)
                    .setTitle("Cancelled!")
                    .setMessage("parent photo is required and is essential element during the process of data capture")
                    .setPositiveButton("ok") { dg, _ ->
                        dg.dismiss()
                        //return to main profile
                        startActivity(Intent(this@StudentRegistration, MainProfile::class.java))
                        finish()
                        //
                    }
                    .show()
                    .create()
            }
            //
        }

    private fun returnBitmapImage(context: Context, uri: Uri): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            Log.d(TAG, "returnBitmapImage: error:${e.message}")
            Toast.makeText(this@StudentRegistration, "something went wrong!", Toast.LENGTH_SHORT)
                .show()
            null
        }
    }

}