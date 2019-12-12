package com.kruelkotlinkiller.krowd


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class CourseAdapter(val data: ArrayList<Course>, val type : String): RecyclerView.Adapter<CourseAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(type == "Student") {
            holder?.className?.text = data[position].courseName
            holder?.ids?.text = "ID: " + data[position].courseId
            holder?.name?.text = "Teacher: " + data[position].professorName
            holder?.img?.setImageResource(R.drawable.classlogo)
        }
        if(type == "Teacher"){
            holder?.className?.text = data[position].courseName
            holder?.ids?.text = "ID: " + data[position].courseId
            holder?.name?.text = Date().toString()
            holder?.img?.setImageResource(R.drawable.teachercourse)

        }
    }
    fun getID(position: Int) = data[position].courseId


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.course_item_view, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }



    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val className = itemView.findViewById<TextView>(R.id.className)
        val img = itemView.findViewById<ImageView>(R.id.imageView2)
        val ids = itemView.findViewById<TextView>(R.id.ids)
        val name = itemView.findViewById<TextView>(R.id.name)

    }

}