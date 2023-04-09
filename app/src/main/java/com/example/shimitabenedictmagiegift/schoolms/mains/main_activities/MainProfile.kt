package com.example.shimitabenedictmagiegift.schoolms.mains.main_activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shimitabenedictmagiegift.schoolms.R
import com.example.shimitabenedictmagiegift.schoolms.mains.adapters.MyAdapterProfileMain
import com.example.shimitabenedictmagiegift.schoolms.mains.data_class_main.DataClassMainsProfile

class MainProfile : AppCompatActivity() {
    companion object {
        private const val TAG = "MainProfile"
    }

    //declaration of the globals
    lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_profile)
        //init the rv/globals
        funInitGlobals()
        //call fun to populate data on the rv
        funPopulateRvMainProfile()
        //extract the intent data
        funCheckIntentData()
    }

    private fun funCheckIntentData() {
        //code begins
        val schoolName = intent.getStringExtra("school_name")
        val schoolCode = intent.getStringExtra("school_code")
        //check presence of any null
        schoolCode?.trim()
        schoolName?.trim()
        //
        if (schoolName != null || schoolCode != null) {
            Log.d(TAG, "funCheckIntentData: school_Code:${schoolCode}")
            //set the title of the status bar to the school name
            this.title=schoolName
        }
        //code ends
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun funPopulateRvMainProfile() {
        //code begins
        val arraylistDatClass = arrayListOf<DataClassMainsProfile>()
        arraylistDatClass.add(DataClassMainsProfile(R.drawable.school_msi_1, "student admission"))
        arraylistDatClass.add(
            DataClassMainsProfile(
                R.drawable.register_icons,
                "Staff Registration"
            )
        )
        arraylistDatClass.add(DataClassMainsProfile(R.drawable.logins_icon, "Login"))
        arraylistDatClass.add(DataClassMainsProfile(R.drawable.aca, "Academics"))
        arraylistDatClass.add(DataClassMainsProfile(R.drawable.finance, "Finance"))
        arraylistDatClass.add(DataClassMainsProfile(R.drawable.sports2, "Sports"))
        arraylistDatClass.add(DataClassMainsProfile(R.drawable.news, "School News"))
        arraylistDatClass.add(DataClassMainsProfile(R.drawable.calendar, "Calendar"))

        val adapterMain = MyAdapterProfileMain(arraylistDatClass, this@MainProfile)
        //
        recyclerView.apply {
            adapter = adapterMain
            layoutManager = GridLayoutManager(this@MainProfile, 2)
            adapterMain.notifyDataSetChanged()
        }

        //
        //update the title respectively
        this.title = getString(R.string.title)
        //
    }

    private fun funInitGlobals() {
        //code begin
        recyclerView = findViewById(R.id.rvMainProfile)
        //code ends
    }
}