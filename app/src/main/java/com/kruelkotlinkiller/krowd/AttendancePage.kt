package com.kruelkotlinkiller.krowd

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kruelkotlinkiller.krowd.databinding.FragmentAttendancePageBinding

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
    private lateinit var back : Button
    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient

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
        back = binding.backToHome

        val model = ViewModelProviders.of(activity!!).get(GeneralCommunicator::class.java)
        model.id.observe(this,object: Observer<Any> {
            override fun onChanged(t: Any?) {
                val id = t.toString()!!
                Log.d("Hey the id " , id)
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
                attendanceBtn.setOnClickListener {takeAttendance(id!!)}
            }
        })

        val ft = fragmentManager!!.beginTransaction()
        if(Build.VERSION.SDK_INT>=26){
            ft.setReorderingAllowed(false)
        }
        ft.detach(this).attach(this)

        backFun()
        return binding.root
    }
   private fun takeAttendance(courseId: String){

           val model = ViewModelProviders.of(activity!!).get(GeneralCommunicator::class.java)
           model.name.observe(this, object : Observer<Any> {
               override fun onChanged(t: Any?) {
                   val name = t.toString()!!
                   Log.d("name is ", name)
                   val attendanceResult =
                       FirebaseDatabase.getInstance().getReference("AttendanceResult")
                   val key = attendanceResult.push().key
                   val attendance = AttendanceResult(courseId, name)
                   attendanceResult.child(key!!).setValue(attendance)
               }
           })
           attendanceBtn.isEnabled = false




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
                               attendanceBtn.isEnabled = true
                           }else{
                               Log.d("status is ", status?.status.toString())
                               attendanceBtn.isEnabled = false
                           }

                       }
                   }
               }

           }
       )
   }
   private fun backFun(){
      back.setOnClickListener {view:View->
          val user = FirebaseAuth.getInstance().currentUser
          val model = ViewModelProviders.of(activity!!).get(GeneralCommunicator::class.java)
          model.setMsgCommunicator(user?.email!!)
          val myFragment = StudentHomePage()
          val fragmentTransaction = fragmentManager!!.beginTransaction()
          fragmentTransaction.replace(R.id.myNavHostFragment, myFragment)
          fragmentTransaction.addToBackStack(null)
          fragmentTransaction.commit()
          view.findNavController().navigate(R.id.action_attendancePage_to_studentHomePage)

      }
   }
    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                      //  findViewById<TextView>(R.id.latTextView).text = location.latitude.toString()
                      //  findViewById<TextView>(R.id.lonTextView).text = location.longitude.toString()
                    }
                }
            } else {
                Toast.makeText(context, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
           // findViewById<TextView>(R.id.latTextView).text = mLastLocation.latitude.toString()
           // findViewById<TextView>(R.id.lonTextView).text = mLastLocation.longitude.toString()
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
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
