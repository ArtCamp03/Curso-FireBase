package br.arc_camp.firebaseactivity.firestore

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.arc_camp.firebaseactivity.R
import br.arc_camp.firebaseactivity.databinding.ActivityFireStoreGravarAlterarRemoverBinding
import com.br.jafapps.bdfirestore.util.DialogProgress
import com.br.jafapps.bdfirestore.util.Util
import com.google.firebase.firestore.FirebaseFirestore

class FireStoreGravarAlterarRemoverActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityFireStoreGravarAlterarRemoverBinding

    private lateinit var bd : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFireStoreGravarAlterarRemoverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonFirestoreSalvar.setOnClickListener(this)
        binding.buttonFirestoreAlterar.setOnClickListener(this)
        binding.buttonFirestoreRemover.setOnClickListener(this)

        bd = FirebaseFirestore.getInstance()

        supportActionBar?.hide()
    }

    override fun onClick(v: View) {

        when(v.id){

            R.id.button_firestore_salvar  ->{
                buttonSalvar()

            }

            R.id.button_firestore_alterar ->{
                buttonAlterar()

            }

            R.id.button_firestore_remover ->{
                buttonRemover()

            }

            else -> return
        }

    }

    private fun  buttonSalvar(){

        val nome = binding.editFirestoreNomeGerente.text.toString()
        val idadeString = binding.editFirestoreIdadeGerente.text.toString()
        val nomePasta = binding.editFirestoreNomePasta.text.toString()

        if(!nome.trim().isEmpty() && !idadeString.trim().isEmpty() && !nomePasta.trim().isEmpty()){
            if(Util.statusInternet(baseContext)){

                val idade =  idadeString.toInt()

                salvarDados(nomePasta, nome, idade)

            }else{
                Util.exibirToast(baseContext, "Sem conexao com a internet !!")
            }
        }else{
            Util.exibirToast(baseContext, "Insira todos os campos corretamente!!")
        }
    }

    private fun  buttonAlterar(){
        val nome = binding.editFirestoreNomeGerente.text.toString()
        val idadeString = binding.editFirestoreIdadeGerente.text.toString()
        val nomePasta = binding.editFirestoreNomePasta.text.toString()

        if(!nome.trim().isEmpty() && !idadeString.trim().isEmpty() && !nomePasta.trim().isEmpty()){
            if(Util.statusInternet(baseContext)){

                val idade =  idadeString.toInt()

                alterarDados(nomePasta, nome, idade)

            }else{
                Util.exibirToast(baseContext, "Sem conexao com a internet !!")
            }
        }else{
            Util.exibirToast(baseContext, "Insira todos os campos corretamente!!")
        }
    }

    private fun  buttonRemover(){
        val nomePasta = binding.editFirestoreNomePasta.text.toString()
        if(!nomePasta.trim().isEmpty()){
            if(Util.statusInternet(baseContext)){
                removerDados(nomePasta)
            }else{
                Util.exibirToast(baseContext, "Sem conexao com a internet !!")
            }
        }else{
            Util.exibirToast(baseContext, "Insira todos os campos corretamente!!")
        }
    }

    private fun salvarDados(nomePasta: String, nome: String, idade:Int){

        val dialogProgress = DialogProgress()
        dialogProgress.show(supportFragmentManager, "0")

        val reference = bd.collection("Gerentes")

        val gerente = Gerente(nome, idade, true)

        /*
            var data = hashMapOf<String, Any>(
            "nome" to nome,
            "idade" to idade,
            "fumante" to false
            )

         */

        // gera nome de pasta aleatoria
        //reference.add(gerente).addOnSuccessListener {
        //  reference.document().set(gerente).addOnSuccessListener

        reference.document(nomePasta).set(gerente).addOnSuccessListener {
            dialogProgress.dismiss()
            Util.exibirToast(baseContext, "Dados gravados com sucesso !!")

        }.addOnFailureListener{ error ->
            dialogProgress.dismiss()
            Util.exibirToast(baseContext, "Erro ao gravar dados: ${error} !!")
        }

    }

    private fun alterarDados(nomePasta: String, nome: String, idade:Int){
        val dialogProgress = DialogProgress()
        dialogProgress.show(supportFragmentManager, "0")

        val reference = bd.collection("Gerentes")

        val gerente = hashMapOf<String, Any>(
            "nome" to nome,
            "idade" to idade,
            "fumante" to false
        )

        reference.document(nomePasta).update(gerente).addOnSuccessListener {
            dialogProgress.dismiss()
            Util.exibirToast(baseContext, "Dados alterados com sucesso!!")
        }.addOnFailureListener{ error ->
            dialogProgress.dismiss()
            Util.exibirToast(baseContext, "Erro ao alterar dados : ${error}!!")
        }

    }

    private fun removerDados(nomePasta: String){
        val dialogProgress = DialogProgress()
        dialogProgress.show(supportFragmentManager, "0")

        val reference = bd.collection("Gerentes")

        reference.document(nomePasta).delete().addOnSuccessListener {
            dialogProgress.dismiss()
            Util.exibirToast(baseContext, "Dados removidos com sucesso!!")
        }.addOnFailureListener{ error ->
            dialogProgress.dismiss()
            Util.exibirToast(baseContext, "Erro ao remover dados : ${error}!!")
        }

    }
}