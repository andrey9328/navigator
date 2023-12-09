package org.navigator.main.container

import android.os.Parcel
import android.os.Parcelable

data class SubRoutersContainer(val mainRouter: String, val subRouter: String) : Parcelable {
    constructor(parcel: Parcel):
            this(parcel.readString() ?: "", parcel.readString() ?: "")

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(mainRouter)
        parcel.writeString(subRouter)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SubRoutersContainer> {
        override fun createFromParcel(parcel: Parcel): SubRoutersContainer {
            return SubRoutersContainer(parcel)
        }

        override fun newArray(size: Int): Array<SubRoutersContainer?> {
            return arrayOfNulls(size)
        }
    }
}
