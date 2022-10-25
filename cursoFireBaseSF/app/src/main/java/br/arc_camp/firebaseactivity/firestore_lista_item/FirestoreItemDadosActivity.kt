package br.arc_camp.firebaseactivity.firestore_lista_item

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import br.arc_camp.firebaseactivity.R
import br.arc_camp.firebaseactivity.databinding.ActivityFirestoreItemDadosBinding
import com.br.jafapps.bdfirestore.util.DialogProgress
import com.br.jafapps.bdfirestore.util.Util
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class FirestoreItemDadosActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding : ActivityFirestoreItemDadosBinding
    private var itemSelecionado : Item? = null

    private var uri_imagem: Uri? = null

    private var idCategoria: Int? = null

    private lateinit var storage: FirebaseStorage

    private lateinit var bd : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFirestoreItemDadosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonItemDadosAtualziar.setOnClickListener(this)
        binding.buttonItemDadosRemover.setOnClickListener(this)
        binding.imagemviewItemDados.setOnClickListener(this)

        bd = FirebaseFirestore.getInstance()

        itemSelecionado = intent.getParcelableExtra("item")

        idCategoria = intent.getIntExtra("idCategoria",0)

        Util.exibirToast(this, "ID categoria= ${idCategoria}")

        storage = Firebase.storage

        atualizarDados()

    }

    override fun onClick(v: View) {
        when(v.id){

            R.id.button_item_dados_remover -> {
               buttoRemover()
            }

            R.id.button_item_dados_atualziar -> {
                buttonAtualizar()
            }

            R.id.imagemview_item_dados -> {
                obterImageGallery()
            }

        }

    }

    private fun atualizarDados(){

        binding.edittextItemDadosDescricao.setText(itemSelecionado?.descricao)
        binding.edittextItemDadosNome.setText(itemSelecionado?.nome)

        Glide.with(this).asBitmap().load(itemSelecionado?.url_image).listener(object : RequestListener<Bitmap>{
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Bitmap>?,
                isFirstResource: Boolean
            ): Boolean {
                binding.progressbarItemDados.visibility = View.GONE
                return false
            }

            override fun onResourceReady(
                resource: Bitmap?,
                model: Any?,
                target: Target<Bitmap>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                binding.progressbarItemDados.visibility = View.GONE
                return false
            }

        }).into(binding.imagemviewItemDados)

    }

    private fun obterImageGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(Intent.createChooser(intent, "Escolha uma imagem"), 11)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 11 && data != null) {
                uri_imagem = data.data!!
                binding.imagemviewItemDados.setImageURI(uri_imagem)
            }
        }
    }

    private fun buttonAtualizar(){
        val descricao = binding.edittextItemDadosDescricao.text.toString()
        val nome = binding.edittextItemDadosNome.text.toString()

        if(!itemSelecionado?.nome.equals(nome) || !itemSelecionado?.descricao.equals(descricao) || uri_imagem != null){

            if(!nome.trim().isEmpty() || !descricao.trim().isEmpty()){
                if(Util.statusInternet(baseContext)){
                    if(uri_imagem != null){
                        uploadAtualziarImagem(nome, descricao)
                    }else{
                        atualizarDados(nome, descricao, itemSelecionado?.url_image.toString())
                    }
                }else{
                    Util.exibirToast(this, "Nao a conexao com a internet")
                }
            }else{
                Util.exibirToast(this, "Preenchimento de campos obrigatorios")
            }
        }else{
            Util.exibirToast(this, "Nenhuma imagem foi alterada")
        }
    }

    private fun uploadAtualziarImagem(nome:String, descricao:String){
        val dialogProgress = DialogProgress()
        dialogProgress.show(supportFragmentManager, "1")

        val nomeImg = itemSelecionado?.id.toString() + ".jpg"

        // local para armazenamento da imagem
        val reference = storage.reference.
        child("Categorias").
        child(idCategoria.toString()).
        child("itens").
        child(nomeImg)

        var uploadTask = reference.putFile(uri_imagem!!)

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
            dialogProgress.dismiss()
            atualizarDados(nome, descricao, url)
        }.addOnFailureListener { error ->
            dialogProgress.dismiss()
            Util.exibirToast(
                baseContext,
                "Falha ao realizar upload da imagem: ${error.message.toString()}"
            )
        }

    }

    private fun atualizarDados(nome:String, descricao:String, url:String){
        val dialogProgress = DialogProgress()
        dialogProgress.show(supportFragmentManager, "0")

        val reference = bd.collection("Categorias").document(idCategoria.toString()).
                collection("itens")

        val item = hashMapOf<String, Any>(
           // "id" to itemSelecionado?.id!!,
            "nome" to nome,
            "descricao" to descricao,
            "url_image" to url
        )

        reference.document(itemSelecionado?.id.toString()).update(item).addOnSuccessListener {
            dialogProgress.dismiss()
            Util.exibirToast(baseContext, "Dados alterados com sucesso!!")
            finish()
        }.addOnFailureListener{ error ->
            dialogProgress.dismiss()
            Util.exibirToast(baseContext, "Erro ao alterar dados : ${error}!!")
            finish()
        }
    }

    private fun buttoRemover(){
        val idItem = itemSelecionado!!.id!!
        var url = itemSelecionado?.url_image!!
        removerImagem(idItem,url)
    }

    private fun removerImagem(idItem : Int, url:String){
        val reference = storage!!.getReferenceFromUrl(url)
        reference.delete().addOnSuccessListener {
            Util.exibirToast(baseContext, "Sucesso ao remover imagem")
            removerDados(idItem)
        }.addOnFailureListener {    error ->
            Util.exibirToast(baseContext, "Falha ao remover imagem: ${error.message.toString()}")
        }
    }

    private fun removerDados(idItem: Int){
        val dialogProgress = DialogProgress()
        dialogProgress.show(supportFragmentManager, "0")

        val reference = bd!!.collection("Categorias").document(idCategoria.toString()).
        collection("itens")

        reference.document(idItem.toString()).delete().addOnSuccessListener {
            dialogProgress.dismiss()
            Util.exibirToast(baseContext, "Dados removidos com sucesso!!")
            finish()
        }.addOnFailureListener{ error ->
            dialogProgress.dismiss()
            Util.exibirToast(baseContext, "Erro ao remover dados : ${error}!!")
            finish()
        }

    }

}