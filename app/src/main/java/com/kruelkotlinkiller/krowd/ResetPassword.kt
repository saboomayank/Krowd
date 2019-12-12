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
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.kruelkotlinkiller.krowd.databinding.FragmentResetPasswordBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ResetPassword.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ResetPassword.newInstance] factory method to
 * create an instance of this fragment.
 */
class ResetPassword : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var binding : FragmentResetPasswordBinding
    private lateinit var email : EditText
    private lateinit var sendBtn : Button
    private lateinit var nav : BottomNavigationView

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
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_reset_password,container, false)
        email = binding.enteredEmail
        sendBtn = binding.submit
        nav = binding.nav6
        Log.d("argmument is ", arguments?.getString("Type").toString())
        sendBtn.setOnClickListener {  reset() }
        nav.setOnNavigationItemReselectedListener { item->
            when(item.itemId){
                R.id.backHome->{
                    if(arguments?.getString("Type").toString() == "Student") {
                        findNavController().navigate(R.id.action_resetPassword_to_logIn)
                    }
                    if(arguments?.getString("Type").toString() == "Teacher"){
                        findNavController().navigate(R.id.action_resetPassword_to_teacher_login)
                    }
                }
            }
        }
        return binding.root
    }

    // TODO: Rename method, update argument and hook method into UI event
    private fun reset(){
        val emailA = email.text.toString().trim()

            val mAuth = FirebaseAuth.getInstance()
            mAuth!!.sendPasswordResetEmail(emailA)
                .addOnCompleteListener{task ->
                    if(task.isSuccessful){
                        if(arguments?.getString("Type").toString() == "Student") {
                            findNavController().navigate(R.id.action_resetPassword_to_logIn)
                        }
                        if(arguments?.getString("Type").toString() == "Teacher"){
                            findNavController().navigate(R.id.action_resetPassword_to_teacher_login)
                        }

                        Toast.makeText(context,"email successful sent",Toast.LENGTH_LONG).show()

                    }
                    else{
                        Toast.makeText(context,"email not sent",Toast.LENGTH_LONG).show()
                    }

                }

    }


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
         * @return A new instance of fragment ResetPassword.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ResetPassword().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
