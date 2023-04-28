package com.example.shimitabenedictmagiegift.schoolms.mains.main_activities

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.shimitabenedictmagiegift.schoolms.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HandleLogin : AppCompatActivity() {
    companion object {
        private const val TAG = "HandleLogin"
    }

    //declaration of Globals
    lateinit var loginUI: View
    lateinit var layoutRoleLogin: LinearLayout
    lateinit var layoutParentLogin: LinearLayout
    lateinit var layoutStudentLogin: LinearLayout
    lateinit var layoutStaffLogin: LinearLayout
    lateinit var editTextEnrolKeyStaff: EditText
    lateinit var editTextStaffEmail: EditText
    lateinit var editTextStaffPassword: EditText
    lateinit var edtStudentAdmissionParent: EditText
    lateinit var edtParentEmail: EditText
    lateinit var edtParentPassword: EditText
    lateinit var editStudentAdmissionStudent: EditText
    lateinit var editStudentPassword: EditText
    lateinit var edtStudentEmail: EditText

    //
    lateinit var stringRole: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_handle_login)

        this.title="User Login "

        //code begins
        funInitGlobals()
        //fun decide base on the role
        if (stringRole.isEmpty()) {
            //fun go home
            funBackHome()
        } else if (stringRole.isNotEmpty()) {
            //fun decide
            funDecideDisplay(stringRole)
            Log.d(TAG, "onCreate: data=>${stringRole}")
        }
        //code ends
    }

    private fun funDecideDisplay(stringRole: String) {
        if (stringRole.contains("student", true)) {
            //student login
            funAlertShowStudentUI()

        } else if (stringRole.contains("staff", true)) {
            //staff login alert
            //fun initiate staff login
            funAlertShowStaffUI()
            //

        } else if (stringRole.contains("parent", true)) {
            //parent login

            funAlertShowParentView()
        }
    }

    private fun funBackHome() {

        //no data go home main profile
        val intentHome = Intent(this@HandleLogin, MainProfile::class.java)
        startActivity(intentHome)
        finish()
    }

    @SuppressLint("InflateParams")
    private fun funInitGlobals() {

        //inflating the layout containing the views used for login
        loginUI = layoutInflater.inflate(R.layout.login_ui, null, false)

        //containers
        layoutRoleLogin = loginUI.findViewById(R.id.linearLoginRole)

        layoutParentLogin = loginUI.findViewById(R.id.linearParentLogin)
        layoutStudentLogin = loginUI.findViewById(R.id.linearStudentLogin)
        layoutStaffLogin = loginUI.findViewById(R.id.linearStaffLogin)
        //

        //staff edt
        editTextEnrolKeyStaff = loginUI.findViewById(R.id.edtStaffEnrollKeyLogin)
        editTextStaffEmail = loginUI.findViewById(R.id.edtStaffEmailLogin)
        editTextStaffPassword = loginUI.findViewById(R.id.edtStaffPasswordLogin)
        //

        //parent edt
        edtStudentAdmissionParent = loginUI.findViewById(R.id.edtStudentAdmissionLoginParent)
        edtParentEmail = loginUI.findViewById(R.id.edtParentEmailLogin)
        edtParentPassword = loginUI.findViewById(R.id.edtParentPasswordLogin)
        //

        //student edt
        editStudentAdmissionStudent = loginUI.findViewById(R.id.edtStudentAdmissionLoginStudent)
        editStudentPassword = loginUI.findViewById(R.id.edtStudentPasswordLogin)
        edtStudentEmail = loginUI.findViewById(R.id.edtStudentEmailLogin)
        //

        //get the data from the intent and obtain the role
        stringRole = intent.getStringExtra("role").toString()
    }


    private fun funAlertShowStaffUI(
    ) {
        //code begins

        //disable other views
        layoutParentLogin.visibility = View.GONE
        layoutRoleLogin.visibility = View.GONE
        layoutStudentLogin.visibility = View.GONE

        layoutStaffLogin.visibility = View.VISIBLE

        val materialAlertStaffUI = MaterialAlertDialogBuilder(this@HandleLogin)
        materialAlertStaffUI.setTitle("Staff Login")
        materialAlertStaffUI.setIcon(R.drawable.login_icon_2)
        materialAlertStaffUI.setCancelable(false)
        materialAlertStaffUI.setIcon(R.drawable.login_icon_2)
        materialAlertStaffUI.setView(loginUI)
        materialAlertStaffUI.background = ResourcesCompat.getDrawable(
            this@HandleLogin.resources,
            R.drawable.background_main_profile,
            this@HandleLogin.theme
        )
        materialAlertStaffUI.setPositiveButton("login") { dg, _ ->
            //code begins
            val textStaffEnrolKey = editTextEnrolKeyStaff.text.toString().trim()
            val textStaffEmail = editTextStaffEmail.text.toString().trim()
            val textStaffPassword = editTextStaffPassword.text.toString().trim()
            if (textStaffEmail.isEmpty() || textStaffEnrolKey.isEmpty() || textStaffPassword.isEmpty()) {
                Toast.makeText(this@HandleLogin, "cannot submit empty fields", Toast.LENGTH_SHORT)
                    .show()
                //return home main profile
                funBackHome()

            } else {
                //call fun to proceed login to the student dash
                funProceedStaffLogin(textStaffPassword, textStaffEmail, textStaffEnrolKey, dg)
            }
            //code ends
        }
        materialAlertStaffUI.setNegativeButton("return") { dg, _ ->

            //return home
            funBackHome()

            //code
            dg.dismiss()
            //code
        }
        materialAlertStaffUI.create()
        materialAlertStaffUI.show()


        //code ends
    }

    private fun funProceedStaffLogin(
        textStaffPassword: String,
        textStaffEmail: String,
        textStaffEnrolKey: String,
        dg: DialogInterface?
    ) {
        //code


        //code
    }

    private fun funAlertShowStudentUI() {
        //code begins
        //disable other views containers except student
        layoutStudentLogin.visibility = View.VISIBLE
        layoutRoleLogin.visibility = View.GONE
        layoutStaffLogin.visibility = View.GONE
        layoutParentLogin.visibility = View.GONE

        val materialAlertStudentUI = MaterialAlertDialogBuilder(this@HandleLogin)
        materialAlertStudentUI.setTitle("Student Login")
        materialAlertStudentUI.setCancelable(false)
        materialAlertStudentUI.setIcon(R.drawable.login_icon_2)
        materialAlertStudentUI.setView(loginUI)
        materialAlertStudentUI.background = ResourcesCompat.getDrawable(
            this@HandleLogin.resources,
            R.drawable.background_main_profile,
            this@HandleLogin.theme
        )
        materialAlertStudentUI.setPositiveButton("login") { dg, _ ->
            //code begins
            val textStudentEmail = edtStudentEmail.text.toString().trim()
            val textStudentPassword = editStudentPassword.text.toString().trim()
            val textStudentAdmission = editStudentAdmissionStudent.text.toString().trim()
            if (textStudentEmail.isEmpty() || textStudentAdmission.isEmpty() || textStudentPassword.isEmpty()) {
                Toast.makeText(this@HandleLogin, "cannot submit empty fields", Toast.LENGTH_SHORT)
                    .show()

                dg.dismiss()
                //call intent home
                funIntentMainProfile()
                //

            } else {
                //call fun to proceed login to the student dash
                funProceedStudentLogin(
                    textStudentEmail,
                    textStudentPassword,
                    textStudentAdmission,
                    dg
                )
            }
            //code ends
        }
        materialAlertStudentUI.setNegativeButton("return") { dg, _ ->
            //back to main profile
            funBackHome()
            //
            dg.dismiss()
        }

        //remove view
        materialAlertStudentUI.create()
        materialAlertStudentUI.show()


        //code ends
    }

    private fun funIntentMainProfile() {
        startActivity(Intent(this@HandleLogin, MainProfile::class.java))
        finish()
    }

    private fun funProceedStudentLogin(
        textStudentEmail: String,
        textStudentPassword: String,
        textStudentAdmission: String,
        dg: DialogInterface
    ) {
        //code begins


        //code ends
    }

    private fun funAlertShowParentView(

    ) {

       layoutParentLogin.visibility = View.VISIBLE
        layoutRoleLogin.visibility = View.GONE
        layoutStaffLogin.visibility = View.GONE
        layoutStudentLogin.visibility = View.GONE

        //code begins
        val materialAlertParentUI = MaterialAlertDialogBuilder(this@HandleLogin)
        materialAlertParentUI.setTitle("parent login")
        materialAlertParentUI.setCancelable(false)
        materialAlertParentUI.setIcon(R.drawable.login_icon_2)
        materialAlertParentUI.setView(loginUI)
        materialAlertParentUI.background = ResourcesCompat.getDrawable(
            this@HandleLogin.resources,
            R.drawable.background_main_profile,
            this@HandleLogin.theme
        )
        materialAlertParentUI.setPositiveButton("login") { dg, _ ->
            //code begins
            val textStudentAdmission = edtStudentAdmissionParent.text.toString().trim()
            val textParentEmail = edtParentEmail.text.toString().trim()
            val textParentPassword = edtParentPassword.text.toString().trim()
            if (textParentEmail.isEmpty() || textStudentAdmission.isEmpty() || textParentPassword.isEmpty()) {
                Toast.makeText(this@HandleLogin, "cannot submit empty fields", Toast.LENGTH_SHORT)
                    .show()
                dg.dismiss()

                //return home
                funBackHome()

            } else {
                //call fun to proceed login to the student dash
                funProceedParentLogin(textParentPassword, textParentEmail, textStudentAdmission)
                dg.dismiss()
            }
            //code ends
        }
        materialAlertParentUI.setNegativeButton("return") { dg, _ ->
            funBackHome()
            dg.dismiss()
        }
        materialAlertParentUI.create()
        materialAlertParentUI.show()

        //code ends
    }

    private fun funProceedParentLogin(
        textParentPassword: String,
        textParentEmail: String,
        textStudentAdmission: String,
    ) {

        //code


        //code
    }
}