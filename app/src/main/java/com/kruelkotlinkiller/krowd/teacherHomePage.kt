package com.kruelkotlinkiller.krowd

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
    private var temp : String?=null

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
        val model = ViewModelProviders.of(activity!!).get(TeacherNameCommunicator::class.java)
        model.message.observe(this,object: Observer<Any> {
            override fun onChanged(t: Any?) {
                temp = t!!.toString()
                val ref = FirebaseDatabase.getInstance().reference
                val ordersRef = ref.child("Teacher").orderByChild("email").equalTo(temp)
                val valueEventListener = object : ValueEventListener{
                    override fun onDataChange(p0: DataSnapshot) {
                        for(ds in p0.children){
                            val nameTemp = ds.child("firstName").getValue(String::class.java) + " " + ds.child("lastName").getValue(String::class.java)
                            name.text = nameTemp
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {
                    }

                }
                ordersRef.addListenerForSingleValueEvent(valueEventListener)
            }
        })



        createClassBtn.setOnClickListener {view : View ->
            view.findNavController().navigate(R.id.action_teacherHomePage_to_manageClasses)
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
