package com.kruelkotlinkiller.krowd

import android.app.AlertDialog
import android.content.Context
import android.location.Location
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
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kruelkotlinkiller.krowd.databinding.FragmentAttendancePageBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AttendancePage.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [AttendancePage.newInstance] factory method to
 * create an instance of this fragment.
 */
class AttendancePage : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var binding : FragmentAttendancePageBinding
    private lateinit var courseName : TextView
    private lateinit var courseDesc : TextView
    private lateinit var professorName : TextView
    private lateinit var attendanceBtn : Button
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private val arr = ArrayList<String>()
    private lateinit var navBtn : BottomNavigationView



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
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_attendance_page,container,false)
        courseName = binding.courseName
        courseDesc = binding.courseDescrp
        attendanceBtn = binding.attendance
        professorName = binding.professorName
        navBtn = binding.btnNav3
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context!!)

        val model = ViewModelProviders.of(activity!!).get(GeneralCommunicator::class.java)
        model.id.observe(this,object: Observer<Any> {
            override fun onChanged(t: Any?) {
                val id = t.toString()!!
               // Log.d("Hey the id " , id)
                val courseRef = FirebaseDatabase.getInstance().getReference("Course")
                courseRef.orderByChild("courseId").equalTo(id!!).addListenerForSingleValueEvent(object:ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(p0: DataSnapshot) {
                        for(e in p0.children){
                            val course = e.getValue(Course::class.java)
                            courseName.text = course?.courseName
                            courseDesc.text = course?.courseDescription
                            professorName.text = course?.professorName
                        }
                    }

                })
                detectAttendanceFun(id!!)
                getLocation(id!!)

                navBtn.setOnNavigationItemReselectedListener { item->
                    when(item.itemId){
                        R.id.backHome2->{
                            backFun()
                        }
                        R.id.dropCourse->{
                            dropClass(id!!)
                        }
                    }
                }


            }
        })

        val ft = fragmentManager!!.beginTransaction()
        if(Build.VERSION.SDK_INT>=26){
            ft.setReorderingAllowed(false)
        }
        ft.detach(this).attach(this)


//        val arr = FloatArray(1)
//        val distanceBtnLoc1And2 = Location.distanceBetween(34.07921,-117.9635383, 34.0793078,-117.9635858,arr)
//
//        Log.d("The arr", arr[0].toString())

        return binding.root
    }
    private fun getLocation(courseId: String){
        mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(activity!!.applicationContext)
        mFusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if(location!= null){
                    var tLatitude = 0.0
                    var tLongtitude = 0.0

                    val ref = FirebaseDatabase.getInstance().getReference("TeacherLocation")
                    ref.orderByChild("courseId").equalTo(courseId)
                        .addValueEventListener(object : ValueEventListener{
                            override fun onCancelled(p0: DatabaseError) {}
                            override fun onDataChange(p0: DataSnapshot) {
                                for(e in p0.children){
                                    val tLocation = e.getValue(TeacherLocation::class.java)
                                    tLatitude = tLocation?.latitude!!
                                    tLongtitude = tLocation?.longtitude!!
                                }


                                val arr = FloatArray(1)
                                val distanceBtnLoc1And2 = Location.distanceBetween(tLatitude,tLongtitude, location.latitude,location.longitude,arr)

                                Log.d("The student locaion is " , location.latitude.toString() + " - " + location.longitude.toString())
                                Log.d("The arr", arr[0].toString())

                                
                                if(arr[0] < 80.0){
                                    attendanceBtn.setOnClickListener { takeAttendance(courseId)}
                                }else{
                                    attendanceBtn.setOnClickListener {
                                        val builder = AlertDialog.Builder(context)
                                        builder.setTitle("FATAL")
                                        builder.setMessage("YOU ARE NOT IN CLASSROOM!!")
                                        val alert = builder.create()
                                        alert.setIcon(R.drawable.angry)
                                        alert.show()
                                    }
                                }


                            }
                        })

                }
                else{
                    Toast.makeText(context!!,"Hey location is null", Toast.LENGTH_LONG).show()
                }


            }

    }

   private fun takeAttendance(courseId: String){

           val model = ViewModelProviders.of(activity!!).get(GeneralCommunicator::class.java)

           model.name.observe(this, object : Observer<Any> {
               override fun onChanged(t: Any?) {
                   val name = t.toString()!!

                   arr.add(name)
                   Log.d("name is ", name)
                   val attendanceResult =
                       FirebaseDatabase.getInstance().getReference("AttendanceResult")
                   val key = attendanceResult.push().key
                   val attendance = AttendanceResult(courseId, name)
                   attendanceResult.child(key!!).setValue(attendance)

                   val sdf = SimpleDateFormat("MM/dd/yyyy hh:mm:ss")
                   val currentDate = sdf.format(Date())
                   val record = Record(
                       courseId,
                       currentDate, arr
                   )
                   val ref1 = FirebaseDatabase.getInstance().getReference("Record")
                   val keys = ref1.push().key!!
                   ref1.child(keys).setValue(record)
               }
           })

           attendanceBtn.alpha = 0.5f
           attendanceBtn.isEnabled = false
           val builder = AlertDialog.Builder(context)
           builder.setTitle("Success")
           builder.setMessage("Your attendance is recorded!")
           builder.setPositiveButton("Ok") { dialog, which ->

           }
           val alert = builder.create()
           alert.show()


   }
   private fun detectAttendanceFun(courseId : String){
       val attendanceIndicatorRef = FirebaseDatabase.getInstance().getReference("AttendanceIndicator")
       attendanceIndicatorRef.orderByChild("courseId").equalTo(courseId).addValueEventListener(
           object : ValueEventListener{
               override fun onCancelled(p0: DatabaseError) {}
               override fun onDataChange(p0: DataSnapshot) {
                   if(p0.exists()){
                       for(e in p0.children){
                           Log.d("first key",e.key!!)
                          val status = e.getValue(AttendanceIndicator::class.java)
                           if(status?.status == true){
                               Log.d("status is ", status?.status.toString())
                               attendanceBtn.setAlpha(1.0f)
                               attendanceBtn.isEnabled = true
                           }else{
                               Log.d("status is ", status?.status.toString())
                               attendanceBtn.setAlpha(.5f)
                               attendanceBtn.isEnabled = false
                           }

                       }
                   }
               }

           }
       )
   }
   private fun dropClass(courseId: String){
           val user = FirebaseAuth.getInstance().currentUser
           val studentRef = FirebaseDatabase.getInstance().getReference("Student")
           studentRef.orderByChild("email").equalTo(user!!.email).addListenerForSingleValueEvent(
               object:ValueEventListener{
                   override fun onCancelled(p0: DatabaseError) {}
                   override fun onDataChange(p0: DataSnapshot) {
                       for(e in p0.children){
                           Log.d("e.child is ", e.getValue().toString())
                           studentRef.child(e.key!!).child("courseId").addListenerForSingleValueEvent(
                               object : ValueEventListener{
                                   override fun onDataChange(p1: DataSnapshot) {
                                       for(e1 in p1.children) {
                                           Log.d("e1.child is ", e1.getValue().toString())
                                           if(e1.getValue().toString() == courseId){
                                               studentRef.child(e.key!!).child("courseId").child(e1.key!!).removeValue()
                                           }
                                       }
                                   }

                                   override fun onCancelled(p1: DatabaseError) {}
                               }
                           )
                       }
                   }
               }
           )
           val builder = AlertDialog.Builder(context)
           builder.setTitle("Dropped")
           builder.setMessage("This course is successfully dropped!")
           builder.setIcon(R.drawable.sad)
           builder.setPositiveButton("Ok"){dialog, which ->
               if(findNavController().currentDestination?.id == R.id.attendancePage) {
                   val user = FirebaseAuth.getInstance().currentUser
                   val model =
                       ViewModelProviders.of(activity!!).get(GeneralCommunicator::class.java)
                   model.setMsgCommunicator(user?.email!!)
                   val myFragment = StudentHomePage()
                   val fragmentTransaction = fragmentManager!!.beginTransaction()
                   fragmentTransaction.replace(R.id.myNavHostFragment, myFragment)
                   fragmentTransaction.addToBackStack(null)
                   fragmentTransaction.commit()
                   findNavController().navigate(R.id.action_attendancePage_to_studentHomePage)
               }
           }
           val alert = builder.create()
           alert.show()


   }
   private fun backFun(){

          if(findNavController().currentDestination?.id == R.id.attendancePage) {
              val user = FirebaseAuth.getInstance().currentUser
              val model = ViewModelProviders.of(activity!!).get(GeneralCommunicator::class.java)
              model.setMsgCommunicator(user?.email!!)
              val myFragment = StudentHomePage()
              val fragmentTransaction = fragmentManager!!.beginTransaction()
              fragmentTransaction.replace(R.id.myNavHostFragment, myFragment)
              fragmentTransaction.addToBackStack(null)
              fragmentTransaction.commit()
              findNavController().navigate(R.id.action_attendancePage_to_studentHomePage)
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
         * @return A new instance of fragment AttendancePage.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AttendancePage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
