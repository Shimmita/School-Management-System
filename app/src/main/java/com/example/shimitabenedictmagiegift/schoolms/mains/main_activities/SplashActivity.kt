package com.example.shimitabenedictmagiegift.schoolms.mains.main_activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.shimitabenedictmagiegift.schoolms.R
import com.example.shimitabenedictmagiegift.schoolms.mains.main_activities.MainProfile.Companion.SHARED_PREFERENCE_NAME
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    companion object {
        private const val SCHOOLS_COLLECTION = "SCHOOLS"
        private const val TAG = "SplashMain"
         const val COLLECTION_KEYS = "CollectionKeys";
         const val DOCUMENT_KEYS = "keys";
        private const val SCHOOL_KEY = "school_enrol_key";
    }

    //declaration of the globals
    private lateinit var relativeLayoutSplash: RelativeLayout
    private lateinit var textViewBaseLine: TextView
    private lateinit var imageViewMainSplash: ImageView
    private lateinit var sweetAlertDialogProgress: SweetAlertDialog

    //
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        //init full screen
        funFullScreen()
        //init of the globals
        funInitGlobals()
        //load the school regi/login alert in order to start the application
        funSchoolLoginRegister()
    }


    @SuppressLint("InflateParams")
    private fun funSchoolLoginRegister() {

        //load the view in a material alert
        val view: View = layoutInflater.inflate(R.layout.regist_login_school, null, false)
        //extract views from the layout
        val btnLoginSchool = view.findViewById<AppCompatButton>(R.id.btn_login_school)
        val btnRegisterSchool = view.findViewById<AppCompatButton>(R.id.btn_school_register)
        val btnContactDeveloper = view.findViewById<AppCompatButton>(R.id.btnContactDeveloper)
        //inflate it in a alert
        val matAlertSchoolRegLogin = MaterialAlertDialogBuilder(this@SplashActivity)
        matAlertSchoolRegLogin.setTitle("Home Dialog")
        matAlertSchoolRegLogin.setIcon(R.drawable.school_msi_1)
        matAlertSchoolRegLogin.setView(view)
        matAlertSchoolRegLogin.background =
            ResourcesCompat.getDrawable(resources, R.drawable.background_main_profile, theme)
        matAlertSchoolRegLogin.setCancelable(false)
        matAlertSchoolRegLogin.setNegativeButton("exit") { dg, _ ->
            //dismiss the dg to avoid Rt Exceptions
            dg.dismiss()
            //finish app
            finish()
        }

        //delay the display of the alert by the runnable from the imageview
        imageViewMainSplash.postDelayed({
            matAlertSchoolRegLogin.create()
            matAlertSchoolRegLogin.show()
        }, 3200);


        //setting listeners to btn login and register
        btnLoginSchool.setOnClickListener {
            btnLoginSchool.startAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in))
            it.postDelayed({
                //migrate to school login
                funMigrateSchoolLogin();
            }, 320);

        }

        btnRegisterSchool.setOnClickListener {

            btnRegisterSchool.startAnimation(
                AnimationUtils.loadAnimation(
                    this, R.anim.push_left_out
                )
            )
            it.postDelayed({
                //migrate to school Registration
                funMigrateSchoolRegistration()

            }, 320)

        }

        btnContactDeveloper.setOnClickListener {
            //dismiss the material alert
            btnContactDeveloper.startAnimation(
                AnimationUtils.loadAnimation(
                    this, R.anim.push_left_out
                )
            )

            it.postDelayed({
                funShowContactOptions()
            }, 400)
        }


    }

    private fun funShowContactOptions() {
        //code begins
        val materialAlertContactDev = MaterialAlertDialogBuilder(this@SplashActivity)
        materialAlertContactDev.background =
            ResourcesCompat.getDrawable(resources, R.drawable.background_main_profile, theme)
        materialAlertContactDev.setCancelable(false)
        materialAlertContactDev.setTitle("Contact Developer")
        materialAlertContactDev.setMessage(
            "you can reach out to us via the options provided in case of:\n\n" +
                    "i.Technical Support\n\n" +
                    "ii.Account Activation\n\n" +
                    "iii.School Enrolment Key Accreditation"
        )
        materialAlertContactDev.setPositiveButton("email") { dg, _ ->
            funEmailDeveloper()
            dg.dismiss()
        }
        materialAlertContactDev.setNeutralButton("call") { dg, _ ->
            funCallDev()
            dg.dismiss()
        }
        materialAlertContactDev.setNegativeButton("more") { dg, _ ->
            funCreateMoreAlert()
            dg.dismiss()

        }
        materialAlertContactDev.setIcon(R.drawable.school_msi_1)
        materialAlertContactDev.create()
        materialAlertContactDev.show()

        //code ends
    }

    private fun funCreateMoreAlert() {
        val alertDialogBuilderMore = AlertDialog.Builder(this@SplashActivity)
        alertDialogBuilderMore.setCancelable(false)
        alertDialogBuilderMore.setTitle("More")
        alertDialogBuilderMore.setMessage("continue by selecting an option from the options given below.")
        alertDialogBuilderMore.setPositiveButton("SMS") { dg, _ ->
            //call fun sms
            funSMSDev()
            dg.dismiss()
        }
        alertDialogBuilderMore.setNegativeButton("Back") { dg, _ ->
            //return by calling parent alert
            funShowContactOptions()
            dg.dismiss()
        }
        alertDialogBuilderMore.setNeutralButton("End") { dg, _ ->
            //dismiss everything
            dg.dismiss()
        }
        alertDialogBuilderMore.create()
        alertDialogBuilderMore.show()
    }

    @SuppressLint("InflateParams")
    private fun funMigrateSchoolRegistration() {
        val viewSchoolReg = layoutInflater.inflate(R.layout.school_registration, null, false)
        //extract views from the layout
        val editSchoolKey: EditText = viewSchoolReg.findViewById(R.id.edtSchoolEnrolKey) as EditText
        val editSchoolName: EditText = viewSchoolReg.findViewById(R.id.edtSchoolNameReg) as EditText
        val editSchoolCode: EditText = viewSchoolReg.findViewById(R.id.edtSchoolCodeReg) as EditText
        val editSchoolPhone1: EditText =
            viewSchoolReg.findViewById(R.id.edtSchoolPhone1) as EditText
        val editSchoolPhone2: EditText =
            viewSchoolReg.findViewById(R.id.edtSchoolPhone2) as EditText
        val editSchoolEmail: EditText = viewSchoolReg.findViewById(R.id.edtSchoolEmail) as EditText
        val editSchoolPassword: EditText =
            viewSchoolReg.findViewById(R.id.edtSchoolPassword) as EditText


        val materialAlertSchoolReg = MaterialAlertDialogBuilder(this@SplashActivity)
        materialAlertSchoolReg.setIcon(R.drawable.school_msi_1)
        materialAlertSchoolReg.setTitle("School Registration")
        materialAlertSchoolReg.setView(viewSchoolReg)
        materialAlertSchoolReg.setCancelable(false)
        materialAlertSchoolReg.background =
            ResourcesCompat.getDrawable(resources, R.drawable.background_main_profile, theme)
        materialAlertSchoolReg.setPositiveButton("register") { dg, _ ->

            //extract the details from the edtTexts
            val enrolKey = editSchoolKey.text.toString().trim()
            val schoolCode = editSchoolCode.text.toString().trim()
            val schoolName = editSchoolName.text.toString().trim()
            val phone1 = editSchoolPhone1.text.toString().trim()
            val phone2 = editSchoolPhone2.text.toString().trim()
            val schoolEmail = editSchoolEmail.text.toString().trim()
            val schoolPassword = editSchoolPassword.text.toString().trim()

            //pass the params in a fun for evaluation
            funEvaluateDetailsEntered(
                enrolKey,
                schoolCode,
                schoolName,
                phone1,
                phone2,
                dg,
                schoolEmail,
                schoolPassword
            )
            //dismiss the dg
            dg.dismiss()

        }
        materialAlertSchoolReg.setNegativeButton("end") { dg, _ ->

            //dismiss the dg to avoid RT exc
            dg.dismiss()
            //finish the app
            finish()
        }
        materialAlertSchoolReg.create()
        materialAlertSchoolReg.show()

    }

    private fun funEvaluateDetailsEntered(
        enrolKey: String,
        schoolCode: String,
        schoolName: String,
        phone1: String,
        phone2: String,
        dg: DialogInterface,
        schoolEmail: String,
        schoolPassword: String
    ) {

        //check presence of any empty fields
        if (enrolKey.isEmpty() || schoolCode.isEmpty() || schoolName.isEmpty() || phone1.isEmpty() ||
            phone2.isEmpty() || schoolEmail.isEmpty() || schoolPassword.isEmpty()
        ) {
            Toast.makeText(this@SplashActivity, "cannot submit empty fields!", Toast.LENGTH_LONG)
                .show()
            //dismiss the dialog
            dg.dismiss()
        } else if (Regex("\\d").containsMatchIn(schoolName)) {
            Toast.makeText(this@SplashActivity, "school name contains number!", Toast.LENGTH_LONG)
                .show()
            //dismiss the dg
            dg.dismiss()
        } else if (phone1.length > 10 || phone2.length > 10) {
            //length of phone numbers is limited to 10 digits
            Toast.makeText(this@SplashActivity, "please enter 10 digit number!", Toast.LENGTH_LONG)
                .show()
            //dialog dismiss
            dg.dismiss()
        } else if (phone1.length < 10 || phone2.length < 10) {
            Toast.makeText(this@SplashActivity, "please enter 10 digit number!", Toast.LENGTH_LONG)
                .show()
            //dismiss dg
            dg.dismiss()
        } else {

            //show the progressD
            sweetAlertDialogProgress.create()
            sweetAlertDialogProgress.show()

            //begin registering school
            funRegisterSchoolNow(
                schoolCode,
                schoolName,
                phone1,
                phone2,
                enrolKey,
                schoolEmail,
                schoolPassword
            )
            //dismiss the dg
            dg.dismiss()
        }

    }

    private fun funRegisterSchoolNow(
        schoolCode: String,
        schoolName: String,
        phone1: String,
        phone2: String,
        enrolKey: String,
        schoolEmail: String,
        schoolPassword: String
    ) {
        //code begins
        val storeCheckSchoolEnrolKey = FirebaseFirestore.getInstance();
        //check for the match of the school enrollment key with that from the server
        storeCheckSchoolEnrolKey.collection(COLLECTION_KEYS).document(DOCUMENT_KEYS).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    //successfully fetched
                    val valueSchoolKey = it.result[SCHOOL_KEY];
                    //check if value school key is the same as that from the server continue else disagree
                    if (enrolKey == valueSchoolKey) {
                        //continue registering the school the key is real
                        funBeginCreatingNewSchool(
                            schoolEmail,
                            schoolPassword,
                            schoolCode,
                            schoolName,
                            phone1,
                            phone2
                        )
                    } else {
                        //disable progressD and show alert
                        sweetAlertDialogProgress.apply {
                            changeAlertType(SweetAlertDialog.ERROR_TYPE)
                            titleText = "Enroll Key Incorrect!"
                            contentText = ""
                            setConfirmClickListener {
                                it.dismiss()

                            }
                        }
                    }
                } else if (!it.isSuccessful) {
                    sweetAlertDialogProgress.apply {
                        changeAlertType(SweetAlertDialog.ERROR_TYPE)
                        titleText="Registration Failed"
                        contentText=it.exception?.message
                        confirmText="okay"
                        setCancelClickListener {
                            it.dismiss()
                        }
                    }

                }
            }
        //code ends
    }

    private fun funBeginCreatingNewSchool(
        schoolEmail: String,
        schoolPassword: String,
        schoolCode: String,
        schoolName: String,
        phone1: String,
        phone2: String
    ) {

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(schoolEmail, schoolPassword)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    //school been registered now lets continue to post user data in stored
                    sweetAlertDialogProgress.contentText = "saving and securing data"
                    //
                    funSaveSchoolDataStore(schoolEmail, schoolCode, schoolName, phone1, phone2)

                } else if (!it.isSuccessful) {
                    Log.d(TAG, "funBeginCreatingNewSchool: ${it.exception?.message}")

                    sweetAlertDialogProgress.apply {
                        titleText = "Error!"
                        contentText = "${it.exception?.message}"
                        changeAlertType(SweetAlertDialog.ERROR_TYPE)
                        confirmText = "retry"
                        cancelText = "exit!"
                        setConfirmClickListener {
                            //dismiss the dialog due to presence of error
                            dismiss()
                            //
                            funRestartCurrentActivity()

                        }
                        setCancelClickListener {
                            //end the app
                            dismiss()
                            //
                            finish()
                        }
                    }
                }
            }
    }

    private fun funSaveSchoolDataStore(
        schoolEmail: String,
        schoolCode: String,
        schoolName: String,
        phone1: String,
        phone2: String
    ) {
        //document used to identify schools in a collection=schoolCode
        val dateToday = Date()
        val formatDate = SimpleDateFormat("dd/MM/yyyy HH:mm:ss ", Locale("en", "KE"))
        val finalDate = formatDate.format(dateToday)


        val keySchoolCode = "code"
        val keyEmail = "email"
        val keyName = "name"
        val keyPhone1 = "phone1"
        val keyPhone2 = "phone2"
        val keyRegDate = "date"

        val mapData =
            hashMapOf(
                keySchoolCode to schoolCode,
                keyName to schoolName.uppercase(Locale.ROOT),
                keyEmail to schoolEmail,
                keyPhone1 to phone1,
                keyPhone2 to phone2,
                keyRegDate to finalDate
            )

        //path to data school=>(Schools/schoolCode/data)
        val storeSchoolData = FirebaseFirestore.getInstance()
        storeSchoolData.collection(SCHOOLS_COLLECTION).document(schoolCode).set(mapData)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    // TODO: implement an option of downloading pdf details of the registered school

                    sweetAlertDialogProgress.apply {
                        changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                        contentText = "$schoolName registered successfully"
                        confirmText = "Login"
                        titleText = "Congratulations!"
                        cancelText = "exit"

                        setConfirmClickListener {
                            dismiss()
                            //migrate to login alert
                            funMigrateSchoolLogin()
                            //
                        }

                        setCancelClickListener {
                            dismiss()
                            //end app
                            finish()
                        }

                    }

                } else if (!it.isSuccessful) {
                    //error encountered
                    Log.d(TAG, "funSaveSchoolDataStore: Error:${it.exception?.message}")
                    //
                    sweetAlertDialogProgress.apply {
                        changeAlertType(SweetAlertDialog.ERROR_TYPE)
                        titleText = "Failed!"
                        confirmText = "retry"
                        cancelText = "end"
                        contentText = ""

                        setConfirmClickListener {
                            dismiss()
                            //restart the activity
                            funRestartCurrentActivity()
                        }

                        setCancelClickListener {
                            dismiss()
                            //finish app
                            finish()
                        }
                    }
                }
            }

    }

    private fun funMigrateSchoolLogin() {
        //delay 3sec before alerting the user to enter the school name before proceeding to the next activity
        //init of the view and the components attached to it
        val viewEnterSchoolCredentials = LayoutInflater.from(this@SplashActivity)
            .inflate(R.layout.school_login_layout, null, false)
        val editTextSchoolName: EditText =
            viewEnterSchoolCredentials.findViewById(R.id.edtSchoolName) as EditText
        val editTextSchoolCode: EditText =
            viewEnterSchoolCredentials.findViewById(R.id.edtSchoolCode) as EditText


        //init of the alert and entering the view to to the alert
        val materialAlertDialogBuilderSchoolDetails =
            MaterialAlertDialogBuilder(this@SplashActivity)
        materialAlertDialogBuilderSchoolDetails.setTitle("School Details")
        materialAlertDialogBuilderSchoolDetails.setView(viewEnterSchoolCredentials)
        materialAlertDialogBuilderSchoolDetails.setCancelable(false)
        materialAlertDialogBuilderSchoolDetails.setIcon(R.drawable.school_msi_1)
        materialAlertDialogBuilderSchoolDetails.background =
            ResourcesCompat.getDrawable(resources, R.drawable.background_main_profile, theme)
        materialAlertDialogBuilderSchoolDetails.setPositiveButton("proceed") { dialog, _ ->
            //code begins
            val textSchoolName = editTextSchoolName.text.toString()
            val textSchoolCode = editTextSchoolCode.text.toString()
            //trim the data returned
            textSchoolCode.trim()
            textSchoolName.trim()

            //check if name contains a number
            val containsNumber = Regex("\\d").containsMatchIn(textSchoolName)


            //check the legitimacy of the data entered
            if (textSchoolCode.isEmpty() || textSchoolName.isEmpty()) {
                //data empty
                Toast.makeText(
                    this@SplashActivity,
                    "all fields are required!",
                    Toast.LENGTH_SHORT
                )
                    .show()

                //dismiss the dialog
                dialog.dismiss()

            } else if (containsNumber) {
                //official school names should not contain a number
                Toast.makeText(
                    this@SplashActivity,
                    "School Name Contains a Number!",
                    Toast.LENGTH_LONG
                )
                    .show()

                //dismiss the dg
                dialog.dismiss()

            } else {
                //show progress dialog
                sweetAlertDialogProgress.apply {
                    titleText = "LOGIN"
                    contentText = "processing"
                    create()
                    show()
                }

                //fetching the data from the backEnd to figure out the school availability
                val storeCurrentSchool = FirebaseFirestore.getInstance()
                storeCurrentSchool.collection(SCHOOLS_COLLECTION).get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {

                            val dataReturned = it.result.documents
                            var isSchoolCodePresent = false;
                            var schoolNameBackend = "";
                            for (data in dataReturned) {
                                if (data["code"].toString() == textSchoolCode) {
                                    //change bool value since school code is found
                                    isSchoolCodePresent = true
                                    schoolNameBackend = data["name"].toString()
                                }

                            }

                            //check to ensure if true the value of the school was found and name present
                            if (isSchoolCodePresent && schoolNameBackend.isNotEmpty()) {
                                //login was a success
                                sweetAlertDialogProgress.apply {
                                    changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                                    setCancelClickListener(null)
                                    titleText = "Successful"
                                    contentText = "school found"
                                    confirmText = "proceed"

                                    //fetching the value of the school name and save it in a variable

                                    setConfirmClickListener {
                                        //school exists is registered

                                        //put the school code and the school name in a shared preference
                                        val sharedPreferences=getSharedPreferences(SHARED_PREFERENCE_NAME,Context.MODE_PRIVATE)
                                        sharedPreferences.edit().putString("code",textSchoolCode).putString("name",schoolNameBackend).apply()
                                        //
                                        val intent =
                                            Intent(this@SplashActivity, MainProfile::class.java)
                                        startActivity(intent)
                                        finish()

                                        //dismiss alert
                                        dismiss()

                                    }

                                }


                                //
                            } else {
                                sweetAlertDialogProgress.apply {
                                    changeAlertType(SweetAlertDialog.WARNING_TYPE)
                                    contentText = "not found"
                                    titleText = "School Not Registered!"
                                    confirmText = "retry"
                                    cancelText = "register"

                                    setConfirmClickListener {
                                        dismiss()
                                        //migrate to school login
                                        funMigrateSchoolLogin()
                                    }
                                    setCancelClickListener {
                                        //dismiss
                                        dismiss()
                                        //migrate to registration
                                        funMigrateSchoolRegistration()
                                        //
                                    }
                                }
                            }

                        } else if (!it.isSuccessful) {
                            Log.d(TAG, "funMigrateSchoolLogin: ${it.exception?.message}")
                            sweetAlertDialogProgress.apply {
                                changeAlertType(SweetAlertDialog.WARNING_TYPE)
                                titleText = "ERROR!"
                                contentText = "${it.exception?.message}"

                                confirmText = "retry"
                                cancelText = "exit"

                                setConfirmClickListener {
                                    //migrate to login alert
                                    funMigrateSchoolLogin()
                                    dismiss()
                                }

                                setCancelClickListener {
                                    //finish app
                                    dismiss()
                                    finish()
                                }
                            }
                        }
                    }
            }

            //code ends
        }
        materialAlertDialogBuilderSchoolDetails.setNegativeButton("exit") { dialog, _ ->

            //alert user of being exited
            AlertDialog.Builder(this@SplashActivity)
                .setTitle("application exit")
                .setMessage("sure exit application")
                .setPositiveButton("yes") { dg, _ ->

                    //dismiss the alert
                    dg.dismiss()
                    //finish the application
                    finish()
                }
                .setNegativeButton("no") { dg, _ ->

                    //dismiss the alert and then re-back to the main alerter
                    dg.dismiss()
                    //recall the main function again
                    funRestartCurrentActivity()
                    //
                }
                .setCancelable(false)
                .create()
                .show()
            //dismiss the parent dg
            dialog.dismiss()
            //
        }
        //show the alert
        materialAlertDialogBuilderSchoolDetails.create()
        materialAlertDialogBuilderSchoolDetails.show()

    }

    private fun funRestartCurrentActivity() {
        //code begins
        val intentRestartActivity = Intent(this@SplashActivity, SplashActivity::class.java)
        startActivity(intentRestartActivity)
        //code ends
    }

    private fun funInitGlobals() {
        //code begins
        relativeLayoutSplash = findViewById(R.id.relativeSplashMain)
        textViewBaseLine = findViewById(R.id.tvBaselineTextSplash)
        imageViewMainSplash = findViewById(R.id.imgLogo)

        //init of progressD that will be available only when the button submit are clicked
        sweetAlertDialogProgress =
            SweetAlertDialog(this@SplashActivity, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialogProgress.titleText = "Processing"
        sweetAlertDialogProgress.contentText = "registering"
        sweetAlertDialogProgress.setCancelable(false)


        //

        //anim layout on the views
        val layoutSplash = LayoutAnimationController(
            AnimationUtils.loadAnimation(
                this@SplashActivity,
                R.anim.push_left_in
            )
        )
        layoutSplash.apply {
            order = LayoutAnimationController.ORDER_REVERSE
            delay = 0.5f
            relativeLayoutSplash.layoutAnimation = layoutSplash
            relativeLayoutSplash.startLayoutAnimation()
        }

        //delay 1.8 secs and set the status bar visible to the window of the screen since it's hidden by the fun
        //load full screen
        relativeLayoutSplash.postDelayed({

            //check if the status bar is hidden and visible  it
            val isShownStatusBar: Boolean? = supportActionBar?.isShowing
            if (isShownStatusBar != null) {
                if (!isShownStatusBar) {
                    //show the status bar since it is hidden
                    supportActionBar?.show()
                }
                //status bar is shown
            }
        }, 2500)
        //code ends
    }

    @Suppress("DEPRECATION")
    private fun funFullScreen() {
        //code begins
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //android system is >=version 10
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            //android system is less than version 10
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            //
        }

        //set the title of the status bar to
        this.title = getString(R.string.title)
        //hide the status bar and that let it be displayed when the anim are done
        supportActionBar?.hide()
        //code ends
    }

    private fun funEmailDeveloper() {
        //code
        val emailsMyEmails = arrayOf("douglasshimita3@gmail.com", "shimitadouglas@gmail.com","benedictmaundu2000@gmail.com")
        val emailSubject = "write email subject here"
        val messageBodyText = "Dear user, write your message here"
        val intentEmail = Intent()
        intentEmail.action = Intent.ACTION_SEND
        intentEmail.setDataAndType(Uri.parse("email"), "message/rfc822")
        intentEmail.putExtra(Intent.EXTRA_EMAIL, emailsMyEmails)
        intentEmail.putExtra(Intent.EXTRA_SUBJECT, emailSubject)
        intentEmail.putExtra(Intent.EXTRA_TEXT, messageBodyText)
        startActivity(Intent.createChooser(intentEmail, "Launch Email"))
        //code ends

    }

    private fun funSMSDev() {
        //code begins
        val phoneNumber = "+254757450727"
        val messageBody =
            "hey,write your text here and send it to me, i will be glad to feedback you"
        val intentMessaging = Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null))
        startActivity(Intent.createChooser(intentMessaging, "Launch SMS APP"))
        //code ends

    }

    private fun funCallDev() {
        //code begins
        //start an intent to the phone call
        val numberIntent = Intent()
        numberIntent.action = Intent.ACTION_DIAL
        numberIntent.data = Uri.parse("tel:+254757450727")
        startActivity(numberIntent)
        //code ends

    }

    // Hash a password using SHA-256
    private fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val hash = md.digest(password.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(hash, Base64.DEFAULT)
    }

    // Verify a password by comparing its hash with a stored hash
    fun verifyPassword(password: String, storedHash: String): Boolean {
        val hash = hashPassword(password)
        return hash == storedHash
    }


}