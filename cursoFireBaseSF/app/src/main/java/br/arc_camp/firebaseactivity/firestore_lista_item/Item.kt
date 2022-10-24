package br.arc_camp.firebaseactivity.firestore_lista_item

class Item(var id: Int?, var nome: String?,var descricao: String?, var url_image: String?) {

    // necessario para ler informaçeos do banco Firebase
    constructor():this (null, null, null,null)

}