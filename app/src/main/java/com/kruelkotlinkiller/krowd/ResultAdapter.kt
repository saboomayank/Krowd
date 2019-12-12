package com.kruelkotlinkiller.krowd

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ResultAdapter(val data: ArrayList<Student>, val id : String): RecyclerView.Adapter<ResultAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val name = data[position].firstName + " " + data[position].lastName
        holder?.studentName?.text = name
        holder?.studentName.setTextColor(Color.WHITE)
        holder?.row?.setBackgroundColor(Color.parseColor("#009900"))
        holder?.status?.text = "Present"
        holder?.status.setTextColor(Color.WHITE)
        holder?.img?.setImageResource(R.drawable.attended)
        val nameList = ArrayList<String>()
        val attendedList = ArrayList<String>()
        val attendanceRef = FirebaseDatabase.getInstance().getReference("AttendanceResult")
            attendanceRef.orderByChild("courseId").equalTo(id).addValueEventListener(
                object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(p0: DataSnapshot) {
                        if(p0.exists()){
                            for(e in p0.children) {
                                nameList.add(name)
                                attendedList.add(e.getValue(AttendanceResult::class.java)?.name!!)
                            }

                            nameList.removeAll(attendedList)
                            for(i in nameList){
                                holder?.studentName?.text = i
                                holder?.status?.text = "Absent"
                                holder?.studentName?.setTextColor(Color.WHITE)
                                holder?.status?.setTextColor(Color.WHITE)
                                holder?.img?.setImageResource(R.drawable.absent)
                                holder?.row?.setBackgroundColor(Color.RED)

                            }
                            }
                        else{
                            holder?.studentName?.setTextColor(Color.WHITE)
                            holder?.status?.setTextColor(Color.WHITE)
                            holder?.img?.setImageResource(R.drawable.absent)
                            holder?.status?.text = "Absent"
                            holder?.row.setBackgroundColor(Color.RED)
                        }

                    }


        })

    }
    fun getID(position: Int) = data[position].courseId



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.attendance_item_view, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }



    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val studentName = itemView.findViewById<TextView>(R.id.attendanceT)
        val img = itemView.findViewById<ImageView>(R.id.attendanceS)
        val status = itemView.findViewById<TextView>(R.id.status)
        val row = itemView.findViewById<ConstraintLayout>(R.id.attendanceRow)


    }

}