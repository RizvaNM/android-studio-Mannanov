package com.itis.mannanov

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    lateinit var database : FirebaseDatabase
    lateinit var mondayRef : DatabaseReference
    lateinit var tuesdayRef : DatabaseReference
    lateinit var wednesdayRef : DatabaseReference
    lateinit var thursdayRef : DatabaseReference
    lateinit var fridayRef : DatabaseReference
    lateinit var saturdayRef : DatabaseReference
    lateinit var lastAddedLessonRef: DatabaseReference

    lateinit var spinner: Spinner
    lateinit var day: TextView
    lateinit var day2: TextView

    lateinit var adapter: DiaryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = FirebaseDatabase.getInstance()
        mondayRef = database.getReference("Monday")
        tuesdayRef = database.getReference("Tuesday")
        wednesdayRef = database.getReference("Wednesday")
        thursdayRef = database.getReference("Thursday")
        fridayRef = database.getReference("Friday")
        saturdayRef = database.getReference("Saturday")
        lastAddedLessonRef = database.getReference("lastAddedLesson")

        val integerChars = '0'..'9'
        fun isHour(input: String): Boolean {
            return input.all { it in integerChars &&
                    input.length < 3 && input.isNotEmpty() &&
                    input.toInt() < 24}
        }
        fun isMinute(input: String): Boolean {
            return input.all { it in integerChars &&
                    input.length < 3 && input.isNotEmpty() &&
                    input.toInt() < 60}
        }

        val addLesson = findViewById<EditText>(R.id.add_lesson)
        val hoursBegin = findViewById<EditText>(R.id.add_begin_hours)
        val minutesBegin = findViewById<EditText>(R.id.add_begin_minutes)
        val hoursEnd = findViewById<EditText>(R.id.add_end_hours)
        val minutesEnd = findViewById<EditText>(R.id.add_end_minutes)
        val add = findViewById<Button>(R.id.add)
        val monday = findViewById<RecyclerView>(R.id.lessons_monday)
        val tuesday = findViewById<RecyclerView>(R.id.lessons_tuesday)
        val wednesday = findViewById<RecyclerView>(R.id.lessons_wednesday)
        val thursday = findViewById<RecyclerView>(R.id.lessons_thursday)
        val friday = findViewById<RecyclerView>(R.id.lessons_friday)
        val saturday = findViewById<RecyclerView>(R.id.lessons_saturday)
        val cancel = findViewById<FloatingActionButton>(R.id.cancel_button)

        getData(mondayRef, monday)
        getData(tuesdayRef, tuesday)
        getData(wednesdayRef, wednesday)
        getData(thursdayRef, thursday)
        getData(fridayRef, friday)
        getData(saturdayRef, saturday)

        spinner = findViewById(R.id.spinner)
        day = findViewById(R.id.example)
        day2 = findViewById(R.id.example2)
        val spinnerAdapter = ArrayAdapter.createFromResource(this,
            R.array.days_on_week,
            android.R.layout.simple_spinner_item)
        spinner.setSelection(0)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter
        spinner.onItemSelectedListener = this

        var count: Int = 0
        lastAddedLessonRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                count = 0
                for (data in snapshot.children) {
                    count += 1
                    day2.text = data.value.toString().substringAfter('=').substringBefore(',')
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("cancel", error.toString())
            }

        })

        add.setOnClickListener {
            val HBegin = hoursBegin.text.toString()
            val MBegin = minutesBegin.text.toString()
            val HEnd = hoursEnd.text.toString()
            val MEnd = minutesEnd.text.toString()
            if (addLesson.text.toString().trim().isNotEmpty() &&
                isHour(HBegin) && isMinute(MBegin) &&
                isHour(HEnd) && isMinute(MEnd) &&
                (HBegin.toInt() < HEnd.toInt() || (HBegin == HEnd && MBegin.toInt() < MEnd.toInt())))
                {
                count += 1
                sendData(mondayRef, "Понедельник", "Monday", addLesson, hoursBegin, minutesBegin, hoursEnd, minutesEnd, count)
                sendData(tuesdayRef, "Вторник", "Tuesday", addLesson, hoursBegin, minutesBegin, hoursEnd, minutesEnd, count)
                sendData(wednesdayRef, "Среда", "Wednesday", addLesson, hoursBegin, minutesBegin, hoursEnd, minutesEnd, count)
                sendData(thursdayRef, "Четверг", "Thursday", addLesson, hoursBegin, minutesBegin, hoursEnd, minutesEnd, count)
                sendData(fridayRef, "Пятница", "Friday", addLesson, hoursBegin, minutesBegin, hoursEnd, minutesEnd, count)
                sendData(saturdayRef, "Суббота","Saturday", addLesson, hoursBegin, minutesBegin, hoursEnd, minutesEnd, count)
                if (day.text.toString() == "") {
                    Toast.makeText(this, "Выберите день недели", Toast.LENGTH_LONG).show()
                }
            }
        }
        cancel.setOnClickListener {
            lastAddedLessonRef.child(count.toString()).removeValue()
            database.getReference(day2.text.toString()).child(count.toString()).removeValue()
            count -= 1
        }

    }
    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented")
    }
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val text: String = parent?.getItemAtPosition(position).toString()
        day.text = text
    }

    private fun sendData (ref: DatabaseReference, dayOfWeek: String, dayOfWeekEnglish: String, addLesson: EditText,
                          hoursBegin: EditText, minutesBegin: EditText,
                          hoursEnd: EditText, minutesEnd: EditText, count: Int) {

        if(day.text.toString() == dayOfWeek) {
            val model = LessonModel(addLesson.text.toString().trim(),
                dayOfWeekEnglish,
                hoursBegin.text.toString().toInt(),
                minutesBegin.text.toString().toInt(),
                hoursEnd.text.toString().toInt(),
                minutesEnd.text.toString().toInt(),
                count.toString()
            )


            ref.child(count.toString()).setValue(model)

            addLesson.text.clear()
            hoursBegin.text.clear()
            minutesBegin.text.clear()
            hoursEnd.text.clear()
            minutesEnd.text.clear()
            spinner.setSelection(0)

            lastAddedLessonRef.child(count.toString()).setValue(LastAddedLessonModel(count.toString(), dayOfWeekEnglish))
        }
    }

    private fun getData(ref: DatabaseReference, recyclerView: RecyclerView) {
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val list: ArrayList<LessonModel> = ArrayList()

                for (data in snapshot.children){
                    val modeltext = data.value.toString().substring(5)
                    val UID = modeltext
                        .substringBefore(',')
                    val weekDay = modeltext
                        .substringAfter('=').substringBefore(',')
                    val lesson = modeltext
                        .substringAfter('=').substringAfter('=')
                        .substringBefore(',')
                    val minBegin = modeltext
                        .substringAfter('=').substringAfter('=')
                        .substringAfter('=').substringBefore(',')
                        .toInt()
                    val hourBegin = modeltext
                        .substringAfter('=').substringAfter('=')
                        .substringAfter('=').substringAfter('=')
                        .substringBefore(',').toInt()
                    val minEnd = modeltext
                        .substringAfter('=').substringAfter('=')
                        .substringAfter('=').substringAfter('=')
                        .substringAfter('=').substringBefore(',')
                        .toInt()
                    val hourEnd = modeltext
                        .substringAfter('=').substringAfter('=')
                        .substringAfter('=').substringAfter('=')
                        .substringAfter('=').substringAfter('=')
                        .substringBefore('}').toInt()
                    val model = LessonModel(lesson, weekDay, hourBegin, minBegin, hourEnd, minEnd, UID)
                    if (model.UID != "nothing") {
                        list.add(model)
                    }
                }
                if (list.size > 0) {
                    list.sortBy { it.minutesEnd }
                    list.sortBy { it.hoursEnd }
                    list.sortBy { it.minutesBegin }
                    list.sortBy { it.hoursBegin }
                }
                    adapter = DiaryAdapter(list)
                    recyclerView.adapter = adapter


            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("cancel", error.toString())
            }

        })
    }
}