package com.kruelkotlinkiller.krowd

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.kruelkotlinkiller.krowd.databinding.FragmentSignUpTeacherBinding
import java.util.regex.Pattern

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [signUpTeacher.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [signUpTeacher.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignUpTeacher : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var binding: FragmentSignUpTeacherBinding
    lateinit var fname : EditText
    lateinit var lname : EditText
    lateinit var email : EditText
    lateinit var password : EditText
    lateinit var submitBtn : Button
    lateinit var databaseReference: DatabaseReference
    lateinit var database : FirebaseDatabase
    lateinit var mAuth : FirebaseAuth
    lateinit var teacherId : String
    lateinit var nav : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =  DataBindingUtil.inflate(inflater,R.layout.fragment_sign_up_teacher, container, false)
        submitBtn = binding.button
        fname = binding.simpleEditText
        lname = binding.simpleEditText2
        email = binding.simpleEditText5
        password = binding.simpleEditText7
        nav = binding.nav5
        databaseReference = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()




        submitBtn.setOnClickListener{ view : View ->
            if(fname.text.toString().isNotEmpty()
                && lname.text.toString().isNotEmpty()
                && email.text.toString().isNotEmpty()
                && password.text.toString().isNotEmpty()
                && isEmailValid(email.text.toString())) {

                saveTeacher()
                if(findNavController().currentDestination?.id == R.id.signUpTeacher) {
                    view.findNavController().navigate(R.id.action_signUpTeacher_to_teacher_login)
                }
            }
            else{
                val builder = AlertDialog.Builder(context)
                builder.setTitle("ERROR")
                if(!isEmailValid(email.text.toString())){
                    builder.setMessage("Please enter a valid email address")
                }else {
                    builder.setMessage("Please fill in all the fields!")
                }
                builder.setPositiveButton("Ok"){dialog, which ->

                }
                val alert = builder.create()
                alert.show()

            }

            }
        // Inflate the layout for this fragment
        nav.setOnNavigationItemReselectedListener {
            item->
            when(item.itemId){
                R.id.backHome->{
                    if(findNavController().currentDestination?.id == R.id.signUpTeacher){
                        findNavController().navigate(R.id.mainPage)
                    }
                }
            }
        }

        return binding.root
    }
    private fun saveTeacher() {
        val firstName = fname.text.toString().trim()
        val lastName = lname.text.toString().trim()
        val emailA = email.text.toString().trim()
        val pass = password.text.toString().trim()


        if(isEmailValid(emailA)) {


            mAuth.createUserWithEmailAndPassword(emailA, pass)
                .addOnCompleteListener { task: Task<AuthResult> ->
                    if (task.isSuccessful) {
                        val ref = FirebaseDatabase.getInstance().getReference("Teacher")
                        teacherId = ref.push().key!!
                        val teacher = Teacher(0,firstName,lastName, emailA)
                        ref.child(teacherId).setValue(teacher)
                        Toast.makeText(context, "register successfully", Toast.LENGTH_LONG).show()


                    } else {

                        Toast.makeText(context, "register unsuccessfully", Toast.LENGTH_LONG).show()

                    }
                }
        }
        else{
            val builder = AlertDialog.Builder(context)
            builder.setTitle("ERROR")
            builder.setMessage("Please Enter a valid email!")
            builder.setPositiveButton("Ok"){dialog, which ->

            }
            val alert = builder.create()
            alert.show()

        }

    }


    val EMAIL_REGEX = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
    fun isEmailValid(email: String): Boolean {
        return EMAIL_REGEX.toRegex().matches(email)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment signUpTeacher.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignUpTeacher().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
