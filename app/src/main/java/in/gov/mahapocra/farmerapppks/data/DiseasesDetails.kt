package `in`.gov.mahapocra.farmerapppks.data

import android.os.Parcel
import android.os.Parcelable

data class DiseasesDetails(
    var id: Int, var name: String?, var decription: String?, var img: String?, var type: String?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(decription)
        parcel.writeString(img)
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DiseasesDetails> {
        override fun createFromParcel(parcel: Parcel): DiseasesDetails {
            return DiseasesDetails(parcel)
        }

        override fun newArray(size: Int): Array<DiseasesDetails?> {
            return arrayOfNulls(size)
        }
    }
}
