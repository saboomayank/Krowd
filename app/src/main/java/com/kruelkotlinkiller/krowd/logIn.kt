package com.kruelkotlinkiller.krowd

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.kruelkotlinkiller.krowd.databinding.FragmentLogInBinding
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task
import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import android.R.attr.password
import android.R.attr.password
import androidx.navigation.findNavController


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [logIn.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [logIn.newInstance] factory method to
 * create an instance of this fragment.
 */
class logIn : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var binding : FragmentLogInBinding
    lateinit var email : EditText
    lateinit var password : EditText
    lateinit var logInBtn : Button
    lateinit var mAuth : FirebaseAuth

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

        binding =  DataBindingUtil.inflate(inflater,R.layout.fragment_log_in, container, false)
        email = binding.simpleEditText
        password = binding.simpleEditText3
        logInBtn = binding.button
        mAuth = FirebaseAuth.getInstance()
        logInBtn.setOnClickListener {

            checkUser()

        }

        // Inflate the layout for this fragment
        return binding.root
    }

    fun checkUser(){
        val refStudent = FirebaseDatabase.getInstance().getReference("Student")
        val refTeacher = FirebaseDatabase.getInstance().getReference("Teacher")
        val emailA = email.text.toString().trim()
        val pass = password.text.toString().trim()

        if (emailA.isNotEmpty() && pass.isNotEmpty()) {
            this.mAuth.signInWithEmailAndPassword(emailA, pass).addOnCompleteListener { task: Task<AuthResult> ->
             if(task.isSuccessful) {
                 refStudent.orderByChild("email").equalTo(emailA).addValueEventListener(object:ValueEventListener{
                     override fun onDataChange(snapshot: DataSnapshot) {
                       if(snapshot.exists()){
                           logInBtn.setOnClickListener{ view : View ->
                               view.findNavController().navigate(R.id.action_logIn_to_studentHomePage)
                           }
                       }
                     }

                     override fun onCancelled(error: DatabaseError) {}

                 })
                 refTeacher.orderByChild("email").equalTo(emailA).addValueEventListener(object : ValueEventListener{
                     override fun onDataChange(p0: DataSnapshot) {
                         if(p0.exists()){
                             logInBtn.setOnClickListener{ view : View ->
                                 view.findNavController().navigate(R.id.action_logIn_to_teacherHomePage)
                             }
                         }
                     }

                     override fun onCancelled(p0: DatabaseError) {
                         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                     }

                 })

             }else{
                 Toast.makeText(context, "Incorrect credentials", Toast.LENGTH_SHORT).show()
             }
            }

        }else {
            Toast.makeText(context, "Please fill up the Credentials :|", Toast.LENGTH_SHORT).show()
        }


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
         * @return A new instance of fragment logIn.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            logIn().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
