package br.arc_camp.firebaseactivity.firestore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import br.arc_camp.firebaseactivity.R
import br.arc_camp.firebaseactivity.databinding.ActivityFireStoreGravarAlterarRemoverBinding
import com.br.jafapps.bdfirestore.util.Util

class FireStoreGravarAlterarRemoverActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityFireStoreGravarAlterarRemoverBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFireStoreGravarAlterarRemoverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonFirestoreSalvar.setOnClickListener(this)
        binding.buttonFirestoreAlterar.setOnClickListener(this)
        binding.buttonFirestoreRemover.setOnClickListener(this)

        supportActionBar?.hide()
    }

    override fun onClick(v: View) {

        when(v.id){

            R.id.button_firestore_salvar  ->{
                buttonSalvar()
               // Util.exibirToast(baseContext, "Salvar!!")
            }

            R.id.button_firestore_alterar ->{
                buttonAlterar()
                //Util.exibirToast(baseContext, "Alterar!!")
            }

            R.id.button_firestore_remover ->{
                buttonRemover()
                //Util.exibirToast(baseContext, "Remover!!")
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

    }

    private fun  buttonRemover(){

    }

    private fun salvarDados(nomePasta: String, nome: String, idade:Int){
        Util.exibirToast(baseContext, "itens salvos nome: ${nome} idade: ${idade} nome da pasta: ${nomePasta}")
    }

}