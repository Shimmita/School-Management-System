package com.example.shimitabenedictmagiegift.schoolms.mains.main_activities

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.NestedScrollView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.example.shimitabenedictmagiegift.schoolms.R
import com.example.shimitabenedictmagiegift.schoolms.mains.main_activities.MainProfile.Companion.SHARED_PREFERENCE_NAME
import com.example.shimitabenedictmagiegift.schoolms.mains.main_activities.SplashActivity.Companion.COLLECTION_KEYS
import com.example.shimitabenedictmagiegift.schoolms.mains.main_activities.SplashActivity.Companion.DOCUMENT_KEYS
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.util.*

class StaffRegistration : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    companion object {
        private const val TAG = "StaffRegistration"
        const val TEACHER_KEY = "teacher_key"
        const val ADMINISTRATION_KEY="admin_key"
        const val COLLECTION_TEACHER = "Teacher"
        const val COLLECTION_BURSA = "Bursa"
        const val COLLECTION_DEPUTY = "Deputy"
        const val COLLECTION_PRINCIPAL = "Principal"

    }

    //declaration of the Globals
    private lateinit var spinnerRole: Spinner
    lateinit var editTextRoleStaff: EditText
    lateinit var editTextStaffEnrolKey: EditText
    lateinit var editTextStaffName: EditText
    lateinit var edtStaffEmail: EditText
    lateinit var editTextStaffPhone: EditText
    lateinit var edtStaffDOB: EditText
    lateinit var edtStaffPassword: EditText
    lateinit var editTextStaffDisability: EditText
    lateinit var edtStaffCounty: EditText
    lateinit var appCompatButtonRegisterStaff: AppCompatButton
    lateinit var appcompatPickStaffPhoto: AppCompatButton
    lateinit var circleImageViewStaff: CircleImageView

    //
    private lateinit var datePickerDialog: DatePickerDialog

    //text association from the edits
    lateinit var textEnrolKey: String
    lateinit var textStaffRole: String
    lateinit var textStaffFullName: String
    lateinit var textStaffEmail: String
    lateinit var textStaffPhone: String
    lateinit var textStaffDOB: String
    lateinit var textStaffDisability: String
    lateinit var textPassword: String
    lateinit var textStaffCounty: String
    private var photoUriStaffPhoto: Uri? = null
    lateinit var linearLayoutStaff: NestedScrollView

    var schoolCode = ""
    var schoolName = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.staff_registration)
        //set the title action bar to staff
        this.title = "Staff Registration"
        //
        funInitGlobals()
        //fun to init other events and fun
        funInitOtherGlobals()

        //animScroll of the
        // funAnimScrollLinear()
    }

    private fun funAnimScrollLinear() {
        //code begins
        linearLayoutStaff.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            val linearLayoutStaff: LinearLayout = findViewById(R.id.linearStaffDetails)
            if (scrollY > oldScrollY) {
                //scrolling downwards
                linearLayoutStaff.layoutAnimation = LayoutAnimationController(
                    AnimationUtils.loadAnimation(
                        this@StaffRegistration,
                        R.anim.slide_in_left
                    )
                )
                linearLayoutStaff.startLayoutAnimation()
            } else {
                //scrolling upwards
                linearLayoutStaff.layoutAnimation = LayoutAnimationController(
                    AnimationUtils.loadAnimation(
                        this@StaffRegistration,
                        R.anim.slide_in_right
                    )
                )
                linearLayoutStaff.startLayoutAnimation()
            }
        }
        //code ends
    }

    private fun funInitOtherGlobals() {
        //code begins
        //setting onclick listener on the edtDob to show the datePicker
        edtStaffDOB.setOnClickListener {
            //show datePicker
            datePickerDialog.show()
        }

        //setting listener to the btn register staff
        appCompatButtonRegisterStaff.setOnClickListener {
            //call funToRegister Staff after evaluations
            it.startAnimation(AnimationUtils.loadAnimation(this@StaffRegistration, R.anim.fade_out))

            it.postDelayed({
                funEvaluateStaffDetails()
            }, 500)
        }

        //setting listener to the btn pick image
        appcompatPickStaffPhoto.setOnClickListener {
            funPickImageStaff()
        }
        //code ends
    }

    private fun funPickImageStaff() {
        //code begins
        val intentPickStaffPhoto = Intent()
        intentPickStaffPhoto.type = "image/*"
        intentPickStaffPhoto.action = Intent.ACTION_GET_CONTENT
        galleryActivityStaff.launch(intentPickStaffPhoto)
        //code ends
    }

    private fun funEvaluateStaffDetails() {
        //code begins
        textStaffCounty = edtStaffCounty.text.toString().trim()
        textEnrolKey = editTextStaffEnrolKey.text.toString().trim()
        textStaffRole = editTextRoleStaff.text.toString().trim()
        textStaffFullName = editTextStaffName.text.toString().trim().uppercase(Locale.ROOT)
        textStaffEmail = edtStaffEmail.text.toString().trim()
        textStaffPhone = editTextStaffPhone.text.toString().trim()
        textStaffDOB = edtStaffDOB.text.toString().trim()
        textPassword = edtStaffPassword.text.toString().trim()
        textStaffDisability = editTextStaffDisability.text.toString()
        textStaffCounty = edtStaffCounty.text.toString()

        //begin evaluate of the code
        if (textEnrolKey.isEmpty()
            || textStaffRole.isEmpty()
            || textStaffFullName.isEmpty()
            || textStaffEmail.isEmpty()
            || textStaffPhone.isEmpty()
            || textStaffDOB.isEmpty()
            || textPassword.isEmpty()
            || textStaffCounty.isEmpty()
        ) {
            val messageText = "cannot submit empty fields!"
            val fixMessageTex = "dully fill all the required fields"
            funAlertEmptyFields(messageText, fixMessageTex)
        } else if (photoUriStaffPhoto == null) {
            //alert user profile photo is required
            val materialAlertPhoto = MaterialAlertDialogBuilder(this@StaffRegistration)
            materialAlertPhoto.setCancelable(false)
            materialAlertPhoto.background =
                ResourcesCompat.getDrawable(resources, R.drawable.background_main_profile, theme)
            materialAlertPhoto.setIcon(R.drawable.ic_insert_photo)
            materialAlertPhoto.setMessage("profile picture of the staff has not been provided!\n\nfix: provide profile picture")
            materialAlertPhoto.setTitle("profile photo")
            materialAlertPhoto.setPositiveButton("okay") { dg, _ ->

                //dismiss the dialog
                circleImageViewStaff.apply {
                    startAnimation(
                        AnimationUtils.loadAnimation(
                            this@StaffRegistration,
                            R.anim.rotate_avg
                        )
                    )
                    borderColor =
                        resources.getColor(cn.pedant.SweetAlert.R.color.red_btn_bg_color, theme)
                }
                dg.dismiss()
                //
            }
            materialAlertPhoto.create()
            materialAlertPhoto.show()
        } else if (Regex("\\d").containsMatchIn(textStaffFullName)) {
            //name should not contain letters
            funShowAlertNameNoLetters()

        } else if (Regex("\\d").containsMatchIn(textStaffCounty)) {
            funAlertShowCountyNoNumbers()

        } else if (textStaffPhone.contains(".") || textStaffPhone.contains(",")) {
            Toast.makeText(this@StaffRegistration, "error phone invalid", Toast.LENGTH_SHORT).show()
            editTextStaffPhone.error = "should not contain . or ,"
            linearLayoutStaff.apply {
                startAnimation(AnimationUtils.loadAnimation(this@StaffRegistration, R.anim.shake))
            }
        } else {
            //code begins

            //register staff according to the role
            if (textStaffRole.contains("principal", ignoreCase = true)) {
                //fun register principal
                funRegisterPrincipal()
            } else if (textStaffRole.contains("deputy", ignoreCase = true)) {
                //fun register deputy principal
                funRegisterDeputyPrincipal()
            } else if (textStaffRole.contains("teacher", ignoreCase = true)) {
                funAlertClassTeacherFor()
            } else if (textStaffRole.contains("bursa", ignoreCase = true)) {
                //fun register bursa
                funRegisterBursa()
            }
        }

        //code ends
    }

    private fun funRegisterPrincipal() {
        funCheckEnrolKeyPrincipal()
    }

    private fun funCheckEnrolKeyPrincipal() {
        val sweetAlertDialog =
            SweetAlertDialog(this@StaffRegistration, SweetAlertDialog.PROGRESS_TYPE)
        sweetAlertDialog.titleText = "processing"
        sweetAlertDialog.contentText = "validating"
        sweetAlertDialog.setCancelable(false)
        sweetAlertDialog.create()
        sweetAlertDialog.show()
        //store check the staff enrol key
        val storeCheckEnrol = FirebaseFirestore.getInstance()
        storeCheckEnrol.collection(COLLECTION_KEYS).document(DOCUMENT_KEYS).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val keyReturned = it.result.get(ADMINISTRATION_KEY)
                    if (textEnrolKey == keyReturned) {
                        //proceed with registration process key is true
                        funProceedRegistrationPrincipal(sweetAlertDialog)
                        //
                    } else {
                        //halt registration key incorrect
                        sweetAlertDialog.apply {
                            contentText = "invalid enrol key"
                            titleText = "Registration Failed"
                            changeAlertType(SweetAlertDialog.WARNING_TYPE)
                            confirmText = "check"
                            cancelText = "home"
                            setConfirmClickListener {
                                //dismiss since user wants to check the key and might register agin
                                it.dismiss()
                            }
                            setCancelClickListener {
                                it.dismiss()
                                funReturnMainHome()
                            }
                        }
                    }
                } else if (!it.isSuccessful) {
                    sweetAlertDialog.apply {
                        sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE)
                        titleText = "registration failed"
                        contentText = "${it.exception?.message}"
                        confirmText = "ok"
                        setConfirmClickListener {
                            it.dismiss()
                            funReturnMainHome()
                        }
                    }
                }
            }

    }

    private fun funProceedRegistrationPrincipal(sweetAlertDialog: SweetAlertDialog) {

        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.createUserWithEmailAndPassword(textStaffEmail, textPassword)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val principalUniqueUID = FirebaseAuth.getInstance().currentUser?.uid.toString()
                    funStorePrincipal(sweetAlertDialog, principalUniqueUID)
                } else if (!it.isSuccessful) {
                    sweetAlertDialog.apply {
                        contentText = it.exception?.message
                        titleText = "Registration Failed!"
                        confirmText = "retry"
                        cancelText = "home"
                        setCancelClickListener {
                            it.dismiss()
                            funReturnMainHome()
                        }
                        setConfirmClickListener {
                            it.dismiss()
                        }
                    }
                }
            }

    }

    private fun funStorePrincipal(sweetAlertDialog: SweetAlertDialog, principalUniqueUID: String) {
        //convert the image into the bitmap format to compress it for lightweight upload
        val baos = ByteArrayOutputStream()
        photoUriStaffPhoto?.let {
            returnBitmapImage(
                this@StaffRegistration,
                it
            )?.compress(Bitmap.CompressFormat.JPEG, 25, baos)
        }
        val byteArrayImageAfterCompression = baos.toByteArray()
        //path(Bursa/email)
        val path = "Principal/$textStaffEmail"
        val storage = FirebaseStorage.getInstance().reference
        storage.child(path).putBytes(byteArrayImageAfterCompression).addOnCompleteListener {
            if (it.isSuccessful) {
                //obtain download url
                it.result.storage.downloadUrl.addOnCompleteListener {
                    if (it.isSuccessful) {
                        sweetAlertDialog.contentText = "almost done"
                        val photoPath = it.result.toString()
                        funPostStorePrincipalData(
                            principalUniqueUID,
                            sweetAlertDialog,
                            photoPath
                        )
                    } else if (!it.isSuccessful) {
                        //failed error
                        sweetAlertDialog.apply {
                            titleText = "registration failed"
                            contentText = "${it.exception?.message}"
                            confirmText = "retry"
                            cancelText = "home"
                            setConfirmClickListener {
                                it.dismiss()
                            }
                            setCancelClickListener {
                                funReturnMainHome()
                            }
                        }
                    }
                }
            } else if (!it.isSuccessful) {
                //failed error
                sweetAlertDialog.apply {
                    titleText = "registration failed"
                    contentText = "${it.exception?.message}"
                    confirmText = "retry"
                    cancelText = "home"
                    setConfirmClickListener {
                        it.dismiss()
                    }
                    setCancelClickListener {
                        funReturnMainHome()
                    }
                }
            }
        }
    }

    private fun funPostStorePrincipalData(
        principalUniqueUID: String,
        sweetAlertDialog: SweetAlertDialog,
        photoPath: String
    ) {
        //code begins
        val keyEmail = "email"
        val keyCounty = "county"
        val keyUID = "id"
        val keyDisability = "disability"
        val keyPhone = "phone"
        val keyName = "name"
        val keyBirth = "dob"
        val keyPhoto = "image"

        val mapData = hashMapOf(
            keyName to textStaffFullName,
            keyEmail to textStaffEmail,
            keyPhone to textStaffPhone,
            keyCounty to textStaffCounty,
            keyBirth to textStaffDOB,
            keyUID to principalUniqueUID,
            keyDisability to textStaffDisability,
            keyPhoto to photoPath,
        )

        //path(Bursa/schoolCode+UID)
        val storeStaffData = FirebaseFirestore.getInstance()
        storeStaffData.collection(COLLECTION_PRINCIPAL).document("$schoolCode$principalUniqueUID")
            .set(mapData).addOnCompleteListener {
                if (it.isSuccessful) {
                    //sign out current user
                    FirebaseAuth.getInstance().signOut()
                    sweetAlertDialog.apply {
                        changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                        titleText = "Registration Successful"
                        contentText = "registered"
                        cancelText = "home"
                        setConfirmClickListener {
                            it.dismiss()
                            funReturnMainHome()
                        }
                    }

                } else if (!it.isSuccessful) {
                    //failed error
                    sweetAlertDialog.apply {
                        titleText = "registration failed"
                        contentText = "${it.exception?.message}"
                        confirmText = "retry"
                        cancelText = "home"
                        setConfirmClickListener {
                            it.dismiss()
                        }
                        setCancelClickListener {
                            it.dismiss()
                            funReturnMainHome()
                        }
                    }
                }
            }
    }

    private fun funRegisterDeputyPrincipal() {
        funCheckEnrolKeyDeputy()
    }

    private fun funCheckEnrolKeyDeputy() {

        val sweetAlertDialog =
            SweetAlertDialog(this@StaffRegistration, SweetAlertDialog.PROGRESS_TYPE)
        sweetAlertDialog.titleText = "processing"
        sweetAlertDialog.contentText = "validating"
        sweetAlertDialog.setCancelable(false)
        sweetAlertDialog.create()
        sweetAlertDialog.show()
        //store check the staff enrol key
        val storeCheckEnrol = FirebaseFirestore.getInstance()
        storeCheckEnrol.collection(COLLECTION_KEYS).document(DOCUMENT_KEYS).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val keyReturned = it.result.get(ADMINISTRATION_KEY)
                    if (textEnrolKey == keyReturned) {
                        //proceed with registration process key is true
                        funProceedRegistrationDeputy(sweetAlertDialog)
                        //
                    } else {
                        //halt registration key incorrect
                        sweetAlertDialog.apply {
                            contentText = "invalid enrol key"
                            titleText = "Registration Failed"
                            changeAlertType(SweetAlertDialog.WARNING_TYPE)
                            confirmText = "check"
                            cancelText = "home"
                            setConfirmClickListener {
                                //dismiss since user wants to check the key and might register agin
                                it.dismiss()
                            }
                            setCancelClickListener {
                                it.dismiss()
                                funReturnMainHome()
                            }
                        }
                    }
                } else if (!it.isSuccessful) {
                    sweetAlertDialog.apply {
                        sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE)
                        titleText = "registration failed"
                        contentText = "${it.exception?.message}"
                        confirmText = "ok"
                        setConfirmClickListener {
                            it.dismiss()
                            funReturnMainHome()
                        }
                    }
                }
            }
    }

    private fun funProceedRegistrationDeputy(sweetAlertDialog: SweetAlertDialog) {

        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.createUserWithEmailAndPassword(textStaffEmail, textPassword)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val deputyUniqueUID = FirebaseAuth.getInstance().currentUser?.uid.toString()
                    funStoreDeputyPhoto(sweetAlertDialog, deputyUniqueUID)
                } else if (!it.isSuccessful) {
                    sweetAlertDialog.apply {
                        contentText = it.exception?.message
                        titleText = "Registration Failed!"
                        confirmText = "retry"
                        cancelText = "home"
                        setCancelClickListener {
                            it.dismiss()
                            funReturnMainHome()
                        }
                        setConfirmClickListener {
                            it.dismiss()
                        }
                    }
                }
            }
    }

    private fun funStoreDeputyPhoto(sweetAlertDialog: SweetAlertDialog, deputyUniqueUID: String) {
        //convert the image into the bitmap format to compress it for lightweight upload
        val baos = ByteArrayOutputStream()
        photoUriStaffPhoto?.let {
            returnBitmapImage(
                this@StaffRegistration,
                it
            )?.compress(Bitmap.CompressFormat.JPEG, 25, baos)
        }
        val byteArrayImageAfterCompression = baos.toByteArray()
        //path(Bursa/email)
        val path = "Deputy/$textStaffEmail"
        val storage = FirebaseStorage.getInstance().reference
        storage.child(path).putBytes(byteArrayImageAfterCompression).addOnCompleteListener {
            if (it.isSuccessful) {
                //obtain download url
                it.result.storage.downloadUrl.addOnCompleteListener {
                    if (it.isSuccessful) {
                        sweetAlertDialog.contentText = "almost done"
                        val photoPath = it.result.toString()
                        funPostStoreDeputyData(
                            deputyUniqueUID,
                            sweetAlertDialog,
                            photoPath
                        )
                    } else if (!it.isSuccessful) {
                        //failed error
                        sweetAlertDialog.apply {
                            titleText = "registration failed"
                            contentText = "${it.exception?.message}"
                            confirmText = "retry"
                            cancelText = "home"
                            setConfirmClickListener {
                                it.dismiss()
                            }
                            setCancelClickListener {
                                funReturnMainHome()
                            }
                        }
                    }
                }
            } else if (!it.isSuccessful) {
                //failed error
                sweetAlertDialog.apply {
                    titleText = "registration failed"
                    contentText = "${it.exception?.message}"
                    confirmText = "retry"
                    cancelText = "home"
                    setConfirmClickListener {
                        it.dismiss()
                    }
                    setCancelClickListener {
                        funReturnMainHome()
                    }
                }
            }
        }
    }

    private fun funPostStoreDeputyData(
        deputyUniqueUID: String,
        sweetAlertDialog: SweetAlertDialog,
        photoPath: String
    ) {
        //code begins
        val keyEmail = "email"
        val keyCounty = "county"
        val keyUID = "id"
        val keyDisability = "disability"
        val keyPhone = "phone"
        val keyName = "name"
        val keyBirth = "dob"
        val keyPhoto = "image"

        val mapData = hashMapOf(
            keyName to textStaffFullName,
            keyEmail to textStaffEmail,
            keyPhone to textStaffPhone,
            keyCounty to textStaffCounty,
            keyBirth to textStaffDOB,
            keyUID to deputyUniqueUID,
            keyDisability to textStaffDisability,
            keyPhoto to photoPath,
        )

        //path(Bursa/schoolCode+UID)
        val storeStaffData = FirebaseFirestore.getInstance()
        storeStaffData.collection(COLLECTION_DEPUTY).document("$schoolCode$deputyUniqueUID")
            .set(mapData).addOnCompleteListener {
                if (it.isSuccessful) {
                    //sign out current user
                    FirebaseAuth.getInstance().signOut()
                    sweetAlertDialog.apply {
                        changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                        titleText = "Registration Successful"
                        contentText = "registered"
                        cancelText = "home"
                        setConfirmClickListener {
                            it.dismiss()
                            funReturnMainHome()
                        }
                    }

                } else if (!it.isSuccessful) {
                    //failed error
                    sweetAlertDialog.apply {
                        titleText = "registration failed"
                        contentText = "${it.exception?.message}"
                        confirmText = "retry"
                        cancelText = "home"
                        setConfirmClickListener {
                            it.dismiss()
                        }
                        setCancelClickListener {
                            funReturnMainHome()
                        }
                    }
                }
            }

    }

    private fun funRegisterBursa() {
        //code begins
        funCheckEnrollmentKey()
        //code ends
    }

    private fun funCheckEnrollmentKey() {
        val sweetAlertDialog =
            SweetAlertDialog(this@StaffRegistration, SweetAlertDialog.PROGRESS_TYPE)
        sweetAlertDialog.titleText = "processing"
        sweetAlertDialog.contentText = "validating"
        sweetAlertDialog.setCancelable(false)
        sweetAlertDialog.create()
        sweetAlertDialog.show()
        //store check the staff enrol key
        val storeCheckEnrol = FirebaseFirestore.getInstance()
        storeCheckEnrol.collection(COLLECTION_KEYS).document(DOCUMENT_KEYS).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val keyReturned = it.result.get(ADMINISTRATION_KEY)
                    if (textEnrolKey == keyReturned) {
                        //proceed with registration process key is true
                        funProceedRegistrationBursa(sweetAlertDialog)
                        //
                    } else {
                        //halt registration key incorrect
                        sweetAlertDialog.apply {
                            contentText = "invalid enrol key"
                            titleText = "Registration Failed"
                            changeAlertType(SweetAlertDialog.WARNING_TYPE)
                            confirmText = "check"
                            cancelText = "home"
                            setConfirmClickListener {
                                //dismiss since user wants to check the key and might register agin
                                it.dismiss()
                            }
                            setCancelClickListener {
                                it.dismiss()
                                funReturnMainHome()
                            }
                        }
                    }
                } else if (!it.isSuccessful) {
                    sweetAlertDialog.apply {
                        sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE)
                        titleText = "registration failed"
                        contentText = "${it.exception?.message}"
                        confirmText = "ok"
                        setConfirmClickListener {
                            it.dismiss()
                            funReturnMainHome()
                        }
                    }
                }
            }

    }

    private fun funProceedRegistrationBursa(sweetAlertDialog: SweetAlertDialog) {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.createUserWithEmailAndPassword(textStaffEmail, textPassword)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val bursaUniqueUID = FirebaseAuth.getInstance().currentUser?.uid.toString()
                    funStoreBursaPhoto(sweetAlertDialog, bursaUniqueUID)
                } else if (!it.isSuccessful) {
                    sweetAlertDialog.apply {
                        contentText = it.exception?.message
                        titleText = "Registration Failed!"
                        confirmText = "retry"
                        cancelText = "home"
                        setCancelClickListener {
                            it.dismiss()
                            funReturnMainHome()
                        }
                        setConfirmClickListener {
                            it.dismiss()
                        }
                    }
                }
            }

    }

    private fun funStoreBursaPhoto(sweetAlertDialog: SweetAlertDialog, bursaUniqueUID: String) {
        //code begins
        //convert the image into the bitmap format to compress it for lightweight upload
        val baos = ByteArrayOutputStream()
        photoUriStaffPhoto?.let {
            returnBitmapImage(
                this@StaffRegistration,
                it
            )?.compress(Bitmap.CompressFormat.JPEG, 25, baos)
        }
        val byteArrayImageAfterCompression = baos.toByteArray()
        //path(Bursa/email)
        val path = "Bursa/$textStaffEmail"
        val storage = FirebaseStorage.getInstance().reference
        storage.child(path).putBytes(byteArrayImageAfterCompression).addOnCompleteListener {
            if (it.isSuccessful) {
                //obtain download url
                it.result.storage.downloadUrl.addOnCompleteListener {
                    if (it.isSuccessful) {
                        sweetAlertDialog.contentText = "almost done"
                        val photoPath = it.result.toString()
                        funPostStoreBursaData(
                            bursaUniqueUID,
                            sweetAlertDialog,
                            photoPath
                        )
                    } else if (!it.isSuccessful) {
                        //failed error
                        sweetAlertDialog.apply {
                            titleText = "registration failed"
                            contentText = "${it.exception?.message}"
                            confirmText = "retry"
                            cancelText = "home"
                            setConfirmClickListener {
                                it.dismiss()
                            }
                            setCancelClickListener {
                                funReturnMainHome()
                            }
                        }
                    }
                }
            } else if (!it.isSuccessful) {
                //failed error
                sweetAlertDialog.apply {
                    titleText = "registration failed"
                    contentText = "${it.exception?.message}"
                    confirmText = "retry"
                    cancelText = "home"
                    setConfirmClickListener {
                        it.dismiss()
                    }
                    setCancelClickListener {
                        funReturnMainHome()
                    }
                }
            }
        }

    }

    private fun funPostStoreBursaData(
        bursaUniqueUID: String,
        sweetAlertDialog: SweetAlertDialog,
        photoPath: String
    ) {
        //code begins
        val keyEmail = "email"
        val keyCounty = "county"
        val keyUID = "id"
        val keyDisability = "disability"
        val keyPhone = "phone"
        val keyName = "name"
        val keyBirth = "dob"
        val keyPhoto = "image"

        val mapData = hashMapOf(
            keyName to textStaffFullName,
            keyEmail to textStaffEmail,
            keyPhone to textStaffPhone,
            keyCounty to textStaffCounty,
            keyBirth to textStaffDOB,
            keyUID to bursaUniqueUID,
            keyDisability to textStaffDisability,
            keyPhoto to photoPath,
        )

        //path(Bursa/schoolCode+UID)
        val storeStaffData = FirebaseFirestore.getInstance()
        storeStaffData.collection(COLLECTION_BURSA).document("$schoolCode$bursaUniqueUID")
            .set(mapData).addOnCompleteListener {
                if (it.isSuccessful) {
                    //sign out current user
                    FirebaseAuth.getInstance().signOut()
                    sweetAlertDialog.apply {
                        changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                        titleText = "Registration Successful"
                        contentText = "registered"
                        cancelText = "home"
                        setConfirmClickListener {
                            it.dismiss()
                            funReturnMainHome()
                        }
                    }

                } else if (!it.isSuccessful) {
                    //failed error
                    sweetAlertDialog.apply {
                        titleText = "registration failed"
                        contentText = "${it.exception?.message}"
                        confirmText = "retry"
                        cancelText = "home"
                        setConfirmClickListener {
                            it.dismiss()
                        }
                        setCancelClickListener {
                            funReturnMainHome()
                        }
                    }
                }
            }

    }

    private fun funAlertClassTeacherFor() {
        //code begins
        val view: View = layoutInflater.inflate(R.layout.class_teacher_for, null, false)
        val editTextFor: EditText = view.findViewById(R.id.edtClassTeacherFor)
        val materialAlertDialogBuilderOther = MaterialAlertDialogBuilder(this@StaffRegistration)
        materialAlertDialogBuilderOther.setCancelable(false)
        materialAlertDialogBuilderOther.setView(view)
        materialAlertDialogBuilderOther.background =
            ResourcesCompat.getDrawable(resources, R.drawable.background_main_profile, theme)
        materialAlertDialogBuilderOther.setIcon(R.drawable.school_msi_1)
        materialAlertDialogBuilderOther.setTitle("class teacher role")
        materialAlertDialogBuilderOther.setPositiveButton("proceed") { dg, _ ->
            //code begins
            val selection = editTextFor.text.toString().trim()
            if (selection.contains("other", true)) {
                funCreateOtherAlert()
            } else {
                //teacher selection does not contain other (cbc)
                funBeginRegistrationTeacher(selection)

            }

            dg.dismiss()
            //code ends
        }
        materialAlertDialogBuilderOther.setNegativeButton("dismiss") { dg, _ ->
            dg.dismiss()
        }
        materialAlertDialogBuilderOther.setNeutralButton("home") { dg, _ ->
            //code begins
            dg.dismiss()
            startActivity(Intent(this@StaffRegistration, MainProfile::class.java))
            finish()
            //code begins
        }
        materialAlertDialogBuilderOther.create()
        materialAlertDialogBuilderOther.show()


        val spinner: Spinner = view.findViewById(R.id.spinnerClassTeacherFor)
        val adapter = ArrayAdapter.createFromResource(
            this@StaffRegistration,
            R.array.class_teacher_form,
            android.R.layout.simple_list_item_1
        )
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val textSelectionFor = p0?.getItemAtPosition(p2).toString().trim()

                editTextFor.setText(textSelectionFor)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                //nothing
            }

        }

        //code ends
    }

    private fun funCreateOtherAlert() {


        //contains other cbc
        val viewOther: View =
            layoutInflater.inflate(R.layout.provide_other_teacher, null, false)

        val editTextOther: EditText = viewOther.findViewById(R.id.edtTeacherFormLevelOther)

        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(this@StaffRegistration)
        materialAlertDialogBuilder.background = ResourcesCompat.getDrawable(
            resources,
            R.drawable.background_main_profile,
            theme
        )
        materialAlertDialogBuilder.setTitle("Provide Other (CBC)")
        materialAlertDialogBuilder.setCancelable(false)
        materialAlertDialogBuilder.setView(viewOther)
        materialAlertDialogBuilder.setIcon(R.drawable.school_msi_1)
        materialAlertDialogBuilder.setPositiveButton("proceed") { d, _ ->

            val otherTextSelection = editTextOther.text.toString().trim()
            if (otherTextSelection.isEmpty()) {
                Toast.makeText(
                    this@StaffRegistration,
                    "empty fields not allowed!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                funBeginRegistrationTeacher(otherTextSelection)
                d.dismiss()
            }
        }
        materialAlertDialogBuilder.setNegativeButton("dismiss") { dialog, _ -> dialog.dismiss() }
        materialAlertDialogBuilder.create()
        materialAlertDialogBuilder.show()
    }

    private fun funBeginRegistrationTeacher(selection: String) {


        val firebaseAuth = FirebaseAuth.getInstance()
        //sign out eny user that is existing
        if (firebaseAuth.currentUser != null) {
            FirebaseAuth.getInstance().signOut()
            funCheckEnrollKey(selection)
        }
        else
        {
            funCheckEnrollKey(selection)
        }
        //code ends
    }

    private fun funCheckEnrollKey(selection: String) {
        //code begins
        val sweetAlertDialog =
            SweetAlertDialog(this@StaffRegistration, SweetAlertDialog.PROGRESS_TYPE)
        sweetAlertDialog.titleText = "processing"
        sweetAlertDialog.contentText = "validating"
        sweetAlertDialog.setCancelable(false)
        sweetAlertDialog.create()
        sweetAlertDialog.show()

        //store check the staff enrol key
        val storeCheckEnrol = FirebaseFirestore.getInstance()
        storeCheckEnrol.collection(COLLECTION_KEYS).document(DOCUMENT_KEYS).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val keyReturned = it.result.get(TEACHER_KEY)
                    if (textEnrolKey == keyReturned) {
                        //proceed with registration process key is true
                        funProceedRegistrationTeacher(selection, sweetAlertDialog)
                        //
                    } else {
                        //halt registration key incorrect
                        sweetAlertDialog.apply {
                            contentText = "invalid enrol key"
                            titleText = "Registration Failed"
                            changeAlertType(SweetAlertDialog.WARNING_TYPE)
                            confirmText = "check"
                            cancelText = "home"
                            setConfirmClickListener {
                                //dismiss since user wants to check the key and might register agin
                                it.dismiss()
                            }
                            setCancelClickListener {
                                it.dismiss()
                                funReturnMainHome()
                            }
                        }
                    }
                } else if (!it.isSuccessful) {
                    sweetAlertDialog.apply {
                        sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE)
                        titleText = "registration failed"
                        contentText = "${it.exception?.message}"
                        confirmText = "ok"
                        setConfirmClickListener {
                            it.dismiss()
                            funReturnMainHome()
                        }
                    }
                }
            }

        //code ends
    }

    private fun funProceedRegistrationTeacher(
        selection: String,
        sweetAlertDialog: SweetAlertDialog
    ) {
        //code begins
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.createUserWithEmailAndPassword(textStaffEmail, textPassword)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val teacherUniqueUID = FirebaseAuth.getInstance().currentUser?.uid.toString()
                    funStoreTeacherPhoto(selection, sweetAlertDialog, teacherUniqueUID)
                } else if (!it.isSuccessful) {
                    sweetAlertDialog.apply {
                        contentText = it.exception?.message
                        titleText = "Registration Failed!"
                        confirmText = "retry"
                        cancelText = "home"
                        setCancelClickListener {
                            it.dismiss()
                            funReturnMainHome()
                        }
                        setConfirmClickListener {
                            it.dismiss()
                        }
                    }
                }
            }
        //code ends
    }

    private fun funStoreTeacherPhoto(
        selection: String,
        sweetAlertDialog: SweetAlertDialog,
        teacherUniqueUID: String
    ) {
        //code begins
        //convert the image into the bitmap format to compress it for lightweight upload
        val baos = ByteArrayOutputStream()
        photoUriStaffPhoto?.let {
            returnBitmapImage(
                this@StaffRegistration,
                it
            )?.compress(Bitmap.CompressFormat.JPEG, 25, baos)
        }
        val byteArrayImageAfterCompression = baos.toByteArray()
        //path(Teacher/email)
        val path = "Teacher/$textStaffEmail"
        val storage = FirebaseStorage.getInstance().reference
        storage.child(path).putBytes(byteArrayImageAfterCompression).addOnCompleteListener {
            if (it.isSuccessful) {
                //obtain download url
                it.result.storage.downloadUrl.addOnCompleteListener {
                    if (it.isSuccessful) {
                        sweetAlertDialog.contentText = "almost done"
                        val photoPath = it.result.toString()
                        funPostStoreStaffData(
                            selection,
                            teacherUniqueUID,
                            sweetAlertDialog,
                            photoPath
                        )
                    } else if (!it.isSuccessful) {
                        //failed error
                        sweetAlertDialog.apply {
                            titleText = "registration failed"
                            contentText = "${it.exception?.message}"
                            confirmText = "retry"
                            cancelText = "home"
                            setConfirmClickListener {
                                it.dismiss()
                            }
                            setCancelClickListener {
                                funReturnMainHome()
                            }
                        }
                    }
                }
            } else if (!it.isSuccessful) {
                //failed error
                sweetAlertDialog.apply {
                    titleText = "registration failed"
                    contentText = "${it.exception?.message}"
                    confirmText = "retry"
                    cancelText = "home"
                    setConfirmClickListener {
                        it.dismiss()
                    }
                    setCancelClickListener {
                        funReturnMainHome()
                    }
                }
            }
        }
        //code ends
    }

    private fun funPostStoreStaffData(
        selection: String,
        teacherUniqueUID: String,
        sweetAlertDialog: SweetAlertDialog,
        photoPath: String
    ) {
        //code begins
        val keyEmail = "email"
        val keyClassTeacherFor = "form"
        val keyCounty = "county"
        val keyUID = "id"
        val keyDisability = "disability"
        val keyPhone = "phone"
        val keyName = "name"
        val keyBirth = "dob"
        val keyPhoto = "image"

        val mapData = hashMapOf(
            keyName to textStaffFullName,
            keyEmail to textStaffEmail,
            keyPhone to textStaffPhone,
            keyCounty to textStaffCounty,
            keyBirth to textStaffDOB,
            keyUID to teacherUniqueUID,
            keyDisability to textStaffDisability,
            keyPhoto to photoPath,
            keyClassTeacherFor to selection
        )

        //path(Teacher/schoolCode+UID)
        val storeStaffData = FirebaseFirestore.getInstance()
        storeStaffData.collection(COLLECTION_TEACHER).document("$schoolCode$teacherUniqueUID")
            .set(mapData).addOnCompleteListener {
                if (it.isSuccessful) {
                    //sign out current user
                    FirebaseAuth.getInstance().signOut()
                    sweetAlertDialog.apply {
                        changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                        titleText = "Registration Successful"
                        contentText = "registered"
                        cancelText=""
                        confirmText = "home"
                        setConfirmClickListener {
                            it.dismiss()
                            funReturnMainHome()
                        }
                    }

                } else if (!it.isSuccessful) {
                    //failed error
                    sweetAlertDialog.apply {
                        titleText = "registration failed"
                        contentText = "${it.exception?.message}"
                        confirmText = "retry"
                        cancelText = "home"
                        setConfirmClickListener {
                            it.dismiss()
                        }
                        setCancelClickListener {
                            funReturnMainHome()
                        }
                    }
                }
            }

        //code ends
    }

    private fun funReturnMainHome() {
        //code begins

        if (FirebaseAuth.getInstance().currentUser != null) {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this@StaffRegistration, MainProfile::class.java))
            finish()
        } else {
            startActivity(Intent(this@StaffRegistration, MainProfile::class.java))
            finish()
        }


        //code ends
    }

    private fun funAlertEmptyFields(messageText: String, fixMessageTex: String) {
        //alert user profile photo is required
        val materialAlertPhoto = MaterialAlertDialogBuilder(this@StaffRegistration)
        materialAlertPhoto.setCancelable(false)
        materialAlertPhoto.background =
            ResourcesCompat.getDrawable(resources, R.drawable.background_main_profile, theme)
        materialAlertPhoto.setIcon(R.drawable.school_msi_1)
        materialAlertPhoto.setMessage("$messageText\n\nfix:$fixMessageTex")
        materialAlertPhoto.setTitle("missing field(s)")
        materialAlertPhoto.setPositiveButton("okay") { dg, _ ->
            dg.dismiss()

            linearLayoutStaff.startAnimation(
                AnimationUtils.loadAnimation(
                    this@StaffRegistration,
                    R.anim.shake
                )
            )
        }
        materialAlertPhoto.create()
        materialAlertPhoto.show()
    }


    private fun funAlertShowCountyNoNumbers() {
        //code begins
        val materialAlertPhoto = MaterialAlertDialogBuilder(this@StaffRegistration)
        materialAlertPhoto.setCancelable(false)
        materialAlertPhoto.background =
            ResourcesCompat.getDrawable(resources, R.drawable.background_main_profile, theme)
        materialAlertPhoto.setIcon(R.drawable.school_msi_1)
        materialAlertPhoto.setMessage("county name provided contains numbers!\n\nfix: provide a valid county name. It should not contain numbers")
        materialAlertPhoto.setTitle("county name")
        materialAlertPhoto.setPositiveButton("okay") { dg, _ ->

            //dismiss the dialog
            editTextStaffName.error = "provide valid name"
            editTextStaffName.requestFocus()
            dg.dismiss()
            //
        }
        materialAlertPhoto.create()
        materialAlertPhoto.show()
        //code ends
    }

    private fun funShowAlertNameNoLetters() {

        //alert user profile photo is required
        val materialAlertPhoto = MaterialAlertDialogBuilder(this@StaffRegistration)
        materialAlertPhoto.setCancelable(false)
        materialAlertPhoto.background =
            ResourcesCompat.getDrawable(resources, R.drawable.background_main_profile, theme)
        materialAlertPhoto.setIcon(R.drawable.school_msi_1)
        materialAlertPhoto.setMessage("the name provided contains numbers!\n\nfix: provide a valid name.It should not contain numbers")
        materialAlertPhoto.setTitle("staff name")
        materialAlertPhoto.setPositiveButton("okay") { dg, _ ->

            //dismiss the dialog

            edtStaffCounty.error = "provide valid name"
            edtStaffCounty.requestFocus()
            dg.dismiss()
            //
        }
        materialAlertPhoto.create()
        materialAlertPhoto.show()
    }

    private fun funInitGlobals() {

        //getting the data of the school code and school name from the shared preference
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        schoolCode = sharedPreferences.getString("code", "").toString().trim()
        schoolName = sharedPreferences.getString("name", "").toString().trim()

        Toast.makeText(this@StaffRegistration, schoolCode, Toast.LENGTH_SHORT).show()

        spinnerRole = findViewById(R.id.spinnerStaffRole)
        editTextRoleStaff = findViewById(R.id.edtStaffRole)
        editTextStaffEnrolKey = findViewById(R.id.edtStaffEnrolKey)
        editTextStaffName = findViewById(R.id.edtStaffFullNameReg)
        edtStaffEmail = findViewById(R.id.edtStafftEmailReg)
        editTextStaffPhone = findViewById(R.id.edtStaffPhonReg)
        edtStaffDOB = findViewById(R.id.edtStaffDOB)
        edtStaffPassword = findViewById(R.id.edtStaffPassword)
        editTextStaffDisability = findViewById(R.id.edtStaffDisability)
        edtStaffCounty = findViewById(R.id.edtStaffCounty)
        appCompatButtonRegisterStaff = findViewById(R.id.btnRegisterStaff)
        appcompatPickStaffPhoto = findViewById(R.id.btnPickStaffPhoto)
        circleImageViewStaff = findViewById(R.id.imgStaffPhoto)
        linearLayoutStaff = findViewById(R.id.nestedStaffDetails)

        //init of calendar to get date data from the the dg picker
        val calendar: Calendar = Calendar.getInstance();
        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH)
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
        //init of date picker dg
        datePickerDialog = DatePickerDialog(this@StaffRegistration, this, year, month, day)


        //spinner
        val adapterSpinnerStaffRoleData = ArrayAdapter.createFromResource(
            this@StaffRegistration,
            R.array.staff_role,
            android.R.layout.simple_list_item_1
        )
        //setting the adapter to the spinner
        spinnerRole.adapter = adapterSpinnerStaffRoleData
        //setting listener to the spinner
        spinnerRole.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //setting the selected value of the spinner to edt role
                val text = p0?.getItemAtPosition(p2).toString()
                editTextRoleStaff.setText(text)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                //nothing selected
            }

        }

    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        val finalMonth = month + 1
        var stringDate = ""
        if (finalMonth > 9) {
            stringDate = "$day/$finalMonth/$year"
        } else {
            stringDate = "$day/0$finalMonth/$year"
        }

        //set the edit text of the DOB to the date of the string
        edtStaffDOB.setText(stringDate)
    }

    private val galleryActivityStaff =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if (it.resultCode == RESULT_OK) {
                //user accepted
                photoUriStaffPhoto = it.data?.data!!
                if (photoUriStaffPhoto == null)
                    return@registerForActivityResult
                else {
                    Glide.with(this@StaffRegistration).load(photoUriStaffPhoto)
                        .into(circleImageViewStaff)
                    circleImageViewStaff.borderColor =
                        getColor(cn.pedant.SweetAlert.R.color.main_green_color)
                }
            } else
                if (it.resultCode == RESULT_CANCELED) {
                    //user cancelled
                    alertStaffPhotoRequired()
                }
        }

    private fun alertStaffPhotoRequired() {
        //code begins
        AlertDialog.Builder(this@StaffRegistration)
            .setCancelable(false)
            .setTitle("Cancelled!")
            .setMessage("staff photo is required and is essential element during the process of data capture")
            .setPositiveButton("ok") { dg, _ ->
                dg.dismiss()
                //return to main profile
                startActivity(Intent(this@StaffRegistration, MainProfile::class.java))
                finish()
                //
            }
            .show()
            .create()
        //code ends
    }

    private fun returnBitmapImage(context: Context, uri: Uri): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            Log.d(TAG, "returnBitmapImage: error:${e.message}")
            Toast.makeText(this@StaffRegistration, "something went wrong!", Toast.LENGTH_SHORT)
                .show()
            null
        }
    }

}