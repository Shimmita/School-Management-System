package com.example.shimitabenedictmagiegift.schoolms.mains.dash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView

class BursaDash : AppCompatActivity() {
    companion object {
        private const val TAG = "BursaDash"
        const val COLLECTION_BURSA = "Bursa"
        const val COLLECTION_NEWS = "NEWS"
        const val COLLECTION_CLASSES_FEES = "CLASS_FEES"
    }

    //declaration of Globals
    private lateinit var appCompatButtonUpdateFess: AppCompatButton
    lateinit var appCompatButtonPostNews: AppCompatButton
    lateinit var appCompatButtonLogout: AppCompatButton
    lateinit var appCompatButtonPublishFees: AppCompatButton
    lateinit var sweetAlertDialogProgress: SweetAlertDialog
    lateinit var circleImageView: CircleImageView
    lateinit var textViewNameBursa: TextView
    private lateinit var schoolCode: String
    private lateinit var schoolName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bursa_dash)
        funInitGlobals()
        funInitOther()
    }

    @SuppressLint("InflateParams")
    private fun funInitOther() {
        //code begins

        appCompatButtonUpdateFess.setOnClickListener {
            it.apply {
                startAnimation(AnimationUtils.loadAnimation(this@BursaDash, R.anim.push_down_out))
                postDelayed({ funUpdateFees() }, 500)
            }
        }
        appCompatButtonLogout.setOnClickListener {
            it.apply {
                startAnimation(AnimationUtils.loadAnimation(this@BursaDash, R.anim.push_down_out))
                postDelayed({ funLogout() }, 500)
            }
        }
        appCompatButtonPostNews.setOnClickListener {
            it.apply {
                startAnimation(AnimationUtils.loadAnimation(this@BursaDash, R.anim.push_down_out))
                postDelayed({ funPostSchoolNews() }, 500)
            }
        }
        appCompatButtonPublishFees.setOnClickListener {
            it.apply {
                startAnimation(AnimationUtils.loadAnimation(this@BursaDash, R.anim.push_down_out))
                postDelayed({ funFeesClassUpdate() }, 500)
            }
        }


        //fun load bursa data
        funLoadBursaDataFromStore()
        //code ends
    }


    private fun funLoadBursaDataFromStore() {
        //code begins
        //path(SchoolCode/UID)
        sweetAlertDialogProgress.create()
        sweetAlertDialogProgress.show()
        //

        val currentUID = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val path = "$schoolCode$currentUID"

        val collectionBursa = FirebaseFirestore.getInstance()
        collectionBursa.collection(COLLECTION_BURSA).document(path).get().addOnCompleteListener {
            if (it.isSuccessful) {
                sweetAlertDialogProgress.apply {
                    val name = it.result["name"].toString()
                    val imagePath = it.result["image"].toString()

                    //setting data on to the profile dashBoard
                    textViewNameBursa.text = name
                    Glide.with(this@BursaDash).load(imagePath).into(circleImageView)
                    //
                    dismissWithAnimation()
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
        //code ends
    }

    private fun funPostSchoolNews() {
        //code begins

        val viewPostNews: View = layoutInflater.inflate(R.layout.layout_news_post, null, false)
        val editTextTitle: EditText = viewPostNews.findViewById(R.id.edtTitlePost)
        val editTextMessage: EditText = viewPostNews.findViewById(R.id.edtMessagePost)

        val materialAlertAdmissionNumber = MaterialAlertDialogBuilder(this@BursaDash)
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
                Toast.makeText(this@BursaDash, "cannot submit empty data!", Toast.LENGTH_SHORT)
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
        //code ends
    }

    private fun funPostNow(title: String, message: String) {
        //code begins
        val sweetAlertDialogPostNews =
            SweetAlertDialog(this@BursaDash, SweetAlertDialog.PROGRESS_TYPE)
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
                keySender to "School Bursa",
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

    private fun funLogout() {
        //code begins

        val materialAlertLogout = MaterialAlertDialogBuilder(this@BursaDash)
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
        startActivity(Intent(this@BursaDash, MainProfile::class.java))
        finish()
    }

    private fun funUpdateFees() {
        //code begins
        val view: View = layoutInflater.inflate(R.layout.layout_student_adm_no, null, false)
        val editText: EditText = view.findViewById(R.id.edtStudentAdmissionNumber)

        val materialAlertAdmissionNumber = MaterialAlertDialogBuilder(this@BursaDash)
        materialAlertAdmissionNumber.setCancelable(false)
        materialAlertAdmissionNumber.setView(view)
        materialAlertAdmissionNumber.setTitle("admission number")
        materialAlertAdmissionNumber.setIcon(R.drawable.school_msi_1)
        materialAlertAdmissionNumber.background =
            ResourcesCompat.getDrawable(resources, R.drawable.background_main_profile, theme)
        materialAlertAdmissionNumber.setPositiveButton("check results") { dg, _ ->

            val text = editText.text.toString().trim()

            if (text.isEmpty()) {
                Toast.makeText(this@BursaDash, "cannot submit empty data!", Toast.LENGTH_SHORT)
                    .show()

            } else if (text.isNotEmpty()) {
                funSearchStudentUpdateFees(text)
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

    private fun funSearchStudentUpdateFees(text: String) {
        //code begins
        val sweetAlertDialogFeesProgress =
            SweetAlertDialog(this@BursaDash, SweetAlertDialog.PROGRESS_TYPE)
        sweetAlertDialogFeesProgress.titleText = "Fetching"
        sweetAlertDialogFeesProgress.setCancelable(false)
        sweetAlertDialogFeesProgress.create()
        sweetAlertDialogFeesProgress.show()


        val searchString = "$schoolCode$text"
        Log.d(TAG, "funSearchStudentUpdateFees: string search->$searchString")
        //search in the collection student fees/document=schoolCode/studentAdmissionNumber.
        val storeStudentFees = FirebaseFirestore.getInstance()
        storeStudentFees.collection(COLLECTION_FEES).get().addOnCompleteListener {
            if (it.isSuccessful) {

                //obtain the document ids
                val dataDocuments = it.result.documents

                var isStudentPresent = false
                var gottenStudentDocumentID = ""
                for (doc in dataDocuments) {
                    var docId = doc.id
                    Log.d(TAG, "funSearchStudentUpdateFees: docID=>$docId\n")
                    if (docId.contains(searchString)) {
                        isStudentPresent = true
                        gottenStudentDocumentID = docId
                    }
                }

                //check the results
                if (isStudentPresent) {
                    //student found thus proceed with update of the fees
                    //funAlertEnterFees(gottenStudentDocumentID)
                    Log.d(TAG, "funSearchStudentUpdateFees: CONGRATS FOUND")
                    val storeStudentFees = FirebaseFirestore.getInstance()
                    storeStudentFees.collection(COLLECTION_FEES).document(gottenStudentDocumentID)
                        .get().addOnCompleteListener {
                            if (it.isSuccessful) {
                                val studentName = it.result["name"].toString()
                                val studentForm = it.result["form"].toString()
                                val studentID = it.result["id"].toString()
                                val paid = it.result["paid"].toString()
                                val balance = it.result["balance"].toString()
                                val required = it.result["total"].toString()

                                sweetAlertDialogFeesProgress.apply {
                                    changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                                    titleText = studentName
                                    contentText = studentForm
                                    confirmText = "update fees"
                                    cancelText = "cancel"
                                    setConfirmClickListener {
                                        funAlertEnterFees(
                                            gottenStudentDocumentID,
                                            studentName,
                                            studentForm,
                                            studentID,
                                            paid,
                                            balance,
                                            required,
                                            sweetAlertDialogFeesProgress
                                        )
                                    }
                                    setCancelClickListener {
                                        it.dismiss()
                                    }
                                }

                            } else if (!it.isSuccessful) {
                                sweetAlertDialogFeesProgress.apply {
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
                    sweetAlertDialogFeesProgress.apply {
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
                sweetAlertDialogFeesProgress.apply {
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

    private fun funAlertEnterFees(
        gottenStudentDocumentID: String,
        studentName: String,
        studentForm: String,
        studentID: String,
        paid: String,
        balance: String,
        required: String,
        sweetAlertDialogFeesProgress: SweetAlertDialog
    ) {

        val viewEnterFees: View = layoutInflater.inflate(R.layout.layout_enter_fees, null, false)
        val editText: EditText = viewEnterFees.findViewById(R.id.edtStudentFeesPaid)

        val stringMessage =
            "Form: $studentForm\n\nPaid:$paid\n\nRequired Amount (p.a):$required\n \nBalance:$balance\n"

        val materialAlertEnterFees = MaterialAlertDialogBuilder(this@BursaDash)
        materialAlertEnterFees.setCancelable(false)
        materialAlertEnterFees.setTitle(studentName)
        materialAlertEnterFees.setMessage(stringMessage)
        materialAlertEnterFees.setView(viewEnterFees)
        materialAlertEnterFees.setIcon(R.drawable.school_msi_1)
        materialAlertEnterFees.background =
            ResourcesCompat.getDrawable(resources, R.drawable.background_main_profile, theme)
        materialAlertEnterFees.setPositiveButton("update") { dg, _ ->

            val text = editText.text.toString().trim()
            if (text.isEmpty()) {
                Toast.makeText(this@BursaDash, "cannot submit empty field!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                funProceedFinalUpdate(text, gottenStudentDocumentID, sweetAlertDialogFeesProgress)
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
        text: String,
        gottenStudentDocumentID: String,
        sweetAlertDialogFeesProgress: SweetAlertDialog
    ) {
        //code begins
        val keyPaid = "paid"
        val mapData = hashMapOf(keyPaid to text)
        val storeFees = FirebaseFirestore.getInstance()
        storeFees.collection(COLLECTION_FEES).document(gottenStudentDocumentID)
            .update(mapData as Map<String, Any>).addOnCompleteListener {
                if (it.isSuccessful) {
                    sweetAlertDialogFeesProgress.apply {
                        changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                        titleText = "UPDATE SUCCESSFUL"
                        contentText = "fees update"
                        confirmText = "thanks"
                        setConfirmClickListener {
                            dismiss()
                        }
                    }
                } else if (!it.isSuccessful) {
                    //error fees update failed
                    sweetAlertDialogFeesProgress.apply {
                        changeAlertType(SweetAlertDialog.ERROR_TYPE)
                        titleText = "Update Failed"
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

    private fun funFeesClassUpdate() {
        //code begins

        val viewResultsOption = layoutInflater.inflate(R.layout.layout_view_results, null, false)
        val editText: EditText = viewResultsOption.findViewById(R.id.edtResultsOption)

        val materialAlertDialogBuilderResultsOption = MaterialAlertDialogBuilder(this@BursaDash)
        materialAlertDialogBuilderResultsOption.setCancelable(false)
        materialAlertDialogBuilderResultsOption.setView(viewResultsOption)
        materialAlertDialogBuilderResultsOption.setTitle("select class")
        materialAlertDialogBuilderResultsOption.setIcon(R.drawable.school_msi_1)
        materialAlertDialogBuilderResultsOption.background =
            ResourcesCompat.getDrawable(resources, R.drawable.background_main_profile, theme)
        materialAlertDialogBuilderResultsOption.setPositiveButton("proceed") { dg, _ ->

            val text = editText.text.toString().trim()
            if (text.contains("form 1", true)) {
                //publish school fees form 1
                funPublishFeesForm1(text, materialAlertDialogBuilderResultsOption)
            } else if (text.contains("form 2", true)) {
                //publish school fees form 2
                funPublishFeesForm2(text, materialAlertDialogBuilderResultsOption)
            } else if (text.contains("form 3", true)) {
                //publish school fees form 3
                funPublishFeesForm3(text, materialAlertDialogBuilderResultsOption)
            } else if (text.contains("form 4", true)) {
                //publish school fees form 4
                funPublishFeesForm3(text, materialAlertDialogBuilderResultsOption)
            } else {
                //not available for other (CBC)
                funPublishFeesFormOther(text, materialAlertDialogBuilderResultsOption)
            }

            dg.dismiss()
        }
        materialAlertDialogBuilderResultsOption.setNegativeButton("dismiss") { dg, _ ->
            dg.dismiss()
        }
        materialAlertDialogBuilderResultsOption.create()
        materialAlertDialogBuilderResultsOption.show()

        val spinner: Spinner = viewResultsOption.findViewById(R.id.spinnerResultsOption)
        val adapter = ArrayAdapter.createFromResource(
            this@BursaDash,
            R.array.form_level,
            android.R.layout.simple_list_item_1
        )
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val textSelected = p0?.getItemAtPosition(p2).toString()
                editText.setText(textSelected)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                //nothing
            }

        }

    }

    private fun funPublishFeesFormOther(
        text: String,
        materialAlertDialogBuilderResultsOption: MaterialAlertDialogBuilder
    ) {


        //code begins

        //path form 1(schoolCode/form)
        val view: View = layoutInflater.inflate(R.layout.layout_enter_fees, null, false)
        val editText: EditText = view.findViewById(R.id.edtStudentFeesPaid)

        materialAlertDialogBuilderResultsOption.setView(view)
        materialAlertDialogBuilderResultsOption.setTitle("Publish Fees")
        materialAlertDialogBuilderResultsOption.setPositiveButton("Publish") { dg, _ ->
            val stringFees = editText.text.toString()
            funProceedUploadFees(stringFees, text)
        }
        materialAlertDialogBuilderResultsOption.setNegativeButton("dismiss") { dg, _ ->
            dg.dismiss()
        }
        materialAlertDialogBuilderResultsOption.create()
        materialAlertDialogBuilderResultsOption.show()
    }

    private fun funPublishFeesForm3(
        text: String,
        materialAlertDialogBuilderResultsOption: MaterialAlertDialogBuilder
    ) {


        //code begins

        //path form 1(schoolCode/form)
        val view: View = layoutInflater.inflate(R.layout.layout_enter_fees, null, false)
        val editText: EditText = view.findViewById(R.id.edtStudentFeesPaid)

        materialAlertDialogBuilderResultsOption.setView(view)
        materialAlertDialogBuilderResultsOption.setTitle("Publish Fees")
        materialAlertDialogBuilderResultsOption.setPositiveButton("Publish") { dg, _ ->
            val stringFees = editText.text.toString()
            funProceedUploadFees(stringFees, text)
        }
        materialAlertDialogBuilderResultsOption.setNegativeButton("dismiss") { dg, _ ->
            dg.dismiss()
        }
        materialAlertDialogBuilderResultsOption.create()
        materialAlertDialogBuilderResultsOption.show()
    }

    private fun funPublishFeesForm2(
        text: String,
        materialAlertDialogBuilderResultsOption: MaterialAlertDialogBuilder
    ) {

        //code begins

        //path form 1(schoolCode/form)
        val view: View = layoutInflater.inflate(R.layout.layout_enter_fees, null, false)
        val editText: EditText = view.findViewById(R.id.edtStudentFeesPaid)

        materialAlertDialogBuilderResultsOption.setView(view)
        materialAlertDialogBuilderResultsOption.setTitle("Publish Fees")
        materialAlertDialogBuilderResultsOption.setPositiveButton("Publish") { dg, _ ->
            val stringFees = editText.text.toString()
            funProceedUploadFees(stringFees, text)
        }
        materialAlertDialogBuilderResultsOption.setNegativeButton("dismiss") { dg, _ ->
            dg.dismiss()
        }
        materialAlertDialogBuilderResultsOption.create()
        materialAlertDialogBuilderResultsOption.show()
    }

    @SuppressLint("InflateParams")
    private fun funPublishFeesForm1(
        text: String,
        materialAlertDialogBuilderResultsOption: MaterialAlertDialogBuilder
    ) {
        //code begins

        //path form 1(schoolCode/form)
        val view: View = layoutInflater.inflate(R.layout.layout_enter_fees, null, false)
        val editText: EditText = view.findViewById(R.id.edtStudentFeesPaid)

        materialAlertDialogBuilderResultsOption.setView(view)
        materialAlertDialogBuilderResultsOption.setTitle("Publish Fees")
        materialAlertDialogBuilderResultsOption.setPositiveButton("Publish") { dg, _ ->
            val stringFees = editText.text.toString()
            funProceedUploadFees(stringFees, text)
        }
        materialAlertDialogBuilderResultsOption.setNegativeButton("dismiss") { dg, _ ->
            dg.dismiss()
        }
        materialAlertDialogBuilderResultsOption.create()
        materialAlertDialogBuilderResultsOption.show()
        //code ends
    }

    private fun funProceedUploadFees(stringFees: String, text: String) {
        //

        val sweetAlertDialogFeesProgress =
            SweetAlertDialog(this@BursaDash, SweetAlertDialog.PROGRESS_TYPE)
        sweetAlertDialogFeesProgress.titleText = "Publishing"
        sweetAlertDialogFeesProgress.setCancelable(false)
        sweetAlertDialogFeesProgress.create()
        sweetAlertDialogFeesProgress.show()

        text.lowercase()
        val path = "$schoolCode$text"
        val keyFees = "fee"
        val keyForm = "form"
        val keySchoolCode = "code"
        val mapData = hashMapOf(keyFees to stringFees, keyForm to text, keySchoolCode to schoolCode)
        val storePublishFees = FirebaseFirestore.getInstance()
        storePublishFees.collection(COLLECTION_CLASSES_FEES).document(path).set(mapData)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    sweetAlertDialogFeesProgress.apply {
                        changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                        titleText = "Successful"
                        contentText = "school fees posted"
                        confirmText = "ok"
                        setConfirmClickListener {
                            it.dismissWithAnimation()
                        }
                    }
                } else if (!it.isSuccessful) {
                    sweetAlertDialogFeesProgress.apply {
                        changeAlertType(SweetAlertDialog.ERROR_TYPE)
                        titleText = "Update Failed"
                        contentText = it.exception?.message
                        confirmText = "okay"
                        setConfirmClickListener {
                            it.dismiss()
                        }
                    }
                }
            }
        //
    }

    private fun funInitGlobals() {
        //code begins
        this.title = getString(R.string.bursa_dash)
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        schoolCode = sharedPreferences.getString("code", "").toString().trim()
        schoolName = sharedPreferences.getString("name", "").toString().trim()

        appCompatButtonPostNews = findViewById(R.id.btnPostInformation)
        appCompatButtonUpdateFess = findViewById(R.id.btnUpdateFees)
        appCompatButtonLogout = findViewById(R.id.btnLogoutBursa)
        appCompatButtonPublishFees = findViewById(R.id.btnPublishFees)
        circleImageView = findViewById(R.id.imgBursa)
        textViewNameBursa = findViewById(R.id.tvBursaName)

        //init of pgDialog
        sweetAlertDialogProgress = SweetAlertDialog(this@BursaDash, SweetAlertDialog.PROGRESS_TYPE)
        sweetAlertDialogProgress.setCancelable(false)
        sweetAlertDialogProgress.titleText = "Fetching Data"
        //code ends
    }
}