package com.kruelkotlinkiller.krowd


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecordAdapter(val data: ArrayList<Record>, val context : Context): RecyclerView.Adapter<RecordAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.date?.text = data[position].date
        holder?.list?.adapter = ArrayAdapter(context,android.R.layout.simple_list_item_1,data[position].students)
        holder?.courseName.text = data[position].courseId

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.record_item_view, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val date = itemView.findViewById<TextView>(R.id.studentName)
        val list = itemView.findViewById<ListView>(R.id.Student)
        val courseName = itemView.findViewById<TextView>(R.id.textView15)
    }

}