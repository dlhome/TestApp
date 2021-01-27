package com.example.testapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class PersonData(
    var image: String ="",
    var weight: Double = 0.0,
    var isKgUnits: Boolean = true,
    var dob: Date = Date()
) : Parcelable
