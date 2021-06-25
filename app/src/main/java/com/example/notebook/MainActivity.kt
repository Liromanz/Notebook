package com.example.notebook

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.text.format.DateUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notebook.database.DatabaseHelper
import com.example.notebook.models.ItemOfList
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private var peopleDB: DatabaseHelper? = null
    private var dateAndTime = Calendar.getInstance()
    lateinit var datePickerDialog: DatePickerDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        peopleDB = DatabaseHelper(this)
        setInitialDateTime()

        refreshRecycler()

        addNote.setOnClickListener {
            createNote()
        }
        btnCalendar.setOnClickListener {
            setDate()
        }
    }

    private fun createNote() {
        val intent = Intent(this, NoteActivity::class.java)
        startActivity(intent)
    }

    private fun setDate() {
        val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            dateAndTime[Calendar.YEAR] = year
            dateAndTime[Calendar.MONTH] = monthOfYear
            dateAndTime[Calendar.DAY_OF_MONTH] = dayOfMonth
            setInitialDateTime()
        }
        datePickerDialog = DatePickerDialog.newInstance(date,
            dateAndTime.get(Calendar.YEAR),
            dateAndTime.get(Calendar.MONTH),
            dateAndTime.get(Calendar.DAY_OF_MONTH)
        )
        val list = getDates()
        datePickerDialog.highlightedDays = list
        datePickerDialog.show(supportFragmentManager, "DatePickerDialog")
    }
    private fun setInitialDateTime(){
        var dateString = DateUtils.formatDateTime(this, dateAndTime.timeInMillis,
            DateUtils.FORMAT_SHOW_WEEKDAY or DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_NUMERIC_DATE or DateUtils.FORMAT_SHOW_YEAR)
        txtDate.text = dateString.capitalize().substring(0,dateString.length-3)
        refreshRecycler()
    }

    private fun getDates() : Array<Calendar> {
        var list : MutableList<Calendar> = mutableListOf()

        1
                val date = Calendar.getInstance()
                val fullDateString = data.getString(0)
                val separatedDays = fullDateString.substring(fullDateString.indexOf(',')+2).split('.')
                date.set(Calendar.YEAR, separatedDays[2].toInt())
                date.set(Calendar.MONTH, separatedDays[1].toInt()-1)
                date.set(Calendar.DAY_OF_MONTH, separatedDays[0].toInt())
                list.add(date)
            }
        }
        return list.toTypedArray()
    }

    private fun showUsers() : List<ItemOfList> {
        var list : MutableList<ItemOfList> = mutableListOf()

        val data: Cursor? = peopleDB!!.showData(txtDate.text.toString())
        if (data!!.count != 0) {
            while (data.moveToNext()) {
                list.add(ItemOfList(data.getInt(0), data.getString(1), data.getString(2), data.getString(3), data.getString(4), data.getInt(5) == 1))
            }
        }
        return list.toList()
    }

    fun refreshRecycler(){
        var noteList: List<ItemOfList> = showUsers()
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = ItemAdapter(this, noteList) {
            val intent = Intent(this, NoteActivity::class.java)
            intent.putExtra("NOTE", it)
            startActivity(intent)
        }
    }

}