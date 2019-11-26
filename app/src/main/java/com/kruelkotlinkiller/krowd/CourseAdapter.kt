package com.kruelkotlinkiller.krowd


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CourseAdapter(val data: ArrayList<Course>): RecyclerView.Adapter<CourseAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.className?.text = data[position].courseName
        holder?.img?.setImageResource(R.drawable.teachericon)

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

    }

}