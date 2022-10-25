package br.arc_camp.firebaseactivity.firestore_lista_item

import android.os.Parcel
import android.os.Parcelable

class Item(var id: Int?, var nome: String?,var descricao: String?, var url_image: String?) : Parcelable{

    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    // necessario para ler informaçeos do banco Firebase
    constructor():this (null, null, null,null)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(nome)
        parcel.writeString(descricao)
        parcel.writeString(url_image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Item> {
        override fun createFromParcel(parcel: Parcel): Item {
            return Item(parcel)
        }

        override fun newArray(size: Int): Array<Item?> {
            return arrayOfNulls(size)
        }
    }

}