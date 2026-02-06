package com.pongsawad.blueelephant

import android.os.Parcel
import android.os.Parcelable

data class Friend(
    val name: String,
    val email: String,
    val gender: String,
    val imageUri: String? = null,
    val age: Int
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(gender)
        parcel.writeString(imageUri)
        parcel.writeInt(age)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Friend> {
        override fun createFromParcel(parcel: Parcel) = Friend(parcel)
        override fun newArray(size: Int) = arrayOfNulls<Friend>(size)
    }
}
