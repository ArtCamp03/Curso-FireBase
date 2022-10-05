package br.arc_camp.firebaseactivity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.arc_camp.firebaseactivity.databinding.ActivityMainBinding
import com.br.jafapps.bdfirestore.util.Util
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cadViewMainLogout.setOnClickListener(this)

        listenAuthentic()

        supportActionBar?.hide()
    }

    override fun onClick(v: View) {
        when(v.id){

            R.id.cadView_main_logout -> {
                finish()

                // desloga o usuario do Firebase
                Firebase.auth.signOut()

                startActivity(Intent(this, AberturaActivity::class.java))

            }
        }
    }

    // sempre escutara a respsota do Firebase
    private fun listenAuthentic(){
        Firebase.auth.addAuthStateListener { authentication ->

            if(authentication.currentUser != null){
                Util.exibirToast(baseContext, "Usuarios logado")
            }else{
                Util.exibirToast(baseContext, "Usuarios deslogado")
            }

        }
    }

}