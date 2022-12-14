package br.arc_camp.firebaseactivity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.arc_camp.firebaseactivity.databinding.ActivityMainBinding
import br.arc_camp.firebaseactivity.firestore.FireStoreGravarAlterarRemoverActivity
import br.arc_camp.firebaseactivity.firestore.FirestoreLerDadosActivity
import br.arc_camp.firebaseactivity.firestore_lista_categoria.FirestoreListaCategoriaActivity
import br.arc_camp.firebaseactivity.storage.StorageDownloadActivity
import br.arc_camp.firebaseactivity.storage.StorageUploadActivity
import com.br.jafapps.bdfirestore.util.Util
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cadViewMainLogout.setOnClickListener(this)
        binding.cadViewMainDownloadImg.setOnClickListener(this)
        binding.cadViewMainUploadImg.setOnClickListener(this)
        binding.cadViewMainLerDados.setOnClickListener(this)
        binding.cadViewMainCategorias.setOnClickListener(this)
        binding.cadViewMainGravarAlterarRemover.setOnClickListener(this)

        listenAuthentic()

        // metodo de permissao para funcionalidades do celular
        permission()

        supportActionBar?.hide()
    }

    override fun onClick(v: View) {
        if (v.id == R.id.cadView_main_logout) {
            finish()
            // desloga o usuario do Firebase
            Firebase.auth.signOut()
            startActivity(Intent(this, AberturaActivity::class.java))
        } else if (v.id == R.id.cadView_main_download_img) {
            startActivity(Intent(this, StorageDownloadActivity::class.java))
        } else if (v.id == R.id.cadView_main_upload_img) {
            startActivity(Intent(this, StorageUploadActivity::class.java))
        } else if (v.id == R.id.cadView_main_ler_dados) {
            startActivity(Intent(this, FirestoreLerDadosActivity::class.java))
        } else if (v.id == R.id.cadView_main_categorias) {
            startActivity(Intent(this, FirestoreListaCategoriaActivity::class.java))
        } else if (v.id == R.id.cadView_main_gravarAlterarRemover) {
            startActivity(Intent(this, FireStoreGravarAlterarRemoverActivity::class.java))
        }

    }

    // feedback da resposta de permissoes
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        for (result in grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                Util.exibirToast(
                    baseContext, "Aceite as permissoes para o app funcionar corretamente !!"
                )
                finish()
                break
            }
        }

    }


    // sempre escutara a respsota do Firebase
    private fun listenAuthentic() {
        Firebase.auth.addAuthStateListener { authentication ->

            if (authentication.currentUser != null) {
                Util.exibirToast(baseContext, "Usuarios logado")
            } else {
                Util.exibirToast(baseContext, "Usuarios deslogado")
            }

        }
    }

    private fun permission() {
        val permissoes = arrayOf<String>(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )

        Util.permissao(this, 100, permissoes)

    }

}