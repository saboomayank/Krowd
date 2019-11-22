package com.kruelkotlinkiller.krowd

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.kruelkotlinkiller.krowd.databinding.FragmentTeacherHomePageBinding


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [teacherHomePage.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [teacherHomePage.newInstance] factory method to
 * create an instance of this fragment.
 */
class teacherHomePage : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var binding : FragmentTeacherHomePageBinding
    private lateinit var createClassBtn : Button
    private lateinit var name : TextView
    private lateinit var listView : ListView
    private lateinit var databaseReference : DatabaseReference
    private var temp : String?=null
    var arrayList = ArrayList<String>()

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
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_teacher_home_page, container, false)
        createClassBtn = binding.button3
        name = binding.nameOfTeacher
        listView = binding.listView
        val model = ViewModelProviders.of(activity!!).get(TeacherNameCommunicator::class.java)
        model.message.observe(this,object: Observer<Any> {
            override fun onChanged(t: Any?) {
                temp = t!!.toString()
                Log.d("hi",temp.toString())

                val ordersRef = FirebaseDatabase.getInstance().getReference("Teacher").orderByChild("email").equalTo(temp)
                val valueEventListener = object : ValueEventListener{
                    override fun onDataChange(p0: DataSnapshot) {
                        for(ds in p0.children){
                            Log.d("loooooooool",ds.toString())
                            val firstName = ds.child("firstName").getValue(String::class.java)
                            val lastName = ds.child("lastName").getValue(String::class.java)
                            val fullName = firstName + " " + lastName
                            name.text = fullName
                        }
                    }
                    override fun onCancelled(p0: DatabaseError) {}
                }
                ordersRef.addListenerForSingleValueEvent(valueEventListener)

                 databaseReference = FirebaseDatabase.getInstance().getReference("Course")
                 databaseReference.addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists()){
                        arrayList.clear()
                            for(e in p0.children){
//                                arrayList.clear()
                                val course = e.getValue(Course::class.java)
                                val courseName = course?.courseName
                                val courseId = course?.courseId
                                println(courseName.toString())
                                arrayList.add(courseName!! + " - " + courseId.toString())
                            }


//                                Log.d("hi", courseList.size.toString())


                        }
                    else{
                        arrayList.add("You do not have any available course.")

                    }
                        val adapter =
                            ArrayAdapter(context!!, android.R.layout.simple_list_item_1, arrayList)
                        listView.adapter = adapter


//                        for(i in 0..listView.adapter.count){
//                            if("You do not have any available course."==(listView.adapter.getItem(i))){
//                                createClassBtn.isClickable=true
//                                createClassBtn.isEnabled=true
//                            }else{
//                                createClassBtn.isClickable= false
//                                createClassBtn.isEnabled=false
//                            }
//                        }
//
                   }
                })


                val delimiter = " - "
                listView.setOnItemClickListener{parent:AdapterView<*>?, view:View,position:Int, id:Long->
                    if(!arrayList.contains("You do not have any available course.")){
                        Log.d("My id is that: ", arrayList[position].split(delimiter)[1])

                        model.setMsgCommunicator( name.text.toString())
                        model.setIdCommunicator(arrayList[position].split(delimiter)[1].toDouble())
                        val myFragment = ManageClasses()
                        val fragmentTransaction = fragmentManager!!.beginTransaction()
                        fragmentTransaction.replace(R.id.myNavHostFragment,myFragment)
                        fragmentTransaction.addToBackStack(null)
                        fragmentTransaction.commit()
                        view.findNavController().navigate(R.id.action_teacherHomePage_to_manageClasses3)

                    }
                }





            }
        })




        createClassBtn.setOnClickListener {view : View ->
            model.setMsgCommunicator( name.text.toString())
            model.setIdCommunicator(-1.0)
            val myFragment = ManageClasses()
            val fragmentTransaction = fragmentManager!!.beginTransaction()
            fragmentTransaction.replace(R.id.myNavHostFragment,myFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
            view.findNavController().navigate(R.id.action_teacherHomePage_to_manageClasses3)
        }

        setHasOptionsMenu(true)

        //sends user back to the log in page if he/she is logged out
        val user = FirebaseAuth.getInstance().currentUser
        if(user==null){
            findNavController().navigate(R.id.mainPage)
        }
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu!!, inflater!!)
        inflater?.inflate(R.menu.menu, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                findNavController().navigate(R.id.mainPage)
            }
        }
        return super.onOptionsItemSelected(item)
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
         * @return A new instance of fragment teacherHomePage.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            teacherHomePage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}
