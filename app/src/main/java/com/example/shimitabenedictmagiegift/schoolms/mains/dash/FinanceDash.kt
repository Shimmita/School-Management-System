package com.example.shimitabenedictmagiegift.schoolms.mains.dash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.shimitabenedictmagiegift.schoolms.R
import com.example.shimitabenedictmagiegift.schoolms.mains.adapters.MyAdapterFinance
import com.example.shimitabenedictmagiegift.schoolms.mains.dash.BursaDash.Companion.COLLECTION_CLASSES_FEES
import com.example.shimitabenedictmagiegift.schoolms.mains.data_class_main.DataClassFinance
import com.example.shimitabenedictmagiegift.schoolms.mains.main_activities.MainProfile
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore

class FinanceDash : AppCompatActivity() {
    lateinit var recyclerViewFinance: RecyclerView
    lateinit var schoolCode: String
    lateinit var schoolName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finance_dash)
        funInit()
        funFetchFinanceData()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun funFetchFinanceData() {

        val sweetAlertDialogFeesProgress =
            SweetAlertDialog(this@FinanceDash, SweetAlertDialog.PROGRESS_TYPE)
        sweetAlertDialogFeesProgress.titleText = "Wait"
        sweetAlertDialogFeesProgress.setCancelable(false)
        sweetAlertDialogFeesProgress.create()
        sweetAlertDialogFeesProgress.show()

        val storeClassesFee = FirebaseFirestore.getInstance()
        storeClassesFee.collection(COLLECTION_CLASSES_FEES).get().addOnCompleteListener {
            if (it.isSuccessful) {
                if (it.result.isEmpty) {
                    //not posted
                    sweetAlertDialogFeesProgress.dismissWithAnimation()

                    val titleAlert = "Finance"
                    val message =
                        "finance department is working on the data stay connected it will be updated soon"
                    funAlertNotUpdated(titleAlert, message)
                } else {
                    //posted

                    sweetAlertDialogFeesProgress.dismissWithAnimation()

                    val tempArray = arrayListOf<DataClassFinance>()
                    for (doc in it.result.documents) {
                        val classDataFilter: DataClassFinance? =
                            doc.toObject(DataClassFinance::class.java)
                        if (classDataFilter != null) {
                            tempArray.add(classDataFilter)
                        }
                    }

                    val adapterFinance = MyAdapterFinance(this@FinanceDash, tempArray, schoolCode)

                    recyclerViewFinance.apply {
                        adapter = adapterFinance
                        adapterFinance.notifyDataSetChanged()
                        layoutManager = LinearLayoutManager(this@FinanceDash)
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
    }

    private fun funInit() {
        this.title = getString(R.string.finance_dash)
        recyclerViewFinance = findViewById(R.id.rvFinance)

        //obtain data from shared preference
        val sharedPreferences =
            getSharedPreferences(MainProfile.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        schoolCode = sharedPreferences.getString("code", "").toString().trim()
        schoolName = sharedPreferences.getString("name", "").toString().trim()
    }

    private fun funAlertNotUpdated(title: String, message: String) {

        val materialAlertNoUpdated = MaterialAlertDialogBuilder(this@FinanceDash)
        materialAlertNoUpdated.setCancelable(false)
        materialAlertNoUpdated.setTitle(title)
        materialAlertNoUpdated.setMessage(message)
        materialAlertNoUpdated.setPositiveButton("okay") { dg, _ ->
            dg.dismiss()
            funReturnHome()
        }
        materialAlertNoUpdated.setIcon(R.drawable.baseline_info_24)
        materialAlertNoUpdated.create()
        materialAlertNoUpdated.show()

    }

    private fun funReturnHome() {
        startActivity(Intent(this@FinanceDash, MainProfile::class.java))
    }

}