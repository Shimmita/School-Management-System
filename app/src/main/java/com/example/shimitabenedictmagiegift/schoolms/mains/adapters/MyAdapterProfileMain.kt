package com.example.shimitabenedictmagiegift.schoolms.mains.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shimitabenedictmagiegift.schoolms.R
import com.example.shimitabenedictmagiegift.schoolms.mains.dash.NewsDash
import com.example.shimitabenedictmagiegift.schoolms.mains.data_class_main.DataClassMainsProfile
import com.example.shimitabenedictmagiegift.schoolms.mains.main_activities.HandleLogin
import com.example.shimitabenedictmagiegift.schoolms.mains.main_activities.StaffRegistration
import com.example.shimitabenedictmagiegift.schoolms.mains.main_activities.StudentRegistration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.system.exitProcess

class MyAdapterProfileMain(
    var arrayList: ArrayList<DataClassMainsProfile>,
    var context: Context,
    var globalSchoolCode: String
) :
    RecyclerView.Adapter<MyAdapterProfileMain.MyViewHolder>() {
    companion object {
        private const val TAG = "MyAdapterProfileMain"

    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("InflateParams")
        fun performActionOnCardClick(textPresent: String?) {
            //code begins

            cardView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.abc_fade_out))

            if (textPresent.toString().contains("student", true)) {
                //launch activity student registration since this is the one requested
                cardView.postDelayed({
                    context.startActivity(Intent(context, StudentRegistration::class.java))

                }, 200)
            } else if (textPresent.toString().contains("staff", true)) {
                //launch staff registration
                cardView.postDelayed({
                    context.startActivity(Intent(context, StaffRegistration::class.java))
                }, 200)
            } else if (textPresent.toString().contains("login", true)) {
                cardView.postDelayed({
                    //funShow Alert for user to decide the way of login
                    val loginUI: View =
                        LayoutInflater.from(context).inflate(R.layout.login_ui, null, false)
                    //inflate spinner and set adapter on it
                    val spinnerLoginRole: Spinner = loginUI.findViewById(R.id.spinnerStaffRoleLogin)
                    val adapterRole = ArrayAdapter.createFromResource(
                        context,
                        R.array.login_as,
                        android.R.layout.simple_list_item_1
                    )
                    spinnerLoginRole.adapter = adapterRole

                    funAlertLoginAs(loginUI, spinnerLoginRole)
                }, 230)
            } else if (textPresent.toString().contains("academics", true)) {
                //alert user current have not been posted
                val title = "academics"
                val message =
                    "dear user the results have not yet been posted you will get notified when they become updated"
                funAlertNotUpdated(title, message)
            } else if (textPresent.toString().contains("finance", true)) {
                funAlertNotUpdated(
                    "Finance",
                    "finance department is working on the data stay connected it will be updated soon"
                )

            } else if (textPresent.toString().contains("sport", true)) {
                AlertDialog.Builder(context).setMessage("games department will post results soon")
                    .setCancelable(false)
                    .setTitle("Games And Sports")
                    .setPositiveButton("ok") { dg, _ ->
                        dg.dismiss()
                    }.create().show()
            } else if (textPresent.toString().contains("news", true)) {
                //migrate to the news DashBoard
                cardView.postDelayed({
                    context.startActivity(Intent(context, NewsDash::class.java))
                }, 200)

                //

            } else if (textPresent.toString().contains("calendar", true)) {
                funAlertNotUpdated(
                    "school calendar",
                    "school is up and running the opening and closing dates will be  updated  by next week of this month"
                )
            } else if (textPresent.toString().contains("logout", true)) {
                funAlertExit()
            }

            //code ends
        }

        private fun funAlertExit() {
            //code begins

            val materialAlertExit = MaterialAlertDialogBuilder(context)
            materialAlertExit.setCancelable(false)
            materialAlertExit.setTitle("Application Exit")
            materialAlertExit.setMessage("application will exit")
            materialAlertExit.setPositiveButton("sure") { dg, _ ->
                dg.dismiss()
                //sign out the current user if present
                if (FirebaseAuth.getInstance().currentUser!=null)
                {
                    FirebaseAuth.getInstance().signOut()
                    exitProcess(0)
                }
                else
                    exitProcess(0)

            }
            materialAlertExit.setNegativeButton("no") { dg, _ ->

                //dismiss
                dg.dismiss()
                //
            }
            materialAlertExit.setIcon(R.drawable.baseline_info_24)
            materialAlertExit.create()
            materialAlertExit.show()            //code ends
        }

        private fun funAlertLoginAs(loginUI: View, spinnerLoginRole: Spinner) {

            val editRoleSelection: EditText = loginUI.findViewById(R.id.edtRoleLogin)

            val materialAlertLoginAs = MaterialAlertDialogBuilder(context)
            materialAlertLoginAs.setTitle("Login")
            materialAlertLoginAs.background = ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.background_main_profile,
                context.theme
            )
            materialAlertLoginAs.setView(loginUI)
            materialAlertLoginAs.setCancelable(false)
            materialAlertLoginAs.setPositiveButton("OK") { dg, _ ->
                val roleText = editRoleSelection.text.toString().trim()
                //check if the selected role
                if (roleText.isEmpty()) {
                    Toast.makeText(context, "select role", Toast.LENGTH_SHORT).show()
                } else {
                    //role contains text thus evaluate
                    if (roleText.contains("student", true)) {

                        //migrate to the activity that handles the login and disables the above views
                        val intentParentLogin = Intent(context, HandleLogin::class.java)
                        intentParentLogin.putExtra("role", "student")
                        handleLoginIntents(intentParentLogin)
                        //
                    } else if (roleText.contains("administration", true)) {

                        //
                        val intentStaffLogin = Intent(context, HandleLogin::class.java)
                        intentStaffLogin.putExtra("role", "administration")
                        handleLoginIntents(intentStaffLogin)

                    } else if (roleText.contains("parent", true)) {
                        //migrate to parent
                        val intentParentLogin = Intent(context, HandleLogin::class.java)
                        intentParentLogin.putExtra("role", "parent")
                        handleLoginIntents(intentParentLogin)


                    }
                    dg.dismiss()

                }

            }
            materialAlertLoginAs.setNegativeButton("return") { dg, _ ->

                //dismiss the dg
                dg.dismiss()
            }
            materialAlertLoginAs.create()
            materialAlertLoginAs.show()

            //setting onclick listener to the spinner
            spinnerLoginRole.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    //code begins
                    editRoleSelection.setText(p0?.getItemAtPosition(p2).toString())
                    Log.d(TAG, "onItemSelected: ${p0?.getItemAtPosition(p2).toString()}")

                    //code ends
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    //nothing
                }

            }
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

    private fun funAlertNotUpdated(title: String, message: String) {

        val materialAlertNoUpdated = MaterialAlertDialogBuilder(context)
        materialAlertNoUpdated.setCancelable(false)
        materialAlertNoUpdated.setTitle(title)
        materialAlertNoUpdated.setMessage(message)
        materialAlertNoUpdated.setPositiveButton("okay") { dg, _ ->

            dg.dismiss()
        }
        materialAlertNoUpdated.setIcon(R.drawable.baseline_info_24)
        materialAlertNoUpdated.create()
        materialAlertNoUpdated.show()

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

    fun handleLoginIntents(intent: Intent) {
        //code begins
        context.startActivity(intent)
        //code ends
    }

}