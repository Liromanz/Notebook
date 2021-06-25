package com.example.notebook

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notebook.database.DatabaseHelper
import com.example.notebook.models.ItemOfList
import kotlinx.android.synthetic.main.activity_note.*
import java.text.SimpleDateFormat
import java.util.*


class NoteActivity : AppCompatActivity() {
    private var peopleDB: DatabaseHelper? = null
    private var createOrUpdate = true;
    private var dateAndTime = Calendar.getInstance()
    private var note : ItemOfList? = null
    var time = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
        dateAndTime[Calendar.HOUR_OF_DAY] = hourOfDay
        dateAndTime[Calendar.MINUTE] = minute
        setInitialTime()
    }
    var date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        dateAndTime[Calendar.YEAR] = year
        dateAndTime[Calendar.MONTH] = monthOfYear
        dateAndTime[Calendar.DAY_OF_MONTH] = dayOfMonth
        setInitialDate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        peopleDB = DatabaseHelper(this)
        note = intent.getParcelableExtra("NOTE")

        if (note != null) {
            val txtName = findViewById<TextView>(R.id.editName)
            val txtDesc = findViewById<TextView>(R.id.editDescription)
            val txtTime = findViewById<TextView>(R.id.editTime)
            val txtDate = findViewById<TextView>(R.id.editDate)

            txtName.text = note!!.noteName
            txtDesc.text = note!!.noteDescription
            txtTime.text = note!!.noteTime
            txtDate.text = note!!.noteDay

            createOrUpdate = false;
        }
        editTime.setOnClickListener {
            setTime()
        }
        editDate.setOnClickListener {
            setDate()
        }
        btnCancel.setOnClickListener {
            cancelNote()
        }
        btnDone.setOnClickListener {
            doneNote()
        }
    }

    private fun cancelNote(){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Вы точно хотите вернуться? Все несохраненные изменения будут утеряны")

        builder.setPositiveButton("Да"){dialog, which ->
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        builder.setNegativeButton("Нет"){ dialog, _ -> dialog.cancel()}
        val dialog:AlertDialog = builder.create()
        dialog.show()
    }

    private fun doneNote(){
        val txtName = findViewById<TextView>(R.id.editName)
        val txtTime = findViewById<TextView>(R.id.editTime)
        val txtDate = findViewById<TextView>(R.id.editDate)
        val txtDesc = findViewById<TextView>(R.id.editDescription)

        if (txtName.text.toString() != "" && txtDate.text != "" && txtTime.text != "") {
            if (createOrUpdate) {
                val insertData: Boolean = peopleDB!!.addData( txtName.text.toString(), txtTime.text.toString(), txtDate.text.toString(), txtDesc.text.toString(), false)
                if (!insertData)
                    Toast.makeText(this, "Данные не были добавлены, попробуйте еще раз", Toast.LENGTH_SHORT).show()
            }
            else {
                peopleDB!!.update(txtName.text.toString(), txtTime.text.toString(), txtDate.text.toString(), txtDesc.text.toString(), note!!.noteCheck, note!!.noteID)
            }
             setAlarm(dateAndTime, txtName.text.toString())
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setTime() {
        TimePickerDialog(this, time,
            dateAndTime[Calendar.HOUR_OF_DAY],
            dateAndTime[Calendar.MINUTE], true
        ).show()
    }

    private fun setInitialTime() {
        val date = SimpleDateFormat("HH:mm", Locale.getDefault())
        editTime.text = date.format(dateAndTime.timeInMillis)
    }
    private fun setDate() {
        DatePickerDialog(this, date,
            dateAndTime[Calendar.YEAR],
            dateAndTime[Calendar.MONTH],
            dateAndTime[Calendar.DAY_OF_MONTH]).show()
    }
    private fun setInitialDate(){
        var date = DateUtils.formatDateTime(this, dateAndTime.timeInMillis,
            DateUtils.FORMAT_SHOW_WEEKDAY or DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_NUMERIC_DATE or DateUtils.FORMAT_SHOW_YEAR)
        editDate.text = date.capitalize().substring(0,date.length-3)
    }

    private fun setAlarm(time: Calendar, name: String) {
        val date = SimpleDateFormat("HH:mm", Locale.getDefault())
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(this, AlarmReciever::class.java)
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
        intent.putExtra("Name_Note", name)
        intent.putExtra("Time_Note", date.format(time.timeInMillis))
        val pendingIntent =
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.set(AlarmManager.RTC_WAKEUP, time.timeInMillis, pendingIntent)

        if (chkDay.isChecked) {
            val timeChange = time
            timeChange.add(Calendar.DAY_OF_MONTH, -1)
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeChange.timeInMillis, pendingIntent)
        }
        if (chkHour.isChecked) {
            val timeChange = time
            timeChange.add(Calendar.HOUR, -1)
            alarmManager.set(AlarmManager.RTC_WAKEUP, time.timeInMillis, pendingIntent)
        }
    }
}