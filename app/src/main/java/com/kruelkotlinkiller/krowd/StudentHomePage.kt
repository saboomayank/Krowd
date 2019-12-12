package com.kruelkotlinkiller.krowd

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.kruelkotlinkiller.krowd.databinding.ActivityMainBinding
import com.kruelkotlinkiller.krowd.databinding.FragmentStudentHomePageBinding
import android.os.Build
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [studentHomePage.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [studentHomePage.newInstance] factory method to
 * create an instance of this fragment.
 */
class StudentHomePage : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var binding : FragmentStudentHomePageBinding
    private lateinit var addClassBtn : Button
    private lateinit var name : TextView
    private var temp : String?= null
    private lateinit var courseList : RecyclerView
    //private lateinit var databaseReference : DatabaseReference
    private var arrayList = ArrayList<Course>()
    private lateinit var btmNav : BottomNavigationView

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
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_student_home_page,container,false)
        name = binding.nameOfStudent
//        addClassBtn = binding.button3
        courseList = binding.courseList
        btmNav = binding.bottomNav
        val model = ViewModelProviders.of(activity!!).get(GeneralCommunicator::class.java)
        model.message.observe(this,object: Observer<Any> {
            override fun onChanged(t: Any?) {
                temp = t!!.toString()
                var key = ""
                val ref = FirebaseDatabase.getInstance().reference
                val ordersRef = ref.child("Student").orderByChild("email").equalTo(temp)
                val valueEventListener = object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        if(p0.exists()) {
                            for (ds in p0.children) {
                                val nameTemp =
                                    ds.child("firstName").getValue(String::class.java) + " " + ds.child(
                                        "lastName"
                                    ).getValue(String::class.java)

                                key = ds.key!!
                                //  Log.d("zaccccc",key)
                                name.text = nameTemp
                            }
                            sendKeyToEnrollment(key)


                            val courseRef = FirebaseDatabase.getInstance().getReference("Course")
                            val databaseReference = FirebaseDatabase.getInstance().getReference("Student")
                            ordersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {}
                                override fun onDataChange(p0: DataSnapshot) {
                                    for (e in p0.children) {
                                        arrayList.clear()
                                        Log.d("jimmm", e.key)
                                                            databaseReference.child(e.key!!)
                                                                .child("courseId")
                                                                .addValueEventListener(
                                                                    object : ValueEventListener {
                                                                        override fun onDataChange(p2: DataSnapshot) {

                                                                            for (e2 in p2.children) {
                                                                                Log.d("e2",e2.getValue().toString())
                                                                                courseRef.orderByChild(
                                                                                    "courseId"
                                                                                ).equalTo(
                                                                                    e2.getValue(
                                                                                        String::class.java)
                                                                                )
                                                                                    .addListenerForSingleValueEvent(
                                                                                        object :
                                                                                            ValueEventListener {
                                                                                            override fun onCancelled(
                                                                                                p3: DatabaseError
                                                                                            ) {
                                                                                            }

                                                                                            override fun onDataChange(
                                                                                                p3: DataSnapshot
                                                                                            ) {

                                                                                                for (e3 in p3.children) {

                                                                                                    val course =
                                                                                                        e3.getValue(
                                                                                                            Course::class.java
                                                                                                        )
                                                                                                    arrayList.add(
                                                                                                        course!!
                                                                                                    )
                                                                                                }

                                                                                                val adapter =
                                                                                                    CourseAdapter(
                                                                                                        arrayList,"Student"
                                                                                                    )
                                                                                                courseList.adapter =
                                                                                                    adapter
                                                                                            }

                                                                                        })

                                                                            }
                                                                        }

                                                                        override fun onCancelled(p2: DatabaseError) {}
                                                                    }
                                                                )
                                                        }
                                                    }
                                })
                            }


                    }
                    override fun onCancelled(p0: DatabaseError) {
                    }

                }
                ordersRef.addListenerForSingleValueEvent(valueEventListener)
            }
        })

        courseList.addOnItemTouchListener(RecyclerItemClickListenr(context!!, courseList, object : RecyclerItemClickListenr.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
            if(findNavController().currentDestination?.id == R.id.studentHomePage) {
                model.setIdCommunicator(CourseAdapter(arrayList,"Student").getID(position))
                Log.d("I clicked ", CourseAdapter(arrayList,"Student").getID(position))
                model.setNameCommunicator(name.text.toString())
                val myFragment = AttendancePage()
                val fragmentTransaction = fragmentManager!!.beginTransaction()
                fragmentTransaction.replace(R.id.myNavHostFragment, myFragment)
                fragmentTransaction.commit()
                view.findNavController().navigate(R.id.action_studentHomePage_to_attendancePage)
            }
            }
            override fun onItemLongClick(view: View?, position: Int) {}
        }))





         //   setHasOptionsMenu(true)


        //sends user back to the log in page if he/she is logged out
        val user = FirebaseAuth.getInstance().currentUser
        if(user==null){
            if(findNavController().currentDestination?.id == R.id.studentHomePage) {
                findNavController().navigate(R.id.mainPage)
            }
        }


        val text = activity!!.findViewById<TextView>(R.id.textView20)
        val au = FirebaseAuth.getInstance().currentUser
        text.text = au!!.email
        val navigationView = activity!!.findViewById<NavigationView>(R.id.navView)
        val drawer = activity!!.findViewById<DrawerLayout>(R.id.drawerLayout)

        navigationView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.logout -> {
                    FirebaseAuth.getInstance().signOut()
                    text.text = "Welcome User"
                    findNavController().navigate(R.id.mainPage)
                    drawer.closeDrawers()
                }
                R.id.about ->{
                    val i = Intent(activity, AboutActivty::class.java)
                    drawer.closeDrawers()
                    startActivity(i)
                }



            }
            false
        }

        activity!!.actionBar?.setDisplayHomeAsUpEnabled(false)
        activity!!.actionBar?.setHomeButtonEnabled(false)

        return binding.root
    }
    private fun sendKeyToEnrollment(str : String){
        btmNav.setOnNavigationItemReselectedListener {item->
            when(item.itemId) {
                R.id.add -> {
                    if (findNavController().currentDestination?.id == R.id.studentHomePage) {
                        var bundle: Bundle = bundleOf("key" to str)
                        findNavController().navigate(
                            R.id.action_studentHomePage_to_studentEnroll,
                            bundle
                        )
                    }
                }
                R.id.manageAccount ->{
                    if(findNavController().currentDestination?.id == R.id.studentHomePage){
                        findNavController().navigate(R.id.action_studentHomePage_to_studentAccountManagement2)
                    }
                }
            }
        }



//        addClassBtn.setOnClickListener {view:View->
//            if(findNavController().currentDestination?.id == R.id.studentHomePage) {
////                val model = ViewModelProviders.of(activity!!).get(GeneralCommunicator::class.java)
////                model.setMsgCommunicator(str)
////                val myFragment = StudentEnroll()
////                val fragmentTransaction = fragmentManager!!.beginTransaction()
//                var bundle: Bundle = bundleOf("key" to str)
////                fragmentTransaction.replace(R.id.myNavHostFragment, myFragment)
////                fragmentTransaction.addToBackStack(null)
////                fragmentTransaction.commit()
//                view.findNavController().navigate(R.id.action_studentHomePage_to_studentEnroll, bundle)
//            }
//        }
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
         * @return A new instance of fragment studentHomePage.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StudentHomePage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
