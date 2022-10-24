package br.arc_camp.firebaseactivity.firestore_lista_categoria

import android.os.Parcel
import android.os.Parcelable

class Categoria(var nome: String?, var id: Int?) : Parcelable{

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    constructor(): this (null, null)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nome)
        parcel.writeValue(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Categoria> {
        override fun createFromParcel(parcel: Parcel): Categoria {
            return Categoria(parcel)
        }

        override fun newArray(size: Int): Array<Categoria?> {
            return arrayOfNulls(size)
        }
    }

}