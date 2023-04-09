package com.example.shimitabenedictmagiegift.schoolms.mains.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shimitabenedictmagiegift.schoolms.R
import com.example.shimitabenedictmagiegift.schoolms.mains.data_class_main.DataClassMainsProfile
import de.hdodenhof.circleimageview.CircleImageView

class MyAdapterProfileMain(var arrayList: ArrayList<DataClassMainsProfile>, var context: Context) :
    RecyclerView.Adapter<MyAdapterProfileMain.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun performActionOnCardClick(textPresent: String?) {
            //code begins
            //card anim
            cardView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate_avg))
            //delay 1sec and proceed to the next task by function call
            Toast.makeText(context, textPresent, Toast.LENGTH_SHORT).show()
            //code ends
        }

        val circularImageView: CircleImageView
        val textView: TextView
        val cardView: CardView

        init {
            circularImageView = itemView.findViewById(R.id.circleImageMainProfile)
            textView = itemView.findViewById(R.id.tvDetailsMainProfile)
            cardView = itemView.findViewById(R.id.cardAdapterMainProfile)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        //code begins
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.data_view_main, parent, false)
        return MyViewHolder(view)
        //code ends
    }

    override fun getItemCount(): Int {
        //return the size of the arraylist
        return arrayList.size
        //
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val dataClass = arrayList[position]
        holder.apply {
            textView.text = dataClass.title
            //loading the image using the Glide library
            Glide.with(context).load(dataClass.icon).into(circularImageView)
            //setOnclick listener on the card
            cardView.setOnClickListener {
                val textPresent = dataClass.title
                holder.performActionOnCardClick(textPresent)
            }
            //
        }
    }
}