package com.example.shimitabenedictmagiegift.schoolms.mains.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shimitabenedictmagiegift.schoolms.R
import com.example.shimitabenedictmagiegift.schoolms.mains.data_class_main.DataClassSchoolNews

class MyAdapterSchoolNews(val context: Context, var arrayList: ArrayList<DataClassSchoolNews>) :
    RecyclerView.Adapter<MyAdapterSchoolNews.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_news_get, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var arrayItemPosition = arrayList[position]
        holder.apply {
            textViewNewsTitle.text = arrayItemPosition.title
            textViewNewsSender.text = arrayItemPosition.sender
            textViewNewsMessage.text = arrayItemPosition.message
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewNewsTitle: TextView
        var textViewNewsMessage: TextView
        var textViewNewsSender: TextView

        init {

            textViewNewsMessage = itemView.findViewById(R.id.tvMessageNewsRetrieve)
            textViewNewsSender = itemView.findViewById(R.id.tvSenderNewsRetrieve)
            textViewNewsTitle = itemView.findViewById(R.id.tvTitleNewsRetrieve)
        }
    }
}