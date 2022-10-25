package br.arc_camp.firebaseactivity.firestore_lista_item

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.arc_camp.firebaseactivity.R
import br.arc_camp.firebaseactivity.databinding.ActivityFirestoreListaItemBinding
import br.arc_camp.firebaseactivity.firestore.Gerente
import br.arc_camp.firebaseactivity.firestore_lista_categoria.Categoria
import com.br.jafapps.bdfirestore.util.DialogProgress
import com.br.jafapps.bdfirestore.util.Util
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class FirestoreListaItemActivity : AppCompatActivity(), View.OnClickListener,
    AdapterRecycleViewItem.ClickItem {

    private lateinit var binding: ActivityFirestoreListaItemBinding
    private lateinit var categoria: Categoria

    private lateinit var adapterRecycleViewItem: AdapterRecycleViewItem
    private var itens: ArrayList<Item> = ArrayList()
    private lateinit var database: FirebaseFirestore
    private lateinit var reference: CollectionReference

    // tratamento de upload
    private var uri_imagem: Uri? = null

    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirestoreListaItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        categoria = intent.getParcelableExtra("categoriaNome")!!
        supportActionBar?.title = categoria?.nome

        database = FirebaseFirestore.getInstance()

        binding.buttonFirestoreItemSalvar.setOnClickListener(this)
        binding.imageviewFirestoreLimparItem.setOnClickListener(this)
        binding.imageViewFirestoreGaleria.setOnClickListener(this)

        iniciarRecycleView()

        storage = Firebase.storage

        ouvinte()
    }

    override fun onClick(v: View) {
        when (v.id) {

            R.id.button_firestore_item_salvar -> {
                buttonSalvar()
            }

            R.id.imageview_firestore_limpar_item -> {
                limparCampos()
            }

            R.id.imageView_firestore_galeria -> {
                obterImageGallery()
            }

            else -> return
        }
    }

    override fun clickItem(item: Item) {
        // Util.exibirToast(baseContext, item.nome.toString())
        val intente = Intent(this,FirestoreItemDadosActivity::class.java)
        intente.putExtra("idCategoria", categoria.id)
        intente.putExtra("item", item)
        startActivity(intente)
    }

    // resposta da chamada de galeria
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 11 && data != null) {
                uri_imagem = data.data!!
                binding.imageViewFirestoreGaleria.setImageURI(uri_imagem)
            }
        }
    }

    private fun iniciarRecycleView() {
        adapterRecycleViewItem = AdapterRecycleViewItem(baseContext, itens, this)
        binding.recycleViewLsitaItem.layoutManager = LinearLayoutManager(this)
        binding.recycleViewLsitaItem.adapter = adapterRecycleViewItem
    }

    private fun ouvinte() {

        val dialogProgress = DialogProgress()
        dialogProgress.show(supportFragmentManager, "1")

        reference = database!!.collection("Categorias").document(categoria?.id.toString())
            .collection("itens")

        // atualiza e mostra qualquer alteraçao feita dentro do documento presente na variavel refence
        reference?.addSnapshotListener { documentos, error ->

            dialogProgress.dismiss()

            if (error != null) {
                Util.exibirToast(baseContext, "Erro ao ler dados: ${error}")
            }else {
                for (doc in documentos!!.documentChanges) {
                    when (doc.type) {
                        DocumentChange.Type.ADDED -> {
                            // pega variavel uma por uma
                            //var data = doc.document.data
                            //var nome = data.get("nome").toString()

                            // pega todas as variaveis de uma vez
                            val item = doc.document.toObject(Item::class.java)
                            itens.add(item)

                            adapterRecycleViewItem?.notifyDataSetChanged()
                        }

                        DocumentChange.Type.MODIFIED -> {
                            val item = doc.document.toObject(Item::class.java)

                            // Log.d("eee", item.id.toString())
                            // Log.d("eee", item.nome.toString())

                            val key = doc.document.id.toInt()

                            val index = itens.indexOfFirst { i ->
                                i.id == key
                            }

                            itens.set(index, item)

                            adapterRecycleViewItem?.notifyDataSetChanged()
                        }

                        DocumentChange.Type.REMOVED -> {
                            val item = doc.document.toObject(Item::class.java)
                            val key = item.id

                            val index = itens.indexOfFirst {
                                it.id == key
                            }

                            itens.removeAt(index)
                            adapterRecycleViewItem?.notifyItemChanged(index, itens.size)
                            adapterRecycleViewItem?.notifyItemRemoved(index)

                        }
                    }
                }
            }
        }
    }

    private fun obterImageGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(Intent.createChooser(intent, "Escolha uma imagem"), 11)

    }

    private fun buttonSalvar(){
        val nome = binding.editTextFirestoreItemNome.text.toString()
        val descricao = binding.editTextFirestoreItemDescricao.text.toString()

        if(!nome.trim().isEmpty() && !descricao.trim().isEmpty()){
            if(Util.statusInternet(this)){
                if(uri_imagem != null){
                    uploadImagem(nome, descricao)
                }else{
                    Util.exibirToast(this, "selecione uma imagem")
                }
            }else{
                Util.exibirToast(this, "Nao a conexao com a internet")
            }
        }else{
            Util.exibirToast(this, "Preencha os campos obrigatorios")
        }
    }

    private fun uploadImagem(nome:String, descricao:String){
        val dialogProgress = DialogProgress()
        dialogProgress.show(supportFragmentManager, "1")

        val idItem = System.currentTimeMillis().toInt()

        val nomeImg = "$idItem.jpg"

        // local para armazenamento da imagem
        val reference = storage.reference.
        child("Categorias").
        child(categoria?.id.toString()).
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

            salvarDados(nome, descricao, url, idItem)
        }.addOnFailureListener { error ->

            dialogProgress.dismiss()
            Util.exibirToast(
                baseContext,
                "Falha ao realizar upload da imagem: ${error.message.toString()}"
            )
        }

    }

    private fun limparCampos(){
        binding.editTextFirestoreItemNome.setText("")
        binding.editTextFirestoreItemDescricao.setText("")

        uri_imagem = null
        binding.imageViewFirestoreGaleria.setImageResource(R.drawable.ic_gallery_img)

    }

    private fun salvarDados(nome:String, descricao:String, url:String, idItem:Int){
        val dialogProgress = DialogProgress()
        dialogProgress.show(supportFragmentManager, "0")

        //var nomeDocumento = System.currentTimeMillis().toInt()

        val nomeDocumento = idItem
        val reference = database!!.collection("Categorias").document(categoria?.id.toString()).collection("itens")
        val item = Item(nomeDocumento,nome, descricao, url)

        reference.document(nomeDocumento.toString()).set(item).addOnSuccessListener {
            dialogProgress.dismiss()
            Util.exibirToast(baseContext, "Dados gravados com sucesso !!")
            limparCampos()
        }.addOnFailureListener{ error ->
            dialogProgress.dismiss()
            Util.exibirToast(baseContext, "Erro ao gravar dados: ${error} !!")
        }
    }

}