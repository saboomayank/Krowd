package com.kruelkotlinkiller.krowd

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import co.metalab.asyncawait.async
import co.metalab.asyncawait.await
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kruelkotlinkiller.krowd.databinding.FragmentStudentEnrollBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [StudentEnroll.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [StudentEnroll.newInstance] factory method to
 * create an instance of this fragment.
 */
class StudentEnroll : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var binding : FragmentStudentEnrollBinding
    private lateinit var addCourseBtn : Button
    private lateinit var backBtn : Button
    private lateinit var btnNav : BottomNavigationView
//    private lateinit var courseInput : EditText
    private var temp : String?=null
    private lateinit var spinnerList : Spinner
    private var arr = ArrayList<String>()

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
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_student_enroll,container,false)
        addCourseBtn = binding.addClassBtn
        spinnerList = binding.courseInput
        btnNav = binding.bottomNavigationView
      //  val model = ViewModelProviders.of(activity!!).get(GeneralCommunicator::class.java)
//        model.message.observe(this,object: Observer<Any> {
//            override fun onChanged(t: Any?) {
//                temp = t!!.toString()
//              //  addClassFun(temp!!)
//                Log.d("temp is ", temp)
                val courseRef = FirebaseDatabase.getInstance().getReference("Course")
                courseRef.addValueEventListener(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(p0: DataSnapshot) {
                        arr.clear()

                        for(e in p0.children){
                            val course = e.getValue(Course::class.java)
                            arr.add(course!!.courseName + " -- " + course!!.professorName)
                            val studentCourseRef = FirebaseDatabase.getInstance().getReference("Student")
                            val user = FirebaseAuth.getInstance().currentUser
                            Log.d("user email is = ", user!!.email)
                            studentCourseRef.orderByChild("email").equalTo(user?.email).addValueEventListener(
                                object : ValueEventListener{
                                    override fun onCancelled(p1: DatabaseError) {}
                                    override fun onDataChange(p1: DataSnapshot) {
                                        for(e1 in p1.children){
                                            val studentCourseList = e1.getValue(Student::class.java)!!.courseId.values
                                            if(studentCourseList.contains(course!!.courseId)){
                                                arr.remove(course!!.courseName + " -- " + course!!.professorName)

                                            }

                                        }
                                    for(i in arr){
                                        Log.d("element ", i)
                                    }
                                        val array_adapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, arr)
                                        array_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                        spinnerList.adapter = array_adapter
                                    }
                                })
                        }


                    }
                })


        spinnerList.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val ref = FirebaseDatabase.getInstance().getReference("Course")
                val selected = arr[position].split(" -- ")
                //Toast.makeText(context!!,arr[position].toString(),Toast.LENGTH_LONG).show()

               // Toast.makeText(context!!,selected[0].toString(),Toast.LENGTH_LONG).show()
                ref.orderByChild("courseName").equalTo(selected[0]).addValueEventListener(
                    object : ValueEventListener{
                        override fun onDataChange(p0: DataSnapshot) {
                            for(e in p0.children){
                                val f = e.getValue(Course::class.java)!!.courseId
                                addClassFun(f)
                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {}
                    }
                )
            }


        }

        if(arguments?.size() != 0){
            Log.i("misael", arguments?.getString("name").toString())
        }

        btnNav.setOnNavigationItemReselectedListener { item ->
            when(item.itemId){
                R.id.backHome ->{
                    if(findNavController().currentDestination?.id == R.id.studentEnroll){
                        val model = ViewModelProviders.of(activity!!).get(GeneralCommunicator::class.java)
                        val user = FirebaseAuth.getInstance().currentUser
                        model.setMsgCommunicator(user?.email!!)
                        val myFragment = StudentHomePage()
                        val fragmentTransaction = fragmentManager!!.beginTransaction()
                        fragmentTransaction.replace(R.id.myNavHostFragment,myFragment)
                        fragmentTransaction.commit()
                        findNavController().navigate(R.id.action_studentEnroll_to_studentHomePage)
                    }
                }
            }
        }


        return binding.root
    }

    private fun addClassFun(courseInput: String){
        addCourseBtn.setOnClickListener {view:View->
            if(findNavController().currentDestination?.id==R.id.studentEnroll) {

                val model = ViewModelProviders.of(activity!!).get(GeneralCommunicator::class.java)
                val user = FirebaseAuth.getInstance().currentUser
                model.setMsgCommunicator(user?.email!!)
                val databaseReference = FirebaseDatabase.getInstance().reference
                databaseReference.child("Student").child(arguments!!.getString("key").toString()).child("courseId").push()
                    .setValue(courseInput)

             //  Toast.makeText(context, "Sucessfully add class", Toast.LENGTH_LONG).show()
                val myFragment = StudentHomePage()
                val fragmentTransaction = fragmentManager!!.beginTransaction()
                fragmentTransaction.replace(R.id.myNavHostFragment,myFragment)
                view.findNavController().navigate(R.id.action_studentEnroll_to_studentHomePage)
            }
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
         * @return A new instance of fragment StudentEnroll.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StudentEnroll().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}