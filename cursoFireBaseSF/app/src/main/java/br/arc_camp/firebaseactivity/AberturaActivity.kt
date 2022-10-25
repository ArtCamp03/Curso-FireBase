package br.arc_camp.firebaseactivity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.arc_camp.firebaseactivity.databinding.ActivityAberturaBinding
import com.br.jafapps.bdfirestore.util.DialogProgress
import com.br.jafapps.bdfirestore.util.Util
import com.google.firebase.auth.FirebaseAuth

class AberturaActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding : ActivityAberturaBinding
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAberturaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLogin.setOnClickListener(this)

        auth = FirebaseAuth.getInstance()

        val user = auth?.currentUser

        // usuario que ja foi logado
        if(user != null){
            finish()

            /*
            // recupera informaçoes do usuarios logado
            user.email
            user.uid
             */

            Toast.makeText(this,"Sucesso ao logar !!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,MainActivity::class.java))

        }

        supportActionBar?.hide()
    }

    override fun onClick(v: View) {
        if(v.id == R.id.button_login){
            buttonLogin()
        }

    }

    private fun buttonLogin(){
        val email = binding.editLoginEmail.text.toString()
        val senha = binding.editLoginSenha.text.toString()

        if(!email.trim().equals("") && !senha.trim().equals("")){
            if(Util.statusInternet(this)){
                login(email, senha)
            }else{
                Toast.makeText(this,"Falha com a conexao !!", Toast.LENGTH_SHORT).show()
            }
            //Toast.makeText(this, "Email : ${email} " + "Senha : " + senha, Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this," Usuario ou senha invalidos", Toast.LENGTH_SHORT).show()
        }

    }

    private fun login(email: String, senha: String){

        val dialogoProgress = DialogProgress()
        dialogoProgress.show(supportFragmentManager, "1")

        // cria usaurios com email e senha
        //auth.createUserWithEmailAndPassword(email,senha)

        auth.signInWithEmailAndPassword(email,senha).addOnSuccessListener {
            dialogoProgress.dismiss()

            finish()

            // executa comandos se houver sucesso
            Toast.makeText(this,"Sucesso ao logar !!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,MainActivity::class.java))

        }.addOnFailureListener{ err ->

            dialogoProgress.dismiss()
            // executa comandos se houver falha

            // tratar erro do firebase
            val erro = err.message.toString()
            errosFirebase(erro)
        }

        // faz login com usuario ja existente
        /*
        Verifica se a variavel task

        auth.signInWithEmailAndPassword(email,senha).addOnCompleteListener(this){ task ->
            if(task.isSuccessful){
                Toast.makeText(this,"Sucesso ao logar !!", Toast.LENGTH_SHORT).show()
            }else{
                //Toast.makeText(this,"Falha ao logar ${task.exception.toString()} !!", Toast.LENGTH_SHORT).show()

                // tratar erro do firebase
                val erro = task.exception.toString()

                errosFirebase(erro)

                // utiliza o logcat para debug
                Log.d("testeArt", task.exception.toString())

            }
        }

         */
    }

    private fun errosFirebase(erro: String){

        if(erro.contains("The email address is badly formatted")){
            Util.exibirToast(baseContext, "Email invalido")
        }else if(erro.contains("There is no user record corresponding to this identifier")){
            Util.exibirToast(baseContext, "Email nao existente")
        }else if(erro.contains("The password is invalid or the user does not have a password")){
            Util.exibirToast(baseContext, "Senha invalida")
        }
    }

}