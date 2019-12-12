package com.kruelkotlinkiller.krowd

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.LruCache
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
import androidx.recyclerview.widget.RecyclerView
import co.metalab.asyncawait.async
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kruelkotlinkiller.krowd.databinding.FragmentSeeAttendanceResultBinding
import kotlinx.android.synthetic.main.fragment_attendance_page.*
import kotlinx.android.synthetic.main.fragment_attendance_page.view.*
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.lang.System.out
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SeeAttendanceResult.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SeeAttendanceResult.newInstance] factory method to
 * create an instance of this fragment.
 */
class SeeAttendanceResult : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var binding : FragmentSeeAttendanceResultBinding
    private lateinit var attendedStudentList : RecyclerView
//    private lateinit var back : Button
//    private lateinit var share : Button
    private var arrayList = ArrayList<Student>()
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_see_attendance_result,container,false)
        attendedStudentList = binding.attendanceListView
//        back = binding.backToHom
        btnNav = binding.nav2
//        share = binding.Export
        val model = ViewModelProviders.of(activity!!).get(GeneralCommunicator::class.java)

        model.id.observe(this, object : Observer<Any>{
            override fun onChanged(t: Any?) {
                val courseId = t.toString()!!
               // shareFun(courseId)
                val databaseReference = FirebaseDatabase.getInstance().getReference("Student")
                databaseReference.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(p0: DataSnapshot) {

                        for (e in p0.children) {
                            Log.d("FIRST level key ", e.key!!)

                            databaseReference.child(e.key!!).child("courseId")
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(p1: DataSnapshot) {
                                        arrayList.clear()
                                        for (e1 in p1.children) {
                                            Log.d("second level key ", e1.key!!)
                                            val query =
                                                databaseReference.orderByChild("courseId/" + e1.key)
                                                    .equalTo(courseId)
                                            query.addListenerForSingleValueEvent(
                                                object : ValueEventListener {
                                                    override fun onDataChange(p2: DataSnapshot) {

                                                        if (p2.exists()) {

                                                            for (e2 in p2.children) {
                                                                Log.d(
                                                                    "helloooo",
                                                                    e2.getValue().toString()
                                                                )
                                                                val student =
                                                                    e2.getValue(Student::class.java)
                                                                arrayList.add(student!!)

                                                            }
                                                            for (i in arrayList) {
                                                                Log.d("The array is ", i.firstName)
                                                            }
                                                            val adapter = ResultAdapter(arrayList,courseId)
                                                            attendedStudentList.adapter = adapter
                                                        }
                                                    }

                                                    override fun onCancelled(p2: DatabaseError) {
                                                    }
                                                }
                                            )

                                        }
                                    }

                                    override fun onCancelled(p0: DatabaseError) {
                                    }


                                })

                            btnNav.setOnNavigationItemReselectedListener { item->
                                when(item.itemId){
                                    R.id.backHome4->{
                                        if(findNavController().currentDestination?.id == R.id.seeAttendanceResult) {
                                            val user = FirebaseAuth.getInstance().currentUser
                                            model.setMsgCommunicator(user?.email!!)
                                            val myFragment = TeacherHomePage()
                                            val fragmentTransaction =
                                                fragmentManager!!.beginTransaction()
                                            fragmentTransaction.replace(
                                                R.id.myNavHostFragment,
                                                myFragment
                                            )
                                            fragmentTransaction.addToBackStack(null)
                                            fragmentTransaction.commit()
                                            findNavController().navigate(R.id.teacherHomePage)
                                        }
                                    }
                                    R.id.share->{
                                        shareFun(courseId!!)
                                    }
                                }
                            }

                        }
                    }


                })
            }})



//        back.setOnClickListener {view: View->
//
//        }




                return binding.root
    }

  fun findAbsences(courseId: String, courseName: String){
      val list = ArrayList<String>()
      val list2 = ArrayList<String>()
      val databaseReference = FirebaseDatabase.getInstance().getReference("Student")
      databaseReference.addValueEventListener(object : ValueEventListener {
          override fun onCancelled(p0: DatabaseError) {}
          override fun onDataChange(p0: DataSnapshot) {

              for (e in p0.children) {
                  Log.d("FIRST level key ", e.key!!)

                  databaseReference.child(e.key!!).child("courseId")
                      .addListenerForSingleValueEvent(object : ValueEventListener {
                          override fun onDataChange(p1: DataSnapshot) {
                              arrayList.clear()
                              for (e1 in p1.children) {
                                  Log.d("second level key ", e1.key!!)
                                  val query =
                                      databaseReference.orderByChild("courseId/" + e1.key)
                                          .equalTo(courseId)
                                  query.addListenerForSingleValueEvent(
                                      object : ValueEventListener {
                                          override fun onDataChange(p2: DataSnapshot) {

                                              if (p2.exists()) {

                                                  for (e2 in p2.children) {
                                                      Log.d(
                                                          "helloooo",
                                                          e2.getValue().toString()
                                                      )
                                                      val student =
                                                          e2.getValue(Student::class.java)
                                                     list.add("Name: "+student?.firstName + " " + student?.lastName + "\n CIN: " + student?.id)

                                                      val attendanceRef = FirebaseDatabase.getInstance().getReference("AttendanceResult")
                                                      attendanceRef.orderByChild("courseId").equalTo(courseId).addValueEventListener(
                                                          object : ValueEventListener{
                                                              override fun onCancelled(p3: DatabaseError) {}
                                                              override fun onDataChange(p3: DataSnapshot) {
                                                                  if(p3.exists()){
                                                                      for(e3 in p3.children) {
                                                                          val attendanted = e3.getValue(AttendanceResult::class.java)
                                                                          list2.add("Name: "+attendanted!!.name + "\n CIN: " + student?.id)
                                                                      }

                                                                  }
                                                                  list.removeAll(list2)
                                                                  val current = Date()
                                                                  val formatter = SimpleDateFormat("yyyy-MM-dd")
                                                                  val final = formatter.format(current)
                                                                  val user = FirebaseAuth.getInstance().currentUser
                                                                  val mIntent = Intent(Intent.ACTION_SEND)
                                                                  mIntent.data = Uri.parse("mailto:")
                                                                  mIntent.type = "text/plain"
                                                                  Log.d("The userr email iss ", user?.email!!)
                                                                  mIntent.putExtra(Intent.EXTRA_EMAIL,arrayOf(user?.email!!))
                                                                  mIntent.putExtra(Intent.EXTRA_SUBJECT,"Absent Student for " + courseName + " on " + final.toString())

                                                                  val sb = StringBuilder()
                                                                  for (s in list) {
                                                                      sb.append(s)
                                                                      sb.append("\n")
                                                                  }
                                                                      mIntent.putExtra(Intent.EXTRA_TEXT,sb.toString())

                                                                  try {
                                                                      //start email intent
                                                                      startActivity(Intent.createChooser(mIntent, "Choose Email Client..."))
                                                                  }
                                                                  catch (e: Exception){

                                                                  }



                                                              }


                                                          })

                                                  }

                                              }
                                          }

                                          override fun onCancelled(p2: DatabaseError) {
                                          }
                                      }
                                  )

                              }
                          }

                          override fun onCancelled(p0: DatabaseError) {
                          }


                      })

              }
          }


      })






  }
   fun shareFun(courseId : String){
           val attendanceRef = FirebaseDatabase.getInstance().getReference("AttendanceResult")
           attendanceRef.orderByChild("courseId").equalTo(courseId).addValueEventListener(
               object:ValueEventListener{
                   override fun onCancelled(p0: DatabaseError) {}
                   override fun onDataChange(p0: DataSnapshot) {
                       for(e in p0.children){
                           val result = e.getValue(AttendanceResult::class.java)
                           val courseRef = FirebaseDatabase.getInstance().getReference("Course")
                           courseRef.orderByChild("courseId").equalTo(courseId).addValueEventListener(
                               object: ValueEventListener{
                                   override fun onCancelled(p1: DatabaseError) {}
                                   override fun onDataChange(p1: DataSnapshot) {

                                       for(e1 in p1.children){
                                           val course = e1.getValue(Course::class.java)!!
                                           findAbsences(courseId,course?.courseName)
                                       }

                                   }
                               }
                           )


                       }
                   }
               }
           )

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
         * @return A new instance of fragment SeeAttendanceResult.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SeeAttendanceResult().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
