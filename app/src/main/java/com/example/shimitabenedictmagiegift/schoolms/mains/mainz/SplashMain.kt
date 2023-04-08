package com.example.shimitabenedictmagiegift.schoolms.mains.mainz

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.shimitabenedictmagiegift.schoolms.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SplashMain : AppCompatActivity() {
    //declaration of the globals
    lateinit var relativeLayoutSplash: RelativeLayout
    lateinit var textViewBaseLine: TextView
    lateinit var imageViewMainSplash: ImageView

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
        val btnLoginSchool = view.findViewById<Button>(R.id.btn_login_school)
        val btnRegisterSchool = view.findViewById<Button>(R.id.btn_school_register)
        //inflate it in a alert
        val matAlertSchoolRegLogin = MaterialAlertDialogBuilder(this@SplashMain)
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
            }, 500);

        }

        btnRegisterSchool.setOnClickListener {
            btnRegisterSchool.startAnimation(
                AnimationUtils.loadAnimation(
                    this,
                    R.anim.push_left_out
                )
            )
            it.postDelayed({
                //migrate to school Registration
                funMigrateSchoolRegistration()

            }, 500)
        }


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

        val materialAlertSchoolReg = MaterialAlertDialogBuilder(this@SplashMain)
        materialAlertSchoolReg.setIcon(R.drawable.school_msi_1)
        materialAlertSchoolReg.setTitle("School Registration")
        materialAlertSchoolReg.setView(viewSchoolReg)
        materialAlertSchoolReg.setCancelable(false)
        materialAlertSchoolReg.background =
            ResourcesCompat.getDrawable(resources, R.drawable.background_main_profile, theme)
        materialAlertSchoolReg.setPositiveButton("register") { dg, _ ->
            //extract the details from the edtTexts
            val enrolKey=editSchoolKey.text.toString().trim()
            val schoolCode=editSchoolCode.text.toString().trim()
            val schoolName = editSchoolName.text.toString().trim()
            val phone1=editSchoolPhone1.text.toString().trim()
            val phone2=editSchoolPhone2.text.toString().trim()

            //pass the params in a fun for evaluation
            funEvaluateDetailsEntered(enrolKey,schoolCode,schoolName,phone1,phone2,dg)
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
        dg: DialogInterface
    ) {

        //check presence of any empty fields
        if (enrolKey.isEmpty()||schoolCode.isEmpty()||schoolName.isEmpty()||phone1.isEmpty()||phone2.isEmpty())
        {
            Toast.makeText(this@SplashMain,"cannot submit empty fields!",Toast.LENGTH_LONG).show()
            //dismiss the dialog
            dg.dismiss()
        }
        else if (Regex("\\d").containsMatchIn(schoolName))
        {
            Toast.makeText(this@SplashMain,"school name contains number!",Toast.LENGTH_LONG).show()
            //dismiss the dg
            dg.dismiss()
        }
        else if (phone1.length>10||phone2.length>10)
        {
            //length of phone numbers is limited to 10 digits
            Toast.makeText(this@SplashMain,"please enter 10 digit number!",Toast.LENGTH_LONG).show()
            //dialog dismiss
            dg.dismiss()
        }
        else if (phone1.length<10 ||phone2.length<10)
        {
            Toast.makeText(this@SplashMain,"please enter 10 digit number!",Toast.LENGTH_LONG).show()
        }
        else
        {
        }

    }

    private fun funMigrateSchoolLogin() {
        //delay 3sec before alerting the user to enter the school name before proceeding to the next activity
        //init of the view and the components attached to it
        val viewEnterSchoolCredentials = LayoutInflater.from(this@SplashMain)
            .inflate(R.layout.school_login_layout, null, false)
        val editTextSchoolName: EditText =
            viewEnterSchoolCredentials.findViewById(R.id.edtSchoolName) as EditText
        val editTextSchoolCode: EditText =
            viewEnterSchoolCredentials.findViewById(R.id.edtSchoolCode) as EditText


        //init of the alert and entering the view to to the alert
        val materialAlertDialogBuilderSchoolDetails = MaterialAlertDialogBuilder(this@SplashMain)
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
                Toast.makeText(this@SplashMain, "cannot submit empty fields!", Toast.LENGTH_SHORT)
                    .show()

                //dismiss the dialog
                dialog.dismiss()

            }
            else if (containsNumber)
            {
                //official school names should not contain a number
                Toast.makeText(this@SplashMain, "School Name Contains a Number!", Toast.LENGTH_LONG)
                    .show()

                //dismiss the dg
                dialog.dismiss()

            }

            else {
                Toast.makeText(this@SplashMain, textSchoolCode, Toast.LENGTH_LONG).show()
                //dismiss the dialog
                dialog.dismiss()
                //data is no empty proceed
                //todo:implement backend checking here to find out if the school is in the database
                //intent migrate to the main profile activity
                val intent = Intent(this@SplashMain, MainProfile::class.java)
                intent.putExtra("school_name", textSchoolName)
                intent.putExtra("school_code", textSchoolCode)
                startActivity(intent)
                finishAffinity()
            }

            //code ends
        }
        materialAlertDialogBuilderSchoolDetails.setNegativeButton("exit") { dialog, _ ->

            //alert user of being exited
            AlertDialog.Builder(this@SplashMain)
                .setMessage("exit application ?")
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
        val intentRestartActivity = Intent(this@SplashMain, SplashMain::class.java)
        startActivity(intentRestartActivity)
        //code ends
    }

    private fun funInitGlobals() {
        //code begins
        relativeLayoutSplash = findViewById(R.id.relativeSplashMain)
        textViewBaseLine = findViewById(R.id.tvBaselineTextSplash)
        imageViewMainSplash = findViewById(R.id.imgLogo)
        //

        //anim layout on the views
        val layoutSplash = LayoutAnimationController(
            AnimationUtils.loadAnimation(
                this@SplashMain,
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
        //
        //code ends
    }
}