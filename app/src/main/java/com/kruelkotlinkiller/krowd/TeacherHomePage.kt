package com.kruelkotlinkiller.krowd

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.kruelkotlinkiller.krowd.databinding.ActivityMainBinding
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
class TeacherHomePage : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var binding : FragmentTeacherHomePageBinding
    private lateinit var createClassBtn : Button
    private lateinit var name : TextView
   // private lateinit var listView : ListView
    private lateinit var recyclerView: RecyclerView
    private lateinit var databaseReference : DatabaseReference
    private var temp : String?=null
    var arrayList = ArrayList<Course>()
    private lateinit var btnNav : BottomNavigationView

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
        btnNav = binding.btnnavteacher
        name = binding.nameOfTeacher
        recyclerView = binding.recyclerView
      //  binding.recyclerView.setDivider(R.drawable.recycler_view_divider)
//        listView = binding.listView
        val model = ViewModelProviders.of(activity!!).get(GeneralCommunicator::class.java)
        model.message.observe(this,object: Observer<Any> {
            override fun onChanged(t: Any?) {
                temp = t!!.toString()
                Log.d("hi",temp.toString())

                val ordersRef = FirebaseDatabase.getInstance().getReference("Teacher").orderByChild("email").equalTo(temp)
                val valueEventListener = object : ValueEventListener{
                    override fun onDataChange(p0: DataSnapshot) {
//                        Log.d("The p0 is", p0.toString())
                        for(ds in p0.children){

                            val firstName = ds.child("firstName").getValue(String::class.java)
                            val lastName = ds.child("lastName").getValue(String::class.java)
                            val fullName = firstName + " " + lastName
                            name.text = fullName
                        }
                        databaseReference = FirebaseDatabase.getInstance().getReference("Course")
                        databaseReference.orderByChild("professorName").equalTo(name.text.toString()).addListenerForSingleValueEvent(object: ValueEventListener{
                            override fun onCancelled(p0: DatabaseError) {
                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            }

                            override fun onDataChange(p0: DataSnapshot) {
//                                Log.d("The child is :" , p0.toString())
                                if(p0.exists()){
                                    arrayList.clear()
                                    for(e in p0.children){
                                        Log.d("The course is ", e.getValue().toString())
                                        val course = e.getValue(Course::class.java)
                                        arrayList.add(course!!)
                                }

                                }
                                val adapter = CourseAdapter(arrayList,"Teacher")
                                recyclerView.adapter = adapter

                                }



//                                val adapter =
//                                    ArrayAdapter(context!!, android.R.layout.simple_list_item_1, arrayList)
//                                listView.adapter = adapter


                        })
                    }
                    override fun onCancelled(p0: DatabaseError) {}
                }
                ordersRef.addListenerForSingleValueEvent(valueEventListener)



                binding.recyclerView.addOnItemTouchListener(RecyclerItemClickListenr(context!!, binding.recyclerView, object : RecyclerItemClickListenr.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        if(findNavController().currentDestination?.id == R.id.teacherHomePage) {
                            model.setMsgCommunicator(name.text.toString())
                            // here we pass the id of the course to the manage class
                            model.setIdCommunicator(CourseAdapter(arrayList,"Teacher").getID(position))
                            Log.d("I clicked ", CourseAdapter(arrayList,"Teacher").getID(position))
                            val myFragment = ManageClasses()
                            val fragmentTransaction = fragmentManager!!.beginTransaction()
                            fragmentTransaction.replace(R.id.myNavHostFragment, myFragment)
                            var bundle: Bundle = bundleOf("courseId" to CourseAdapter(arrayList,"Teacher").getID(position))
                            view.findNavController()
                                .navigate(R.id.action_teacherHomePage_to_manageClasses3, bundle)
                        }
                    }

                    override fun onItemLongClick(view: View?, position: Int) {}
                }))





            }
        })


        btnNav.setOnNavigationItemReselectedListener { item->
            when(item.itemId){
                R.id.manageclass ->{
                    if(findNavController().currentDestination?.id == R.id.teacherHomePage) {
                        model.setMsgCommunicator(name.text.toString())
                        model.setIdCommunicator("-1.0")
                        val myFragment = ManageClasses()
                        val fragmentTransaction = fragmentManager!!.beginTransaction()
                        fragmentTransaction.replace(R.id.myNavHostFragment, myFragment)
                        fragmentTransaction.addToBackStack(null)
                        fragmentTransaction.commit()
                        findNavController().navigate(R.id.action_teacherHomePage_to_manageClasses3)
                    }
                }
                R.id.manageAccount->{
                    if(findNavController().currentDestination?.id == R.id.teacherHomePage){
                        findNavController().navigate(R.id.action_teacherHomePage_to_teacherAccountManagement)
                    }
                }
            }
        }


        //setHasOptionsMenu(true)

        //sends user back to the log in page if he/she is logged out
        val user = FirebaseAuth.getInstance().currentUser
        if(user==null){
            if(findNavController().currentDestination?.id == R.id.teacherHomePage) {
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
        return binding.root
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//
//        super.onCreateOptionsMenu(menu!!, inflater!!)
//        inflater?.inflate(R.menu.menu, menu)
//    }
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when(item.itemId){
//            R.id.logout -> {
//                FirebaseAuth.getInstance().signOut()
//                findNavController().navigate(R.id.mainPage)
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }



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
            TeacherHomePage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}

