package br.arc_camp.firebaseactivity.firestore

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.arc_camp.firebaseactivity.databinding.ActivityFirestoreLerDadosBinding
import com.br.jafapps.bdfirestore.util.Util
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreLerDadosActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFirestoreLerDadosBinding
    private lateinit var bd : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFirestoreLerDadosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bd = FirebaseFirestore.getInstance()

        //ouvinte1()

        ouvinte2()

        // jeito 1
       //  var gerente = GerenteTeste("Antonio", 22, false)

        // jeito 2
        var gerente = Gerente("Antonio", 22, false)

        supportActionBar?.hide()
    }

    // 1ª opçao
    private fun ouvinte1(){

        bd.collection("Gerentes").document("gerente0").get().addOnSuccessListener { documento ->

            if(documento != null && documento.exists()){

                // pega o nome da pasta onde esta os dados
                val key = documento.id

                val dados = documento.data
                val nome = dados?.get("nome").toString()
                val idade = dados?.get("idade")
                val fumante = dados?.get("fumante")

                binding.firestoreNome.setText(key + nome)
                binding.firestoreIdade.setText("$idade")
                binding.firestoreFumante.setText("$fumante")

            }else{
                Util.exibirToast(baseContext, "Erro ao ler Documento, esta vazio ou é inexistente")
            }

        }.addOnFailureListener{ error ->
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
    private fun ouvinte2(){

        bd.collection("Gerentes").document("gerente0").get().addOnSuccessListener { documento ->

            if(documento != null && documento.exists()){

                // pega o nome da pasta onde esta os dados
                val key = documento.id

                val gerente = documento.toObject(Gerente::class.java)

                binding.firestoreNome.setText(key + gerente?.nome)
                binding.firestoreIdade.setText("${gerente?.idade}")
                binding.firestoreFumante.setText("${gerente?.fumante}")

            }else{
                Util.exibirToast(baseContext, "Erro ao ler Documento, esta vazio ou é inexistente")
            }

        }.addOnFailureListener{ error ->
            Util.exibirToast(baseContext, "Erro ao ler dados do servidor")
        }

    }
}