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
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kruelkotlinkiller.krowd.databinding.FragmentManageClassesBinding
import kotlinx.android.synthetic.main.fragment_manage_classes.*
import android.os.Build



// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ManageClasses.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ManageClasses.newInstance] factory method to
 * create an instance of this fragment.
 */
class ManageClasses : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var binding : FragmentManageClassesBinding
    private lateinit var addClass : Button
    private lateinit var courseName : EditText
    private lateinit var courseId : EditText
    private lateinit var addBtn : Button
    private lateinit var backBtn : Button
    private lateinit var courseDescription : EditText
    private lateinit var courseNameToDisplay : TextView
    private lateinit var courseDescriptionToDisplay : TextView
    private var courseDescriptionString : String?=null
    private var courseNameString : String?= null
    private var courseIdString : String?=null
    private var professorName : String?=null
    private var id : Double?=null

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
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_manage_classes, container, false )
        addClass = binding.button4
        courseName = binding.editText
        courseId = binding.editText2
        addBtn = binding.button5
        courseDescription = binding.editText6
        backBtn = binding.button6
        courseNameToDisplay = binding.textView9
        courseDescriptionToDisplay = binding.textView10
        addClass.setOnClickListener { form.visibility = View.VISIBLE
            courseInfo.visibility = View.GONE

        }
        courseNameString = courseName.text.toString()
        courseIdString = courseId.text.toString()
        courseDescriptionString = courseDescription.text.toString()
        val model = ViewModelProviders.of(activity!!).get(TeacherNameCommunicator::class.java)

        model.message.observe(this,object: Observer<Any> {
            override fun onChanged(t: Any?) {

                    professorName = t!!.toString()
                    Log.d("Professor name is " , professorName)

              }})

        model.id.observe(this,object : Observer<Any>{
            override fun onChanged(t: Any?) {


                    id = t!!.toString().toDouble()
                    Log.d("the id is is is ", id.toString())
               if(id!=-1.0) {
                   val ordersRef =
                       FirebaseDatabase.getInstance().getReference("Course")
                           .orderByChild("courseId")
                           .equalTo(id!!)
                   val valueEventListener = object : ValueEventListener {
                       override fun onDataChange(p0: DataSnapshot) {
                           for (ds in p0.children) {
                               Log.d("loooooooool", ds.toString())
                               val courseName = ds.child("courseName").getValue(String::class.java)
                               val courseDescription =
                                   ds.child("courseDescription").getValue(String::class.java)

                               courseNameToDisplay.text = courseName
                               courseDescriptionToDisplay.text = courseDescription
                           }
                       }

                       override fun onCancelled(p0: DatabaseError) {}
                   }
                   ordersRef.addListenerForSingleValueEvent(valueEventListener)
               }
            }

        }



        )
        addBtn.setOnClickListener {view : View->
            form.visibility = View.GONE
            courseInfo.visibility = View.VISIBLE
            val ref = FirebaseDatabase.getInstance().getReference("Course")
            val cId = ref.push().key!!
            val course = Course(courseName.text.toString(),courseId.text.toString().toDouble(),courseDescription.text.toString(),professorName!!)
            ref.child(cId).setValue(course)
            Toast.makeText(context, "add class successfully", Toast.LENGTH_LONG).show()
//            val ft = fragmentManager!!.beginTransaction()
//            if (Build.VERSION.SDK_INT >= 26) {
//                ft.setReorderingAllowed(false)
//            }
//            ft.detach(this).attach(this).commit()


        }
        backBtn.setOnClickListener { view : View ->
            val user = FirebaseAuth.getInstance().currentUser
            model.setMsgCommunicator(user!!.email.toString())
            val myFragment = teacherHomePage()
            val fragmentTransaction = fragmentManager!!.beginTransaction()
            fragmentTransaction.replace(R.id.myNavHostFragment,myFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
            view.findNavController().navigate(R.id.teacherHomePage)

        }


            Log.d("Hellooooooo",id.toString())






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
         * @return A new instance of fragment ManageClasses.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ManageClasses().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
