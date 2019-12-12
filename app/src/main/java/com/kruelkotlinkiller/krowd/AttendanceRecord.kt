package com.kruelkotlinkiller.krowd

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kruelkotlinkiller.krowd.databinding.FragmentAttendanceRecordBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AttendanceRecord.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [AttendanceRecord.newInstance] factory method to
 * create an instance of this fragment.
 */
class AttendanceRecord : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var binding : FragmentAttendanceRecordBinding
    private lateinit var recyclerView : RecyclerView
    private lateinit var btn : Button
    private lateinit var courseName : TextView
    private var arr = ArrayList<Record>()
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
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_attendance_record, container, false)
        recyclerView = binding.recyclerView2
        courseName = binding.textView17
        btnNav = binding.nv2

        val ref1 = FirebaseDatabase.getInstance().getReference("Course")
        ref1.orderByChild("courseId").equalTo(arguments!!.getString("courseId"))
            .addValueEventListener(object:ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot) {
                    for(e in p0.children){
                        val course = e.getValue(Course::class.java)
                        courseName.text = course!!.courseName
                    }
                }
            })

        val ref = FirebaseDatabase.getInstance().getReference("Record")
        ref.orderByChild("courseId").equalTo(arguments!!.getString("courseId"))
            .addValueEventListener(
                object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(p0: DataSnapshot) {
                        arr.clear()
                        for(e in p0.children){
                            Log.d("record is ", e.getValue().toString())
                            val record = e.getValue(Record :: class.java)
                            Log.d("lol", record!!.date)
                            Log.d("lol111", record!!.courseId)
                            arr.add(record!!)
                        }
                        val adapter = RecordAdapter(arr, context!!)
                        recyclerView.adapter = adapter
                    }
                }
            )
        btnNav.setOnNavigationItemReselectedListener { item->
            when(item.itemId){
                R.id.backHome->{
                    if(findNavController().currentDestination?.id == R.id.attendanceRecord) {
                        val bundle:Bundle = bundleOf("courseId" to arguments!!.getString("courseId"))
                        val model = ViewModelProviders.of(activity!!).get(GeneralCommunicator::class.java)
                        model.setIdCommunicator(arguments!!.getString("courseId").toString())
                        val myFragment = ManageClasses()
                        val fragmentTransaction = fragmentManager!!.beginTransaction()
                        fragmentTransaction.replace(R.id.myNavHostFragment, myFragment)
                        fragmentTransaction.commit()
                        findNavController()
                            .navigate(R.id.action_attendanceRecord_to_manageClasses, bundle)
                    }
                }
            }
        }
//        btn.setOnClickListener {view:View->
//
//        }
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
         * @return A new instance of fragment AttendanceRecord.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AttendanceRecord().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
