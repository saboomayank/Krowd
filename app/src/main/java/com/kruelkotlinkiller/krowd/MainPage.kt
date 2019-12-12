package com.kruelkotlinkiller.krowd

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kruelkotlinkiller.krowd.databinding.FragmentMainPageBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [mainPage.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [mainPage.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainPage : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var binding: FragmentMainPageBinding
    lateinit var logInBtn : Button
    lateinit var signUpBtn : Button
    lateinit var sw : SwitchCompat
    lateinit var textBelow : TextView
    lateinit var progressBar : ProgressBar
    lateinit var im : ImageView
    lateinit var afterAnimation : ConstraintLayout
    private lateinit var model : GeneralCommunicator

    private var isCheck : Boolean? = null
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
        // Inflate the layout for this fragment
        binding =  DataBindingUtil.inflate(inflater,R.layout.fragment_main_page, container, false)
        signUpBtn = binding.button
        logInBtn = binding.button1
        sw = binding.switch1



        afterAnimation = binding.afterAnimation
        sw?.setOnCheckedChangeListener({ _, isChecked ->
            val msg = if (isChecked) "Teacher" else "Student"
            sw.text = msg
            if(isChecked){
                isCheck = true
            }
            else{
                isCheck = false
            }
        })

        signUpBtn.setOnClickListener{ view : View ->

                if(isCheck == true){
                    if(findNavController().currentDestination?.id == R.id.mainPage) {
                        view.findNavController().navigate(R.id.signUpTeacher)
                    }
                }
                else{
                   if(findNavController().currentDestination?.id == R.id.mainPage) {
                        view.findNavController().navigate(R.id.signUpStudent)
                    }
                }



        }
        logInBtn.setOnClickListener {view:View->

                if(isCheck == true) {
                   if(findNavController().currentDestination?.id == R.id.mainPage) {
                        view.findNavController().navigate(R.id.teacher_login)
                    }
                    }
                else{
                    if(findNavController().currentDestination?.id == R.id.mainPage) {
                        view.findNavController().navigate(R.id.logIn)
                    }
                }

        }



        val user = FirebaseAuth.getInstance().currentUser
//        val fragmentTransaction = fragmentManager?.beginTransaction()
        if(user!=null){
            val refStudent = FirebaseDatabase.getInstance().getReference("Student")
            val refTeacher = FirebaseDatabase.getInstance().getReference("Teacher")
            refStudent.orderByChild("email").equalTo(user.email).addValueEventListener(object:
                ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists()){
                        if(findNavController().currentDestination?.id == R.id.mainPage){
                           model = ViewModelProviders.of(activity!!).get(GeneralCommunicator::class.java)
                            model.setMsgCommunicator(user.email!!)
                            val myFragment = StudentHomePage()
                            val fragmentTransaction = fragmentManager!!.beginTransaction()
                            fragmentTransaction.replace(R.id.myNavHostFragment,myFragment)
                            fragmentTransaction.addToBackStack(null)
                            fragmentTransaction.commit()
                            findNavController().navigate(R.id.action_mainPage_to_studentHomePage)
                    }}
                }
                     })
            refTeacher.orderByChild("email").equalTo(user.email).addValueEventListener(
                object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {}

                    override fun onDataChange(p0: DataSnapshot) {
                        if(p0.exists()) {
                            if (findNavController().currentDestination?.id == R.id.mainPage) {
                                model = ViewModelProviders.of(activity!!)
                                    .get(GeneralCommunicator::class.java)
                                model.setMsgCommunicator(user.email!!)
                                val myFragment = TeacherHomePage()
                                val fragmentTransaction = fragmentManager!!.beginTransaction()
                                fragmentTransaction.replace(R.id.myNavHostFragment, myFragment)
                                fragmentTransaction.addToBackStack(null)
                                fragmentTransaction.commit()
                                findNavController().navigate(R.id.action_mainPage_to_teacherHomePage)
                            }
                        }
                    }
                }
            )

        }else{

        }



        return binding.root
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
         * @return A new instance of fragment mainPage.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainPage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
