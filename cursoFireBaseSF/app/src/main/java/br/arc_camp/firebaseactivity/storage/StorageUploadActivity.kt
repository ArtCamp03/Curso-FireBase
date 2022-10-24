package br.arc_camp.firebaseactivity.storage

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import br.arc_camp.firebaseactivity.R
import br.arc_camp.firebaseactivity.databinding.ActivityStorageUploadBinding
import com.br.jafapps.bdfirestore.util.DialogProgress
import com.br.jafapps.bdfirestore.util.Util
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.io.File

class StorageUploadActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityStorageUploadBinding
    private lateinit var uri_imagem: Uri
    private lateinit var storage: FirebaseStorage
    private var cont = 0

    private val register = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { image: Bitmap? ->
        image?.let {
            binding.imageStorageUpload.setImageBitmap(image)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStorageUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonStorageEnviar.setOnClickListener(this)

        storage = Firebase.storage

        //supportActionBar?.hide()

    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.button_storage_enviar -> {
                //uploadImagem1()
                //Util.exibirToast(baseContext, "Imagem enviada")

                if (uri_imagem != null) {
                    if (Util.statusInternet(this)) {
                        //uploadImage2()
                        // uploadImage3()
                        uploadImage4()
                    } else {
                        Util.exibirToast(baseContext, "Sem conexao com a internet")
                    }
                } else {
                    Util.exibirToast(baseContext, "Nenhuma imagem selecionada")
                }
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
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //  VERSAO MAIS NOVA DO ANDROID
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            //contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "NomeImagem")
            val resolver = contentResolver
            uri_imagem =
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)!!

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        } else {
            //  VERSAO MAIS ANTIGA DO ANDROID
            val autorizacao = "br.arc_camp.firebaseactivity"
            val diretorio =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val nomeImagem = diretorio.path + "/CursoFS" + System.currentTimeMillis() + ".jpg"
            val file = File(nomeImagem)
            uri_imagem = FileProvider.getUriForFile(baseContext, autorizacao, file)
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri_imagem)
        startActivityForResult(intent, 22)
    }

    // resposta da chamada de galeria
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 11 && data != null) {
                uri_imagem = data.data!!
                binding.imageStorageUpload.setImageURI(uri_imagem)
            } else if (requestCode == 22 && uri_imagem != null) {
                binding.imageStorageUpload.setImageURI(uri_imagem)
            }
        }
    }

    // ----------------------------     UPLOAD DE IMAGEM    ---------------------------

    // busca a imagem que esta dentro da ImageView
    private fun uploadImagem1() {

        val dialogProgress = DialogProgress()
        dialogProgress.show(supportFragmentManager, "1")

        val bitmap = (binding.imageStorageUpload.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos)
        val data = baos.toByteArray()

        // local para armazenamento da imagem
        val reference = storage.reference.child("imagens").child("uploadImagem1.jpg")

        var uploadTask = reference.putBytes(data)
        uploadTask.addOnSuccessListener {
            dialogProgress.dismiss()
            Util.exibirToast(baseContext, "Sucesso ao realizar upload da imagem")
        }.addOnFailureListener { error ->
            dialogProgress.dismiss()
            Util.exibirToast(
                baseContext,
                "Falha ao realizar upload da imagem: ${error.message.toString()}"
            )
        }
    }

    // busca imagem de forma direta
    private fun uploadImage2() {

        val dialogProgress = DialogProgress()
        dialogProgress.show(supportFragmentManager, "1")

        // local para armazenamento da imagem
        val reference = storage.reference.child("imagens").child("uploadImagem1.jpg")

        var uploadTask = reference.putFile(uri_imagem)

        uploadTask.addOnSuccessListener {
            dialogProgress.dismiss()
            Util.exibirToast(baseContext, "Sucesso ao realizar upload da imagem")
        }.addOnFailureListener { error ->
            dialogProgress.dismiss()
            Util.exibirToast(
                baseContext,
                "Falha ao realizar upload da imagem: ${error.message.toString()}"
            )
        }

    }

    // busca imagem de forma direta e obtem URL da imagem
    private fun uploadImage3() {

        val dialogProgress = DialogProgress()
        dialogProgress.show(supportFragmentManager, "1")

        // local para armazenamento da imagem
        val reference = storage.reference.child("imagens").child("uploadImagem1.jpg")

        var uploadTask = reference.putFile(uri_imagem)

        uploadTask.continueWithTask { task ->

            if (!task.isSuccessful) {
                // caso de algum erro
                task.exception.let {
                    throw it!!
                }
            }

            reference.downloadUrl

        }.addOnSuccessListener { task ->
            var url = task.toString()
            Log.d("TesteArt", url)
            dialogProgress.dismiss()

            Util.exibirToast(baseContext, "Sucesso ao realizar upload da imagem")
        }.addOnFailureListener { error ->

            dialogProgress.dismiss()
            Util.exibirToast(
                baseContext,
                "Falha ao realizar upload da imagem: ${error.message.toString()}"
            )
        }

        /*
            //     UTILZIANDO addOnCompleteListener

            uploadTask.continueWithTask{ task ->
                if(!task.isSuccessful){
                    // caso de algum erro
                    task.exception.let{
                        throw it!!
                    }
                }
                reference.downloadUrl
            }.addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val url = task.result.toString()
                    Util.exibirToast(baseContext, "Sucesso ao fazer upload da imagem")

                }else{
                    Util.exibirToast(baseContext, "Erro ao fazer upload da imagem: ${task.exception.toString()}")
                }
            }

         */

    }

    private fun uploadImage4() {

        // tratamento da imagem
        Glide.with(baseContext).asBitmap().
        load(uri_imagem).
        apply(RequestOptions.overrideOf(1024, 768)).listener(object :RequestListener<Bitmap>{
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Bitmap>?,
                isFirstResource: Boolean
            ): Boolean {
                Util.exibirToast(baseContext, "Falha ao diminuir da imagem:")
                return false
            }

            override fun onResourceReady(
                bitmap: Bitmap?,
                model: Any?,
                target: Target<Bitmap>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {

                val dialogProgress = DialogProgress()
                dialogProgress.show(supportFragmentManager, "1")

                val baos = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

                // local para armazenamento da imagem
                //val reference = storage.reference.child("imagens").child("uploadImagem1.jpg")
                val uid = Firebase.auth.currentUser?.uid
                val reference = storage.reference.child("arquivosUsuarios").child(uid.toString()).child("imagem"+ cont + ".jpg")
                cont += 1

                var uploadTask = reference.putBytes(data)
                uploadTask.addOnSuccessListener {
                    dialogProgress.dismiss()
                    Util.exibirToast(baseContext, "Sucesso ao realizar upload da imagem")
                }.addOnFailureListener { error ->
                    dialogProgress.dismiss()
                    Util.exibirToast(
                        baseContext,
                        "Falha ao realizar upload da imagem: ${error.message.toString()}"
                    )
                }

                return false
            }

        }).submit()

    }

}