package com.example.shimitabenedictmagiegift.schoolms.mains.main_activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
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
        const val SHARED_PREFERENCE_NAME = "SCHOOL_MS"

    }

    //declaration of the globals
    lateinit var recyclerView: RecyclerView
    //
    lateinit var sharedPreferences: SharedPreferences
    //

    var schoolCode: String = ""
    var schoolName: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_profile)
        //init the rv/globals
        funInitGlobals()
        //call fun to populate data on the rv
        funPopulateRvMainProfile()
    }




    @SuppressLint("NotifyDataSetChanged")
    private fun funPopulateRvMainProfile() {
        //code begins
        val arraylistDatClass = arrayListOf<DataClassMainsProfile>()
        arraylistDatClass.add(DataClassMainsProfile(R.drawable.school_msi_1, "student admission"))
        arraylistDatClass.add(
            DataClassMainsProfile(
                R.drawable.login_imcons,
                "Staff Registration"
            )
        )
        arraylistDatClass.add(DataClassMainsProfile(R.drawable.logins_icon, "Login"))
        arraylistDatClass.add(DataClassMainsProfile(R.drawable.aca, "Academics"))
        arraylistDatClass.add(DataClassMainsProfile(R.drawable.finance, "Finance"))
        arraylistDatClass.add(DataClassMainsProfile(R.drawable.news, "School News"))
        arraylistDatClass.add(DataClassMainsProfile(R.drawable.calendar, "Calendar"))
        arraylistDatClass.add(DataClassMainsProfile(R.drawable.logout,"Logout"))


        val adapterMain = MyAdapterProfileMain(arraylistDatClass, this@MainProfile, schoolCode)
        //
        recyclerView.apply {
            adapter = adapterMain
            layoutManager = GridLayoutManager(this@MainProfile, 2)
            adapterMain.notifyDataSetChanged()
        }


    }

    private fun funInitGlobals() {
        //code begins
        schoolCode=getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).getString("code","").toString()
        schoolName=getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).getString("name","").toString()

        Log.d(TAG, "funCheckIntentData: school_Code:${schoolCode}\n")
        Log.d(TAG, "funCheckIntentData: school_Name:${schoolName}\n")
        //set title
        this.title = schoolName


        //
        recyclerView = findViewById(R.id.rvMainProfile)
        //code ends
    }

}