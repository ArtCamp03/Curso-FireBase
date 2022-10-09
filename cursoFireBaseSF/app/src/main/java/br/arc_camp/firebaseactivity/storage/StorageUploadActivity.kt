package br.arc_camp.firebaseactivity.storage

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.arc_camp.firebaseactivity.R
import br.arc_camp.firebaseactivity.databinding.ActivityStorageUploadBinding
import com.br.jafapps.bdfirestore.util.Util

class StorageUploadActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityStorageUploadBinding
    private lateinit var uri_imagem : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStorageUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonStorageEnviar.setOnClickListener(this)

        //supportActionBar?.hide()
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.button_storage_enviar -> {
                Util.exibirToast(baseContext, "Imagem enviada")
            }

        }
    }

    // --------------------- MENU SUPERIOR DIREITO --------------------------

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_storage_upload, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.menu_item_gallery -> {
                obterImageGallery()
            }

            R.id.menu_item_camera -> {
                obterImageCamera()
            }
            else -> return true

        }

        return super.onOptionsItemSelected(item)
    }

    // opçao de galeria
    private fun obterImageGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(Intent.createChooser(intent, "Escolha uma imagem"), 11)
    }

    // opçao de camera
    private fun obterImageCamera() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(Intent.createChooser(intent, "Escolha uma imagem"), 100)
    }

    // resposta da chamada de galeria
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Activity.RESULT_OK) {
            if (requestCode == 11 && data != null) {
                uri_imagem = data.data
                binding.imageStorageUpload.setImageURI(uri)

            } else if (requestCode == 100 && data != null) {
                val uri = data.data
                binding.imageStorageUpload.setImageURI(uri)

            }
        }

    }


}