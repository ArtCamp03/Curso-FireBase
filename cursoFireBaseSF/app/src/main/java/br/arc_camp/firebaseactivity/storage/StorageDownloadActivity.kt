package br.arc_camp.firebaseactivity.storage

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.arc_camp.firebaseactivity.R
import br.arc_camp.firebaseactivity.databinding.ActivityStorageDownloadBinding
import com.br.jafapps.bdfirestore.util.Util
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class StorageDownloadActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding : ActivityStorageDownloadBinding
    private lateinit var storage : FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStorageDownloadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonStorageDownloadDownload.setOnClickListener(this)
        binding.buttonStorageDownloadRemover.setOnClickListener(this)

        binding.progressBarStorageDownload.visibility = View.GONE

        // variavel capaz de acessar opçoes de storage proveniente do Firebase
        storage = Firebase.storage

        supportActionBar?.hide()
    }

    override fun onClick(v: View) {

        when(v.id){

            R.id.button_storageDownload_download -> {
                buttonDownload()
            }

            R.id.button_storageDownload_remover -> {
                buttonRemover()
            }
        }
    }

    //  ------------------  DOWNLOAD DA IMAGEM  ---------------------

    private fun buttonDownload(){

        // verifica se a conexao com a internet
        if(Util.statusInternet(baseContext)){
            // downloadImage_1()

            // downloadImage_2()

            downloadImage_3()
        }else{
            Util.exibirToast(baseContext, "Falha ao conectar")
        }

    }

    // 1º metodo para se fazer download de imagem no firebase
    private fun downloadImage_1(){
        val urlImagem = "https://firebasestorage.googleapis.com/v0/b/fir-cursosf-405c5.appspot.com/o/imagem1%2Fusuario.jpg?alt=media&token=aa2c344d-85ba-46df-9660-2aab2035fd85"
        Glide.with(baseContext).asBitmap().load(urlImagem).placeholder(R.drawable.ic_progress).into(binding.imageViewStoragedownload)
    }

    // 2º metodo para se fazer download de imagem no firebase
    private fun downloadImage_2(){

        binding.progressBarStorageDownload.visibility = View.VISIBLE

        val urlImagem = "https://firebasestorage.googleapis.com/v0/b/fir-cursosf-405c5.appspot.com/o/imagem1%2Fusuario.jpg?alt=media&token=aa2c344d-85ba-46df-9660-2aab2035fd85"
        Glide.with(baseContext).asBitmap().load(urlImagem).listener(object : RequestListener<Bitmap>{
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Bitmap>?,
                isFirstResource: Boolean
            ): Boolean {
                Util.exibirToast(baseContext, "Falha ao fazer download: ${e.toString()}")
                return false
            }

            override fun onResourceReady(
                resource: Bitmap?,
                model: Any?,
                target: Target<Bitmap>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                binding.progressBarStorageDownload.visibility = View.GONE
                Util.exibirToast(baseContext, "Download concluido")
                return false
            }

        }).into(binding.imageViewStoragedownload)
    }

    // 3º metodo para se fazer download de imagem no firebase
    private fun downloadImage_3(){

        // fora da pasta
        // val references = storage!!.reference.child("usuarios2.png")

        // dentro de pasta
        val references = storage!!.reference.child("imagem1").child("usuario.jpg")

        // resposta da comunicaçao deita com firebase
        references.downloadUrl.addOnSuccessListener { task ->

            // val url = task
            //Log.d("testeURL", url.toString())
            Glide.with(baseContext).asBitmap().load(task).placeholder(R.drawable.ic_progress).into(binding.imageViewStoragedownload)

        }.addOnFailureListener{ error ->
            Util.exibirToast(baseContext, "Falha ao procurar imagem")
        }

    }


    //  ------------------  REMOVER IMAGEM  ---------------------

    private fun buttonRemover(){

        // verifica se a conexao com a internet
        if(Util.statusInternet(baseContext)){
            // removerImgURL1()

            removerImgURL2()

        }else{
            Util.exibirToast(baseContext, "Falha ao conectar")
        }

    }

    // 1º metodo para remover imagem utilizando a URL do firebase
    private fun removerImgURL1(){
        val urlImagem = "https://firebasestorage.googleapis.com/v0/b/fir-cursosf-405c5.appspot.com/o/imagem1%2Fusuario.jpg?alt=media&token=aa2c344d-85ba-46df-9660-2aab2035fd85"
        val reference = storage!!.getReferenceFromUrl(urlImagem)

        // verifica a resposta com o Firebase
        reference.delete().addOnSuccessListener {
            Util.exibirToast(baseContext, "Sucesso ao remover imagem")
        }.addOnFailureListener {    error ->
            Util.exibirToast(baseContext, "Falha ao remover imagem: ${error.message.toString()}")
        }
    }

    // 2º metodo para remover imagem utilziando o nome do firebase
    private fun removerImgURL2(){

        // val nome = "usuario.jpg"

        // dentro de pasta

        val references = storage!!.reference.child("imagem1").child("usuario.jpg")

        // verifica a resposta com o Firebase
        references.delete().addOnSuccessListener {
            Util.exibirToast(baseContext, "Sucesso ao remover imagem")
        }.addOnFailureListener {    error ->
            Util.exibirToast(baseContext, "Falha ao remover imagem: ${error.message.toString()}")
        }
    }

}