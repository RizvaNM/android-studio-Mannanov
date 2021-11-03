package com.itis.mannanov

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.itis.mannanov.R

class DiaryAdapter(var list: ArrayList<LessonModel>): RecyclerView.Adapter<DiaryAdapter.ViewHolder>() {


    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        var lesson : TextView = itemView.findViewById(R.id.lesson_name)
        var HBegin : TextView = itemView.findViewById(R.id.begin_hours)
        var MBegin : TextView = itemView.findViewById(R.id.begin_minutes)
        var HEnd : TextView = itemView.findViewById(R.id.end_hours)
        var MEnd : TextView = itemView.findViewById(R.id.end_minutes)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.lesson_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.lesson.text = list[position].lesson
        holder.HBegin.text = list[position].hoursBegin.toString()
        holder.MBegin.text = list[position].minutesBegin.toString()
        holder.HEnd.text = list[position].hoursEnd.toString()
        holder.MEnd.text = list[position].minutesEnd.toString()
    }

    override fun getItemCount(): Int {
        return list.size
    }
}