package com.example.shimitabenedictmagiegift.schoolms.mains.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shimitabenedictmagiegift.schoolms.R
import com.example.shimitabenedictmagiegift.schoolms.mains.data_class_main.DataClassFinance

class MyAdapterFinance(var context: Context, var arrayList: ArrayList<DataClassFinance>,var schoolCode:String) :
    RecyclerView.Adapter<MyAdapterFinance.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.fees_view_get, parent, false)
        return MyViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val arrayItem = arrayList[position]
        holder.apply {

            //load ony data of the school
            val codeDataBase=arrayItem.code.toString()
            if (codeDataBase == schoolCode)
            {
                val feeInt = arrayItem.fee.toString().toInt()
                textViewFees.text = "TOTAL FEES: KSH.${arrayItem.fee}"
                textViewForm.text = "${arrayItem.form} FEE STRUCTURE"
                textViewHalf.text = "MINIMUM REQUIRED: KSH.${feeInt.div(2)}"
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewForm: TextView
        var textViewFees: TextView
        var textViewHalf: TextView


        init {

            textViewFees = itemView.findViewById(R.id.tvFinanceWholeFees)
            textViewForm = itemView.findViewById(R.id.tvFinanceForm)
            textViewHalf = itemView.findViewById(R.id.tvFinanceHalf)
        }

    }

}