package com.kruelkotlinkiller.krowd

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StudentAdapter(val data: ArrayList<Student>): RecyclerView.Adapter<StudentAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.studentName?.text = data[position].firstName + " " + data[position].lastName
        holder?.img?.setImageResource(R.drawable.studenticon)

    }
    fun getID(position: Int) = data[position].courseId
    fun getName(position: Int) = data[position].firstName
    fun getCIN(position: Int) = data[position].id


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.student_item_view, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }



    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val studentName = itemView.findViewById<TextView>(R.id.studentName)
        val img = itemView.findViewById<ImageView>(R.id.studentIcon)

    }

}