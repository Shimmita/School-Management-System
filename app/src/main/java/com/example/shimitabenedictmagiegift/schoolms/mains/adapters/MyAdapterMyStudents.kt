package com.example.shimitabenedictmagiegift.schoolms.mains.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.example.shimitabenedictmagiegift.schoolms.R
import com.example.shimitabenedictmagiegift.schoolms.mains.dash.TeacherDash
import com.example.shimitabenedictmagiegift.schoolms.mains.data_class_main.DataClassMyStudents
import com.example.shimitabenedictmagiegift.schoolms.mains.main_activities.StudentRegistration.Companion.COLLECTION_PARENTS
import com.example.shimitabenedictmagiegift.schoolms.mains.main_activities.StudentRegistration.Companion.COLLECTION_STUDENTS
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView

class MyAdapterMyStudents(
    var context: Context,
    var arrayList: ArrayList<DataClassMyStudents>,
    var formExpected: String,
    var schoolCode: String
) :
    RecyclerView.Adapter<MyAdapterMyStudents.MyViewHolder>() {
    companion object {
        private const val TAG = "MyAdapterMyStudents"
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_my_students, parent, false)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val arrayItem = arrayList[position]
        holder.apply {
            //display only form expected students by filtering the expected form with student form
            val studentForm = arrayItem.form
            if (studentForm.toString().lowercase() == formExpected.lowercase()) {
                //use the adm no to loop through the results of the students collection and obtain their grades and marks
                val admissionNumber = arrayItem.adm.toString()


                Glide.with(context).load(arrayItem.image).into(circleImageViewStudent)
                textViewStudentAdmissionName.text = "Adm:  ${arrayItem.adm}"
                textViewStudentEmail.text = arrayItem.email
                textViewStudentPhone.text = arrayItem.phone
                textViewStudentName.text = arrayItem.name

                textViewStudentGrade.text = "Grade:  ${arrayItem.grade}"
                textViewStudentPoints.text = "Points:  ${arrayItem.points}"

/*
                holder.funUpdateGradePoints(admissionNumber)
*/


                appCompatButtonContactParent.setOnClickListener {
                    val parentID = arrayItem.parent

                    val sweetAlertDialog = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
                    sweetAlertDialog.setCancelable(false)
                    sweetAlertDialog.titleText = "requesting"
                    sweetAlertDialog.create()
                    sweetAlertDialog.show()

                    funProcessContactParent(parentID, sweetAlertDialog)
                }
            }
        }
    }

    private fun funProcessContactParent(parentID: String?, sweetAlertDialog: SweetAlertDialog) {

        val stringParentID = parentID.toString()

        Log.d(TAG, "funProcessContactParent: PARENT ID:$stringParentID\n")
        val path = "$schoolCode$stringParentID"

        val storeFetchParent = FirebaseFirestore.getInstance()
        storeFetchParent.collection(COLLECTION_PARENTS).document(path).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {

                    val name = it.result["name"].toString()
                    val phone = it.result["phone"].toString()
                    val email = it.result["email"].toString()
                    val image = it.result["image"].toString()

                    sweetAlertDialog.apply {
                        dismiss()
                        funShowParentDetails(name, phone, email, image)
                    }

                } else if (!it.isSuccessful) {
                    sweetAlertDialog.apply {
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

    private fun funShowParentDetails(name: String, phone: String, email: String, image: String) {
        Log.d(
            TAG,
            "funShowParentDetails: PARENT NAME: $name\nPARENT PHONE: $phone\nPARENT EMAIL:$email\nPARENT IMAGE:$image\n"
        )

        val viewParentDetails =
            LayoutInflater.from(context).inflate(R.layout.layout_parent_student, null, false)
        val circleImageViewParentDetails: CircleImageView =
            viewParentDetails.findViewById(R.id.imgParentDetails)
        val textViewName: TextView = viewParentDetails.findViewById(R.id.tvParentName)
        val textViewPhone: TextView = viewParentDetails.findViewById(R.id.tvParentPhoneNumber)
        val textViewEmail: TextView = viewParentDetails.findViewById(R.id.tvParentEmail)

        //setting details before loading
        textViewName.text = name
        textViewPhone.text = phone
        textViewEmail.text = email
        Glide.with(context).load(image).into(circleImageViewParentDetails)
        //

        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
        materialAlertDialogBuilder.setCancelable(false)
        materialAlertDialogBuilder.setTitle("PARENT/GUARDIAN")
        materialAlertDialogBuilder.setView(viewParentDetails)
        materialAlertDialogBuilder.setPositiveButton("Call") { dg, _ ->
            dg.dismiss()
            funCallParentIntent(phone)
        }
        materialAlertDialogBuilder.setNegativeButton("cancel") { dg, _
            ->

            dg.dismiss()
        }
        materialAlertDialogBuilder.create()
        materialAlertDialogBuilder.show()
    }

    private fun funCallParentIntent(phone: String) {
        //start an intent to the phone call
        val numberIntent = Intent()
        numberIntent.action = Intent.ACTION_DIAL
        numberIntent.data = Uri.parse("tel:$phone")
        context.startActivity(numberIntent)
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun funUpdateGradePoints(admissionNumber: String) {
            val storeStudentResults = FirebaseFirestore.getInstance()
            storeStudentResults.collection(COLLECTION_STUDENTS).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val searchString = "$admissionNumber$schoolCode"
                    var isPresent = false
                    var gottenDocument = ""
                    for (doc in it.result) {
                        var docId = doc.id
                        if (docId.contains(searchString, true)) {
                            isPresent = true
                            gottenDocument = docId
                        }
                    }

                    if (isPresent) {
                        //student document found
                        funUpdateNowGradePoints(gottenDocument, storeStudentResults)
                    }

                } else if (!it.isSuccessful) {
                    //an error
                    Toast.makeText(context, it.exception?.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                    //back to TeacherDashBoard
                    funBackTeacherDashBoard()
                }
            }
        }

        private fun funUpdateNowGradePoints(
            gottenDocument: String,
            storeStudentResults: FirebaseFirestore
        ) {
            storeStudentResults.collection(COLLECTION_STUDENTS).document(gottenDocument).get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        //set the results to the tv grade and points
                        textViewStudentGrade.text = it.result["grade"].toString()
                        textViewStudentPoints.text = it.result["points"].toString()
                        //

                    } else if (!it.isSuccessful) {
                        //an error
                        Toast.makeText(
                            context,
                            it.exception?.message.toString(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        //back to TeacherDashBoard
                        funBackTeacherDashBoard()
                    }
                }
        }


        var textViewStudentName: TextView
        var textViewStudentAdmissionName: TextView
        var appCompatButtonContactParent: AppCompatButton
        var textViewStudentGrade: TextView
        var textViewStudentPoints: TextView
        var textViewStudentEmail: TextView
        var textViewStudentPhone: TextView
        var circleImageViewStudent: CircleImageView
        var cardViewMyStudents: CardView

        init {

            textViewStudentGrade = itemView.findViewById(R.id.tvStudentGradeTeacherDash)
            textViewStudentAdmissionName = itemView.findViewById(R.id.tvStudentAdmissionTeacherDash)
            textViewStudentName = itemView.findViewById(R.id.tvStudentNameTeacherDash)
            textViewStudentPoints = itemView.findViewById(R.id.tvStudentPointsTeacherDash)
            textViewStudentGrade = itemView.findViewById(R.id.tvStudentGradeTeacherDash)
            circleImageViewStudent = itemView.findViewById(R.id.imgStudentTeacherDash)
            appCompatButtonContactParent = itemView.findViewById(R.id.btnContactParent)
            textViewStudentEmail = itemView.findViewById(R.id.tvStudentEmailTeacherDash)
            textViewStudentPhone = itemView.findViewById(R.id.tvStudentPhoneTeacherDash)
            cardViewMyStudents = itemView.findViewById(R.id.myStudentsCard)
        }
    }

    private fun funBackTeacherDashBoard() {
        context.startActivity(Intent(context, TeacherDash::class.java))
    }

}