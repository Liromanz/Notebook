package com.example.notebook

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notebook.database.DatabaseHelper
import com.example.notebook.models.ItemOfList

class ItemAdapter (
    private val context: Context,
    private val notes: List<ItemOfList>,
    val listener:  (ItemOfList) -> Unit
) : RecyclerView.Adapter<ItemAdapter.NoteViewHolder>() {

    class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private var peopleDB: DatabaseHelper? = null

        val txtName = view.findViewById<TextView>(R.id.txtName)
        val txtDesc = view.findViewById<TextView>(R.id.txtDescription)
        val txtTime = view.findViewById<TextView>(R.id.txtTime)
        val checkDone = view.findViewById<CheckBox>(R.id.chkDone)
        val btnDelete = view.findViewById<ImageButton>(R.id.btnDelete)
        val context = view.context

        fun bindView(image: ItemOfList, listener: (ItemOfList) -> Unit){
            val id = image.noteID
            txtName.text = image.noteName
            txtTime.text = image.noteTime
            val date = image.noteDay
            txtDesc.text = image.noteDescription
            checkDone.isChecked = image.noteCheck

            checkDone.setOnClickListener {
                peopleDB = DatabaseHelper(it.context)
                peopleDB!!.update(txtName.text.toString(), txtTime.text.toString(), date, txtDesc.text.toString(), checkDone.isChecked, id)
            }
            btnDelete.setOnClickListener{
                peopleDB = DatabaseHelper(it.context)
                if (peopleDB!!.delete(id))
                {
                    (context as MainActivity).refreshRecycler()
                }
            }
            itemView.setOnClickListener {listener(image)}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder =
        NoteViewHolder(LayoutInflater.from(context).inflate(R.layout.item_list, parent, false))

    override fun getItemCount(): Int = notes.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bindView(notes[position], listener)
    }

}
