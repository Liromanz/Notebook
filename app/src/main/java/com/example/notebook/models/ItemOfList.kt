package com.example.notebook.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ItemOfList (
    val noteID : Int,
    val noteName : String,
    val noteTime : String,
    val noteDay : String,
    val noteDescription : String = "",
    var noteCheck: Boolean = false
) : Parcelable