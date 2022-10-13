package br.arc_camp.firebaseactivity.firestore

class GerenteTeste {

    var nome: String? = null
    var idade: Int? = null
    var fumante: Boolean? = null

    // 1º construtor
    constructor()

    // 2º construtor
    constructor(nome: String?, idade: Int?, fumante: Boolean?) {
        this.nome = nome
        this.idade = idade
        this.fumante = fumante
    }

}