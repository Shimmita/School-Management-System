package com.example.shimitabenedictmagiegift.schoolms.mains.dash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.shimitabenedictmagiegift.schoolms.R
import com.example.shimitabenedictmagiegift.schoolms.mains.adapters.MyAdapterSchoolNews
import com.example.shimitabenedictmagiegift.schoolms.mains.dash.BursaDash.Companion.COLLECTION_NEWS
import com.example.shimitabenedictmagiegift.schoolms.mains.data_class_main.DataClassSchoolNews
import com.example.shimitabenedictmagiegift.schoolms.mains.main_activities.MainProfile
import com.google.firebase.firestore.FirebaseFirestore

class NewsDash : AppCompatActivity() {
    lateinit var recyclerViewNews: RecyclerView
    lateinit var sweetAlertDialogProgress: SweetAlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_dash)
        funInitGlobals()
        funRetrieveFromStoreNews()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun funRetrieveFromStoreNews() {
        //code begins
        val storeNews = FirebaseFirestore.getInstance()
        storeNews.collection(COLLECTION_NEWS).get().addOnCompleteListener {
            if (it.isSuccessful) {

                if (it.result.isEmpty) {
                    //data empty not posted
                    sweetAlertDialogProgress.apply {
                        changeAlertType(SweetAlertDialog.NORMAL_TYPE)
                        titleText = "NO DATA"
                        contentText = "no news uploaded"
                        confirmText = "okay"
                        setConfirmClickListener {
                            startActivity(Intent(this@NewsDash, MainProfile::class.java))
                            finish()
                            dismiss()
                        }
                    }
                } else {

                    //data present load it
                    val tempArrayList = arrayListOf<DataClassSchoolNews>()
                    for (post in it.result.documents) {
                        val dataClass: DataClassSchoolNews? =
                            post.toObject(DataClassSchoolNews::class.java)
                        if (dataClass != null) {
                            tempArrayList.add(dataClass)
                        }
                    }

                    //loading the data to the recycler view
                    val adapterNews = MyAdapterSchoolNews(this@NewsDash, tempArrayList)
                    recyclerViewNews.apply {
                        layoutManager = LinearLayoutManager(this@NewsDash)
                        adapter = adapterNews
                        adapterNews.notifyDataSetChanged()
                    }
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

    private fun funInitGlobals() {
        //code begins
        this.title = "SCHOOL NEWS"
        recyclerViewNews = findViewById(R.id.rvNewsRetrieval)
        //init of pgDialog
        sweetAlertDialogProgress = SweetAlertDialog(this@NewsDash, SweetAlertDialog.PROGRESS_TYPE)
        sweetAlertDialogProgress.setCancelable(false)
        sweetAlertDialogProgress.titleText = "searching"
        //code ends
    }
}