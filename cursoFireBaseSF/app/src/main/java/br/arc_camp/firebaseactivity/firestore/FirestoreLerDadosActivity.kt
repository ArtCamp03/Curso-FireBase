package br.arc_camp.firebaseactivity.firestore

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import br.arc_camp.firebaseactivity.databinding.ActivityFirestoreLerDadosBinding
import com.br.jafapps.bdfirestore.util.DialogProgress
import com.br.jafapps.bdfirestore.util.Util
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreLerDadosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFirestoreLerDadosBinding
    private lateinit var bd: FirebaseFirestore

    private lateinit var reference : CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFirestoreLerDadosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bd = FirebaseFirestore.getInstance()

        // guarda toda informaçao referente a Gerentes
        reference = bd.collection("Gerentes")

        // ouvinte1()

        // ouvinte2()

        // ouvinte3()

        // ouvinte4()

        // ouvinte5()

        ouvinte6()

        // jeito 1
        //  var gerente = GerenteTeste("Antonio", 22, false)

        // jeito 2
        var gerente = Gerente("Antonio", 22, false)

        supportActionBar?.hide()
    }

    // 1ª opçao
    private fun ouvinte1() {

        bd.collection("Gerentes").document("gerente0").get().addOnSuccessListener { documento ->

            if (documento != null && documento.exists()) {

                // pega o nome da pasta onde esta os dados
                val key = documento.id

                val dados = documento.data
                val nome = dados?.get("nome").toString()
                val idade = dados?.get("idade")
                val fumante = dados?.get("fumante")

                binding.firestoreNome.setText(key + nome)
                binding.firestoreIdade.setText("$idade")
                binding.firestoreFumante.setText("$fumante")

            } else {
                Util.exibirToast(baseContext, "Erro ao ler Documento, esta vazio ou é inexistente")
            }

        }.addOnFailureListener { error ->
            Util.exibirToast(baseContext, "Erro ao ler dados do servidor")
        }

        /*
        //  2 º opçao
        bd.collection("Gerentes").document("gerente0").get().addOnCompleteListener{ task ->
            if(task.isSuccessful){

                val documento = task.result

                if(documento != null && documento.exists()){

                    val dados = documento.data
                    val nome = dados?.get("nome").toString()
                    val idade = dados?.get("idade")
                    val fumante = dados?.get("fumante")


                    binding.firestoreNome.setText(nome)
                    binding.firestoreIdade.setText("$idade")
                    binding.firestoreFumante.setText("$fumante")

                }else{
                    Util.exibirToast(baseContext, "Erro ao ler Documento, esta vazio ou é inexistente")
                }
            }else{
                Util.exibirToast(baseContext, "Erro ao ler dados do servidor")
            }
        }
         */

    }

    // 2ª opçao
    private fun ouvinte2() {

        val dialogProgress = DialogProgress()
        dialogProgress.show(supportFragmentManager, "0")

        bd.collection("Gerentes").document("gerente0").get().addOnSuccessListener { documento ->

            dialogProgress.dismiss()

            if (documento != null && documento.exists()) {

                // pega o nome da pasta onde esta os dados
                val key = documento.id

                val gerente = documento.toObject(Gerente::class.java)

                binding.firestoreNome.setText(key + gerente?.nome)
                binding.firestoreIdade.setText("${gerente?.idade}")
                binding.firestoreFumante.setText("${gerente?.fumante}")

            } else {
                Util.exibirToast(baseContext, "Erro ao ler Documento, esta vazio ou é inexistente")
            }

        }.addOnFailureListener { error ->
            dialogProgress.dismiss()
            Util.exibirToast(baseContext, "Erro ao ler dados do servidor")
        }

    }

    // 3ª opçao
    private fun ouvinte3() {

        val dialogProgress = DialogProgress()
        dialogProgress.show(supportFragmentManager, "0")

        var listaGerentes: MutableList<Gerente> = ArrayList<Gerente>();

        bd.collection("Gerentes").get().addOnSuccessListener { documentos ->

            dialogProgress.dismiss()

            if (documentos != null) {
                for (documento in documentos) {

                    val key = documento.id
                    /*
                    val nome = documento.get("nome").toString()
                    val idade = documento.get("idade").toString()
                    val fumante = documento.get("fumante").toString()
                     */

                    val gerente = documento.toObject(Gerente::class.java)

                    listaGerentes.add(gerente)

                    //exibindo no LogCat
                   // Log.d("www0r", "Nome pasta: ${key} -- Gerente nome: ${gerente.nome}  -- Idade: ${gerente.idade}"

                }

                binding.firestoreNome.setText("\n" + listaGerentes.get(0).nome)
                binding.firestoreIdade.setText("${listaGerentes.get(0).idade}")
                binding.firestoreFumante.setText("${listaGerentes.get(0).fumante}")

                binding.firestoreNome2.setText("\n" + listaGerentes.get(1).nome)
                binding.firestoreIdade2.setText("${listaGerentes.get(1).idade}")
                binding.firestoreFumante2.setText("${listaGerentes.get(1).fumante}")

            } else {
                Util.exibirToast(baseContext, "Erro ao ler Documento, esta vazio ou é inexistente")
            }

        }.addOnFailureListener { error ->
            dialogProgress.dismiss()
            Util.exibirToast(baseContext, "Erro ao ler dados do servidor")
        }

    }

    // 4ª opçao RECEBE DADOS EM TEMPO REAL
    private fun ouvinte4() {
        // obtem comunicaçao com Firebase e nao ocorre interrupçao
        bd.collection("Gerentes").document("gerente0").addSnapshotListener{ documento, error ->
            if(error != null){
                Util.exibirToast(baseContext, "Erro na comunicaçao com servidor: ${error.message.toString()} !")
            }else if(documento != null && documento.exists()){
                val key = documento.id

                val gerente = documento.toObject(Gerente::class.java)

                binding.firestoreNome.setText(key + gerente?.nome)
                binding.firestoreIdade.setText("${gerente?.idade}")
                binding.firestoreFumante.setText("${gerente?.fumante}")
            }else{
                Util.exibirToast(baseContext, "Esta pasta nao existe ou esta vazia")
            }

        }

    }

    // 5ª opçao RECEBE DADOS EM TEMPO REAL
    private fun ouvinte5() {

        val dialogProgress = DialogProgress()
        dialogProgress.show(supportFragmentManager, "0")

        var listaGerentes: MutableList<Gerente> = ArrayList<Gerente>();

        // obtem comunicaçao com Firebase e nao ocorre interrupçao
        // escuta para todas as pastas
        bd.collection("Gerentes").addSnapshotListener{ documentos, error ->
            if(error != null){

                dialogProgress.dismiss()
                Util.exibirToast(baseContext, "Erro na comunicaçao com servidor: ${error.message.toString()} !")
            }else if(documentos != null){
                dialogProgress.dismiss()

                // limpa lsita de informaçeos
                listaGerentes.clear()
                for (documento in documentos) {
                    val key = documento.id
                    val gerente = documento.toObject(Gerente::class.java)
                    listaGerentes.add(gerente)
                }
                binding.firestoreNome.setText("\n" + listaGerentes.get(0).nome)
                binding.firestoreIdade.setText("${listaGerentes.get(0).idade}")
                binding.firestoreFumante.setText("${listaGerentes.get(0).fumante}")

                binding.firestoreNome2.setText("\n" + listaGerentes.get(1).nome)
                binding.firestoreIdade2.setText("${listaGerentes.get(1).idade}")
                binding.firestoreFumante2.setText("${listaGerentes.get(1).fumante}")
            }else{

                dialogProgress.dismiss()
                Util.exibirToast(baseContext, "Esta pasta nao existe ou esta vazia")
            }

        }
    }


    // 6ª opçao RECEBE DADOS EM TEMPO REAL
    private fun ouvinte6() {
        val dialogProgress = DialogProgress()
        dialogProgress.show(supportFragmentManager, "0")

        // responsavel pela consulta
       // val query = reference!!.whereEqualTo("fumante", false)
       // val query = reference!!.whereLessThan("idade", 10)
        //val query = reference!!.whereGreaterThan("idade", 10)

        // cira indice
        // val query = reference!!.whereEqualTo("fumante", false).whereGreaterThan("idade", 10)

        // ordena os campos
        //val query = reference!!.orderBy("nome", Query.Direction.DESCENDING).limit(3)

        // busca 3 documentos qe iniciam com a letra L
       // val query = reference!!.orderBy("nome").startAt("L").limit(3)


        // busca 3 documentos qe iniciam com a letra L
        val query = reference!!.orderBy("nome").startAt("L").endAt("L"+ "\uf8ff").limit(3)

        // obtem comunicaçao com Firebase e nao ocorre interrupçao
        // escuta para todas as pastas
        query!!.addSnapshotListener{ documentos, error ->
            if(error != null){
                dialogProgress.dismiss()
                Util.exibirToast(baseContext, "Erro na comunicaçao com servidor: ${error.message.toString()} !")
            }else if(documentos != null){
                dialogProgress.dismiss()
                for(documento in documentos.documentChanges){
                    when(documento.type){
                        DocumentChange.Type.ADDED ->{
                            val key = documento.document.id
                            val gerente = documento.document.toObject(Gerente::class.java);
                            Log.d("www0r", "ADDED - : ${key} -- Gerente nome: ${gerente.nome} ")

                        }

                        DocumentChange.Type.MODIFIED ->{
                            val key = documento.document.id
                            val gerente = documento.document.toObject(Gerente::class.java);
                            Log.d("www0r", "MODIFIED - : ${key} -- Gerente nome: ${gerente.nome} ")
                        }

                        DocumentChange.Type.REMOVED ->{
                            val key = documento.document.id
                            val gerente = documento.document.toObject(Gerente::class.java);
                            Log.d("www0r", "REMOVED - : ${key} -- Gerente nome: ${gerente.nome} ")
                        }
                    }
                }
            }else{
                dialogProgress.dismiss()
                Util.exibirToast(baseContext, "Esta pasta nao existe ou esta vazia")
            }
        }
    }

}