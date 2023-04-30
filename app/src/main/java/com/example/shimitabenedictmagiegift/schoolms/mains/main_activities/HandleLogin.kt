package com.example.shimitabenedictmagiegift.schoolms.mains.main_activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.shimitabenedictmagiegift.schoolms.R
import com.example.shimitabenedictmagiegift.schoolms.mains.dash.*
import com.example.shimitabenedictmagiegift.schoolms.mains.main_activities.SplashActivity.Companion.COLLECTION_KEYS
import com.example.shimitabenedictmagiegift.schoolms.mains.main_activities.SplashActivity.Companion.DOCUMENT_KEYS
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HandleLogin : AppCompatActivity() {
    companion object {
        private const val TAG = "HandleLogin"
    }

    //declaration of Globals
    lateinit var loginUI: View
    private lateinit var layoutRoleLogin: LinearLayout
    lateinit var layoutParentLogin: LinearLayout
    lateinit var layoutStudentLogin: LinearLayout
    lateinit var layoutStaffLogin: LinearLayout
    lateinit var editTextEnrolKeyStaff: EditText
    lateinit var editTextStaffEmail: EditText
    lateinit var editTextStaffPassword: EditText
    lateinit var edtParentEmail: EditText
    lateinit var edtParentPassword: EditText
    lateinit var editStudentPassword: EditText
    lateinit var edtStudentEmail: EditText

    //
    lateinit var stringRole: String
    lateinit var sweetAlertDialogProgress: SweetAlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_handle_login)

        //code begins
        funInitGlobals()

        funInitOther()
        //code ends
    }

    private fun funInitOther() {
        //fun decide base on the role
        if (stringRole.isEmpty()) {
            //fun go home
            funBackHome()
        } else if (stringRole.isNotEmpty()) {
            //fun decide
            funDecideDisplay(stringRole)
            Log.d(TAG, "onCreate: data=>${stringRole}")
        }
    }

    private fun funDecideDisplay(stringRole: String) {
        if (stringRole.contains("student", true)) {
            //student login
            funAlertShowStudentUI()

        } else if (stringRole.contains("administration", true)) {
            //fun initiate staff login
            funAlertShowAdministrationStaffUI()
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

        this.title = "LOGIN"

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
        edtParentEmail = loginUI.findViewById(R.id.edtParentEmailLogin)
        edtParentPassword = loginUI.findViewById(R.id.edtParentPasswordLogin)
        //

        //student edt
        editStudentPassword = loginUI.findViewById(R.id.edtStudentPasswordLogin)
        edtStudentEmail = loginUI.findViewById(R.id.edtStudentEmailLogin)
        //

        //get the data from the intent and obtain the role
        stringRole = intent.getStringExtra("role").toString()
        //
        sweetAlertDialogProgress =
            SweetAlertDialog(this@HandleLogin, SweetAlertDialog.PROGRESS_TYPE)
        sweetAlertDialogProgress.titleText = "processing"
        sweetAlertDialogProgress.setCancelable(false)
    }


    @SuppressLint("InflateParams")
    private fun funAlertShowAdministrationStaffUI(
    ) {
        //code begins
        layoutParentLogin.visibility = View.GONE
        layoutRoleLogin.visibility = View.GONE
        layoutStudentLogin.visibility = View.GONE
        layoutStaffLogin.visibility = View.VISIBLE

        val viewWhichAdmin: View =
            layoutInflater.inflate(R.layout.administration_login_as, null, false)
        val editTextSelectionAdminRole: EditText = viewWhichAdmin.findViewById(R.id.edtAdminRole)
        val materialAlertAdminRole = MaterialAlertDialogBuilder(this@HandleLogin)
        materialAlertAdminRole.background =
            ResourcesCompat.getDrawable(resources, R.drawable.background_main_profile, theme)
        materialAlertAdminRole.setCancelable(false)
        materialAlertAdminRole.setView(viewWhichAdmin)
        materialAlertAdminRole.setTitle("Administration Role")
        materialAlertAdminRole.setPositiveButton("proceed") { dg, _ ->

            val textDataAdminRole = editTextSelectionAdminRole.text.toString().trim()
            if (textDataAdminRole.contains("principal", true)) {
                funProceedLoginPrincipal(textDataAdminRole, materialAlertAdminRole)
            } else if (textDataAdminRole.contains("deputy", true)) {
                funProceedLoginDeputy(textDataAdminRole, materialAlertAdminRole)
            } else if (textDataAdminRole.contains("teacher", true)) {
                funProceedLoginTeacher(textDataAdminRole, materialAlertAdminRole)
            } else if (textDataAdminRole.contains("bursa", true)) {
                funProceedLoginBursa(textDataAdminRole, materialAlertAdminRole)
            }

            dg.dismiss()

        }
        materialAlertAdminRole.setNegativeButton("cancel") { dialog, _ ->
            dialog.dismiss()
            funReturnHome()

        }
        materialAlertAdminRole.create()
        materialAlertAdminRole.show()


        val spinner: Spinner = viewWhichAdmin.findViewById(R.id.spinnerAdminRole)
        val adapter = ArrayAdapter.createFromResource(
            this@HandleLogin,
            R.array.staff_role,
            android.R.layout.simple_list_item_1
        )
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val textOption = p0?.getItemAtPosition(p2).toString()
                editTextSelectionAdminRole.setText(textOption)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                //nothing
            }

        }

        //code ends
    }

    private fun funProceedLoginBursa(
        textDataAdminRole: String,
        materialAlertAdminRole: MaterialAlertDialogBuilder
    ) {

        materialAlertAdminRole.setTitle("Administration ($textDataAdminRole)")
        materialAlertAdminRole.setIcon(R.drawable.login_icon_2)
        materialAlertAdminRole.setCancelable(false)
        materialAlertAdminRole.setIcon(R.drawable.login_icon_2)
        materialAlertAdminRole.setView(loginUI)
        materialAlertAdminRole.background = ResourcesCompat.getDrawable(
            this@HandleLogin.resources,
            R.drawable.background_main_profile,
            this@HandleLogin.theme
        )
        materialAlertAdminRole.setPositiveButton("login") { dg, _ ->
            //code begins
            val textAdminEnrolKey = editTextEnrolKeyStaff.text.toString().trim()
            val textAdminEmail = editTextStaffEmail.text.toString().trim()
            val textAdminPassword = editTextStaffPassword.text.toString().trim()
            if (textAdminEmail.isEmpty() || textAdminEnrolKey.isEmpty() || textAdminPassword.isEmpty()) {
                Toast.makeText(this@HandleLogin, "cannot submit empty fields!", Toast.LENGTH_SHORT)
                    .show()
                //return home main profile
                funBackHome()

            } else {
                //call fun to proceed login to the student dash
                funCheckAdminKeyBursa(
                    textAdminPassword,
                    textAdminEmail,
                    textAdminEnrolKey,
                )
            }
            dg.dismiss()
            //code ends
        }
        materialAlertAdminRole.setNegativeButton("return") { dg, _ ->

            //return home
            funBackHome()

            //code
            dg.dismiss()
            //code
        }
        materialAlertAdminRole.create()
        materialAlertAdminRole.show()


    }

    private fun funCheckAdminKeyBursa(
        textAdminPassword: String,
        textAdminEmail: String,
        textAdminEnrolKey: String,
    ) {
        //show dg
        sweetAlertDialogProgress.create()
        sweetAlertDialogProgress.show()

        val storeKeys = FirebaseFirestore.getInstance()
        storeKeys.collection(COLLECTION_KEYS).document(DOCUMENT_KEYS).get().addOnCompleteListener {
            if (it.isSuccessful) {
                sweetAlertDialogProgress.titleText = "Validating"
                //compare the keys if they match @admin =(principal,bursa,deputy)
                val keyServer = it.result["admin_key"]
                if (keyServer.toString() == textAdminEnrolKey) {
                    //key found and true proceed
                    funProceedLoginBursaNow(textAdminEmail, textAdminPassword)
                    //
                } else {
                    //wrong key inputted warn/alert
                    sweetAlertDialogProgress.apply {
                        changeAlertType(SweetAlertDialog.ERROR_TYPE)
                        titleText = "Wrong Key"
                        contentText = "enter valid key"
                        confirmText = "Okay"
                        cancelText = "cancel"

                        setConfirmClickListener {
                            dismissWithAnimation()
                        }
                        setCancelClickListener {
                            dismiss()
                            funReturnHome()
                        }
                    }
                }
            } else if (!it.isSuccessful) {
                //something  went wrong!
                sweetAlertDialogProgress.apply {
                    changeAlertType(SweetAlertDialog.ERROR_TYPE)
                    contentText = it.exception?.message
                    titleText = "Failed"
                    confirmText = "retry"
                    cancelText = "home"

                    setConfirmClickListener {
                        dismiss()
                    }
                    setCancelClickListener {
                        dismiss()
                        funReturnHome()
                    }
                }
            }
        }
    }

    private fun funProceedLoginBursaNow(textAdminEmail: String, textAdminPassword: String) {
        //login user
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInWithEmailAndPassword(textAdminEmail, textAdminPassword)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    //login successful
                    sweetAlertDialogProgress.apply {
                        changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                        titleText = "Login Successful"
                        confirmText = "open"
                        setConfirmClickListener {
                            //intent migration to bursa dashBoard
                            startActivity(Intent(this@HandleLogin, BursaDash::class.java))
                            finish()
                            dismiss()
                        }
                    }
                } else if (!it.isSuccessful) {
                    //login error possibly email or password wrong
                    sweetAlertDialogProgress.apply {
                        changeAlertType(SweetAlertDialog.ERROR_TYPE)
                        contentText = it.exception?.message
                        titleText = "Login Failed"
                        confirmText = "retry"
                        cancelText = "cancel"

                        setConfirmClickListener {
                            dismiss()
                        }
                        setCancelClickListener {
                            dismiss()
                            funReturnHome()
                        }
                    }
                }
            }
    }

    private fun funReturnHome() {
        startActivity(Intent(this@HandleLogin, MainProfile::class.java))
        finish()
    }

    private fun funProceedLoginTeacher(
        textDataAdminRole: String,
        materialAlertAdminRole: MaterialAlertDialogBuilder
    ) {

        materialAlertAdminRole.setTitle("Administration ($textDataAdminRole)")
        materialAlertAdminRole.setIcon(R.drawable.login_icon_2)
        materialAlertAdminRole.setCancelable(false)
        materialAlertAdminRole.setIcon(R.drawable.login_icon_2)
        materialAlertAdminRole.setView(loginUI)
        materialAlertAdminRole.background = ResourcesCompat.getDrawable(
            this@HandleLogin.resources,
            R.drawable.background_main_profile,
            this@HandleLogin.theme
        )
        materialAlertAdminRole.setPositiveButton("login") { dg, _ ->
            //code begins
            val textTeacherEnrolKey = editTextEnrolKeyStaff.text.toString().trim()
            val textTeacherEmail = editTextStaffEmail.text.toString().trim()
            val textTeacherPassword = editTextStaffPassword.text.toString().trim()
            if (textTeacherEmail.isEmpty() || textTeacherEnrolKey.isEmpty() || textTeacherPassword.isEmpty()) {
                Toast.makeText(this@HandleLogin, "cannot submit empty fields!", Toast.LENGTH_SHORT)
                    .show()
                //return home main profile
                funBackHome()

            } else {
                //call fun to proceed login to the student dash
                funCheckAdminKeyTeacher(
                    textTeacherPassword,
                    textTeacherEmail,
                    textTeacherEnrolKey
                )


            }
            dg.dismiss()
            //code ends
        }
        materialAlertAdminRole.setNegativeButton("return") { dg, _ ->

            //return home
            funBackHome()

            //code
            dg.dismiss()
            //code
        }
        materialAlertAdminRole.create()
        materialAlertAdminRole.show()


    }

    private fun funCheckAdminKeyTeacher(
        textTeacherPassword: String,
        textTeacherEmail: String,
        textTeacherEnrolKey: String
    ) {

        //show dg
        sweetAlertDialogProgress.create()
        sweetAlertDialogProgress.show()

        val storeKeys = FirebaseFirestore.getInstance()
        storeKeys.collection(COLLECTION_KEYS).document(DOCUMENT_KEYS).get().addOnCompleteListener {
            if (it.isSuccessful) {
                sweetAlertDialogProgress.titleText = "Validating"
                //compare the keys if they match @admin =(principal,bursa,deputy)
                val keyServer = it.result["teacher_key"]
                if (keyServer.toString() == textTeacherEnrolKey) {
                    //key found and true proceed
                    funProceedLoginTeacherNow(textTeacherEmail, textTeacherPassword)
                    //
                } else {
                    //wrong key inputted warn/alert
                    sweetAlertDialogProgress.apply {
                        changeAlertType(SweetAlertDialog.ERROR_TYPE)
                        titleText = "Wrong Key"
                        contentText = "enter valid key"
                        confirmText = "Okay"
                        cancelText = "cancel"

                        setConfirmClickListener {
                            dismissWithAnimation()
                        }
                        setCancelClickListener {
                            dismiss()
                            funReturnHome()
                        }
                    }
                }
            } else if (!it.isSuccessful) {
                //something  went wrong!
                sweetAlertDialogProgress.apply {
                    changeAlertType(SweetAlertDialog.ERROR_TYPE)
                    contentText = it.exception?.message
                    titleText = "Failed"
                    confirmText = "retry"
                    cancelText = "home"

                    setConfirmClickListener {
                        dismiss()
                    }
                    setCancelClickListener {
                        dismiss()
                        funReturnHome()
                    }
                }
            }
        }
    }

    private fun funProceedLoginTeacherNow(textTeacherEmail: String, textTeacherPassword: String) {

        //login user
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInWithEmailAndPassword(textTeacherEmail, textTeacherPassword)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    //login successful
                    sweetAlertDialogProgress.apply {
                        changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                        titleText = "Login Successful"
                        confirmText = "open"
                        setConfirmClickListener {
                            //intent migration to bursa dashBoard
                            startActivity(Intent(this@HandleLogin, TeacherDash::class.java))
                            finish()
                            dismiss()
                        }
                    }
                } else if (!it.isSuccessful) {
                    //login error possibly email or password wrong
                    sweetAlertDialogProgress.apply {
                        changeAlertType(SweetAlertDialog.ERROR_TYPE)
                        contentText = it.exception?.message
                        titleText = "Login Failed"
                        confirmText = "retry"
                        cancelText = "cancel"

                        setConfirmClickListener {
                            dismiss()
                        }
                        setCancelClickListener {
                            dismiss()
                            funReturnHome()
                        }
                    }
                }
            }
    }

    private fun funProceedLoginDeputy(
        textDataAdminRole: String,
        materialAlertAdminRole: MaterialAlertDialogBuilder
    ) {

        materialAlertAdminRole.setTitle("Administration ($textDataAdminRole)")
        materialAlertAdminRole.setIcon(R.drawable.login_icon_2)
        materialAlertAdminRole.setCancelable(false)
        materialAlertAdminRole.setIcon(R.drawable.login_icon_2)
        materialAlertAdminRole.setView(loginUI)
        materialAlertAdminRole.background = ResourcesCompat.getDrawable(
            this@HandleLogin.resources,
            R.drawable.background_main_profile,
            this@HandleLogin.theme
        )
        materialAlertAdminRole.setPositiveButton("login") { dg, _ ->
            //code begins
            val textAdminEnrolKey = editTextEnrolKeyStaff.text.toString().trim()
            val textAdminEmail = editTextStaffEmail.text.toString().trim()
            val textAdminPassword = editTextStaffPassword.text.toString().trim()
            if (textAdminEmail.isEmpty() || textAdminEnrolKey.isEmpty() || textAdminPassword.isEmpty()) {
                Toast.makeText(this@HandleLogin, "cannot submit empty fields!", Toast.LENGTH_SHORT)
                    .show()
                //return home main profile
                funBackHome()

            } else {
                //call fun to proceed login to the student dash
                funCheckAdminKeyDeputy(
                    textAdminPassword,
                    textAdminEmail,
                    textAdminEnrolKey,
                )
            }
            dg.dismiss()
            //code ends
        }
        materialAlertAdminRole.setNegativeButton("return") { dg, _ ->

            //return home
            funBackHome()

            //code
            dg.dismiss()
            //code
        }
        materialAlertAdminRole.create()
        materialAlertAdminRole.show()


    }

    private fun funCheckAdminKeyDeputy(
        textAdminPassword: String,
        textAdminEmail: String,
        textAdminEnrolKey: String
    ) {

        //show dg
        sweetAlertDialogProgress.create()
        sweetAlertDialogProgress.show()

        val storeKeys = FirebaseFirestore.getInstance()
        storeKeys.collection(COLLECTION_KEYS).document(DOCUMENT_KEYS).get().addOnCompleteListener {
            if (it.isSuccessful) {
                sweetAlertDialogProgress.titleText = "Validating"
                //compare the keys if they match @admin =(principal,bursa,deputy)
                val keyServer = it.result["admin_key"]
                if (keyServer.toString() == textAdminEnrolKey) {
                    //key found and true proceed
                    funProceedLoginDeputyNow(textAdminEmail, textAdminPassword)
                    //
                } else {
                    //wrong key inputted warn/alert
                    sweetAlertDialogProgress.apply {
                        changeAlertType(SweetAlertDialog.ERROR_TYPE)
                        titleText = "Wrong Key"
                        contentText = "enter valid key"
                        confirmText = "Okay"
                        cancelText = "cancel"

                        setConfirmClickListener {
                            dismissWithAnimation()
                        }
                        setCancelClickListener {
                            dismiss()
                            funReturnHome()
                        }
                    }
                }
            } else if (!it.isSuccessful) {
                //something  went wrong!
                sweetAlertDialogProgress.apply {
                    changeAlertType(SweetAlertDialog.ERROR_TYPE)
                    contentText = it.exception?.message
                    titleText = "Failed"
                    confirmText = "retry"
                    cancelText = "home"

                    setConfirmClickListener {
                        dismiss()
                    }
                    setCancelClickListener {
                        dismiss()
                        funReturnHome()
                    }
                }
            }
        }
    }

    private fun funProceedLoginDeputyNow(textAdminEmail: String, textAdminPassword: String) {

        //login user
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInWithEmailAndPassword(textAdminEmail, textAdminPassword)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    //login successful
                    sweetAlertDialogProgress.apply {
                        changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                        titleText = "Login Successful"
                        confirmText = "open"
                        setConfirmClickListener {
                            //intent migration to bursa dashBoard
                            startActivity(Intent(this@HandleLogin, DeputyDash::class.java))
                            finish()
                            dismiss()
                        }
                    }
                } else if (!it.isSuccessful) {
                    //login error possibly email or password wrong
                    sweetAlertDialogProgress.apply {
                        changeAlertType(SweetAlertDialog.ERROR_TYPE)
                        contentText = it.exception?.message
                        titleText = "Login Failed"
                        confirmText = "retry"
                        cancelText = "cancel"

                        setConfirmClickListener {
                            dismiss()
                        }
                        setCancelClickListener {
                            dismiss()
                            funReturnHome()
                        }
                    }
                }
            }
    }

    private fun funProceedLoginPrincipal(
        textDataAdminRole: String,
        materialAlertAdminRole: MaterialAlertDialogBuilder
    ) {


        materialAlertAdminRole.setTitle("Administration ($textDataAdminRole)")
        materialAlertAdminRole.setIcon(R.drawable.login_icon_2)
        materialAlertAdminRole.setCancelable(false)
        materialAlertAdminRole.setIcon(R.drawable.login_icon_2)
        materialAlertAdminRole.setView(loginUI)
        materialAlertAdminRole.background = ResourcesCompat.getDrawable(
            this@HandleLogin.resources,
            R.drawable.background_main_profile,
            this@HandleLogin.theme
        )
        materialAlertAdminRole.setPositiveButton("login") { dg, _ ->
            //code begins
            val textAdminEnrolKey = editTextEnrolKeyStaff.text.toString().trim()
            val textAdminEmail = editTextStaffEmail.text.toString().trim()
            val textAdminPassword = editTextStaffPassword.text.toString().trim()
            if (textAdminEmail.isEmpty() || textAdminEnrolKey.isEmpty() || textAdminPassword.isEmpty()) {
                Toast.makeText(this@HandleLogin, "cannot submit empty fields!", Toast.LENGTH_SHORT)
                    .show()
                //return home main profile
                funBackHome()

            } else {
                //call fun to proceed login to the student dash
                funCheckAdminKeyPrincipal(
                    textAdminPassword,
                    textAdminEmail,
                    textAdminEnrolKey,
                )

            }
            dg.dismiss()
            //code ends
        }
        materialAlertAdminRole.setNegativeButton("return") { dg, _ ->

            //return home
            funBackHome()

            //code
            dg.dismiss()
            //code
        }
        materialAlertAdminRole.create()
        materialAlertAdminRole.show()


    }

    private fun funCheckAdminKeyPrincipal(
        textAdminPassword: String,
        textAdminEmail: String,
        textAdminEnrolKey: String
    ) {

        //show dg
        sweetAlertDialogProgress.create()
        sweetAlertDialogProgress.show()

        val storeKeys = FirebaseFirestore.getInstance()
        storeKeys.collection(COLLECTION_KEYS).document(DOCUMENT_KEYS).get().addOnCompleteListener {
            if (it.isSuccessful) {
                sweetAlertDialogProgress.titleText = "Validating"
                //compare the keys if they match @admin =(principal,bursa,deputy)
                val keyServer = it.result["admin_key"]
                if (keyServer.toString() == textAdminEnrolKey) {
                    //key found and true proceed
                    funProceedLoginPrincipalNow(textAdminEmail, textAdminPassword)
                    //
                } else {
                    //wrong key inputted warn/alert
                    sweetAlertDialogProgress.apply {
                        changeAlertType(SweetAlertDialog.ERROR_TYPE)
                        titleText = "Wrong Key"
                        contentText = "enter valid key"
                        confirmText = "Okay"
                        cancelText = "cancel"

                        setConfirmClickListener {
                            dismissWithAnimation()
                        }
                        setCancelClickListener {
                            dismiss()
                            funReturnHome()
                        }
                    }
                }
            } else if (!it.isSuccessful) {
                //something  went wrong!
                sweetAlertDialogProgress.apply {
                    changeAlertType(SweetAlertDialog.ERROR_TYPE)
                    contentText = it.exception?.message
                    titleText = "Failed"
                    confirmText = "retry"
                    cancelText = "home"

                    setConfirmClickListener {
                        dismiss()
                    }
                    setCancelClickListener {
                        dismiss()
                        funReturnHome()
                    }
                }
            }
        }
    }

    private fun funProceedLoginPrincipalNow(textAdminEmail: String, textAdminPassword: String) {


        //login user
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInWithEmailAndPassword(textAdminEmail, textAdminPassword)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    //login successful
                    sweetAlertDialogProgress.apply {
                        changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                        titleText = "Login Successful"
                        confirmText = "open"
                        setConfirmClickListener {
                            //intent migration to bursa dashBoard
                            startActivity(Intent(this@HandleLogin, PrincipalDash::class.java))
                            finish()
                            dismiss()
                        }
                    }
                } else if (!it.isSuccessful) {
                    //login error possibly email or password wrong
                    sweetAlertDialogProgress.apply {
                        changeAlertType(SweetAlertDialog.ERROR_TYPE)
                        contentText = it.exception?.message
                        titleText = "Login Failed"
                        confirmText = "retry"
                        cancelText = "cancel"

                        setConfirmClickListener {
                            dismiss()
                        }
                        setCancelClickListener {
                            dismiss()
                            funReturnHome()
                        }
                    }
                }
            }
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
            if (textStudentEmail.isEmpty()|| textStudentPassword.isEmpty()) {
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
        textStudentPassword: String
    ) {
        //code begins
        sweetAlertDialogProgress.apply {
            titleText="processing"
            create()
            show()
        }

        val firebaseAuth=FirebaseAuth.getInstance()
        firebaseAuth.signInWithEmailAndPassword(textStudentEmail,textStudentPassword).addOnCompleteListener {
            if (it.isSuccessful)
            {
                //migrate to the studentDash
                sweetAlertDialogProgress.apply {

                    startActivity(Intent(this@HandleLogin,StudentDash::class.java))
                    finish()
                    dismiss()
                }
            }
            else if (!it.isSuccessful)
            {
                //something  went wrong!
                sweetAlertDialogProgress.apply {
                    changeAlertType(SweetAlertDialog.ERROR_TYPE)
                    contentText = it.exception?.message
                    titleText = "Failed"
                    confirmText = "retry"
                    cancelText = "home"

                    setConfirmClickListener {
                        dismiss()
                        funReturnHome()
                    }
                    setCancelClickListener {
                        dismiss()
                        funReturnHome()
                    }
                }
            }
        }
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
            val textParentEmail = edtParentEmail.text.toString().trim()
            val textParentPassword = edtParentPassword.text.toString().trim()
            if (textParentEmail.isEmpty() || textParentPassword.isEmpty()) {
                Toast.makeText(this@HandleLogin, "cannot submit empty fields", Toast.LENGTH_SHORT)
                    .show()
                dg.dismiss()

                //return home
                funBackHome()

            } else {
                //call fun to proceed login to the student dash
                funProceedParentLogin(textParentPassword, textParentEmail)
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
    ) {

        //code

        sweetAlertDialogProgress.apply {
            titleText="processing"
            create()
            show()
        }

        val firebaseAuth=FirebaseAuth.getInstance()
        firebaseAuth.signInWithEmailAndPassword(textParentEmail,textParentPassword).addOnCompleteListener {
            if (it.isSuccessful)
            {
                //migrate to the parent=studentDash
                sweetAlertDialogProgress.apply {
                    startActivity(Intent(this@HandleLogin,StudentDash::class.java))
                    finish()
                    dismiss()
                }
            }
            else if (!it.isSuccessful)
            {

                //something  went wrong!
                sweetAlertDialogProgress.apply {
                    changeAlertType(SweetAlertDialog.ERROR_TYPE)
                    contentText = it.exception?.message
                    titleText = "Failed"
                    confirmText = "retry"
                    cancelText = "home"

                    setConfirmClickListener {
                        dismiss()
                    }
                    setCancelClickListener {
                        dismiss()
                        funReturnHome()
                    }
                }
            }
        }
        //code
    }
}