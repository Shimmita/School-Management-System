package com.example.shimitabenedictmagiegift.schoolms.mains.dash

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import com.example.shimitabenedictmagiegift.schoolms.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class StudentDash : AppCompatActivity() {
    companion object {
        private const val TAG = "StudentDash"
    }

    //declaration of the globals
    private lateinit var textViewStudentName: TextView
    private lateinit var textViewStudentAdmission: TextView
    private lateinit var textViewStudentGrade: TextView
    private lateinit var textViewStudentPoints: TextView
    private lateinit var textViewStudentForm: TextView
    private lateinit var appcompatButtonViewResults: AppCompatButton
    private lateinit var appCompatButtonViewFees: AppCompatButton
    private lateinit var appCompatButtonViewClassTeacher: AppCompatButton

    //
    private var studentUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_dash)
        //init Globals
        funInitGlobals()
        //fun init other
        funInitOtherGlobals()
    }

    @SuppressLint("InflateParams")
    private fun funInitOtherGlobals() {
        //events listeners
        appcompatButtonViewResults.setOnClickListener {

            val examWhichView: View =
                layoutInflater.inflate(R.layout.view_exam_result_which, null, false)
            var spinnerWhichExam: Spinner = examWhichView.findViewById(R.id.spinnerWhichResults)
            var editTextWhichExamResults: EditText =
                examWhichView.findViewById(R.id.edtWhichExamResults)

            //show alert dg
            val materialAlertDialogBuilder = MaterialAlertDialogBuilder(this@StudentDash)
            materialAlertDialogBuilder.setCancelable(false)
            materialAlertDialogBuilder.background =
                ResourcesCompat.getDrawable(resources, R.drawable.background_main_profile, theme)
            materialAlertDialogBuilder.setView(examWhichView)
            materialAlertDialogBuilder.setPositiveButton("view") { dg, _ ->

                editTextWhichExamResults = examWhichView.findViewById(R.id.edtWhichExamResults)


                //get the text from the edt text and evaluate
                val textWhich = editTextWhichExamResults.text.toString().trim()
                if (textWhich.isNotEmpty()) {
                    //call fun evaluate show
                    funProceedViewingResultSelection(textWhich)
                    dg.dismiss()
                    //
                } else if (textWhich.isEmpty()) {
                    Toast.makeText(this@StudentDash, "select again", Toast.LENGTH_SHORT).show()
                    dg.dismiss()
                }
                //
            }
            materialAlertDialogBuilder.setNegativeButton("dismiss", null)
            materialAlertDialogBuilder.create()
            materialAlertDialogBuilder.show()


            //spinner operations
            spinnerWhichExam = examWhichView.findViewById(R.id.spinnerWhichResults)
            //init of the spinner
            val adapterWhichExam = ArrayAdapter.createFromResource(
                this@StudentDash,
                R.array.which_exam_results,
                android.R.layout.simple_list_item_1
            )
            spinnerWhichExam.adapter = adapterWhichExam
            //setting listener
            spinnerWhichExam.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    //setting the edit text with the value selected
                    val selection: String = p0?.getItemAtPosition(p2).toString()
                    editTextWhichExamResults.setText(selection)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    //nothing
                }

            }
            //

        }

        appCompatButtonViewFees.setOnClickListener {

        }

        appCompatButtonViewClassTeacher.setOnClickListener {


        }


    }

    private fun funProceedViewingResultSelection(textWhich: String) {

        //code begins
        //todo:begin here
        //code ends
    }

    private fun funInitGlobals() {

        //code begins
        textViewStudentAdmission = findViewById(R.id.studentAdmission)
        textViewStudentName = findViewById(R.id.studentName)
        textViewStudentGrade = findViewById(R.id.studentGrade)
        textViewStudentPoints = findViewById(R.id.studentPoints)
        textViewStudentForm = findViewById(R.id.studentForm)
        appCompatButtonViewClassTeacher = findViewById(R.id.btnClassTeacherDetails)
        appCompatButtonViewFees = findViewById(R.id.btnFeesPaid)
        appcompatButtonViewResults = findViewById(R.id.btnExamResults)

        //code ends
    }
}