package br.arc_camp.firebaseactivity.firestore_lista_categoria

import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import br.arc_camp.firebaseactivity.R
import br.arc_camp.firebaseactivity.databinding.ActivityFirestoreListaCategoriaBinding
import com.br.jafapps.bdfirestore.util.DialogProgress
import com.br.jafapps.bdfirestore.util.Util
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class FirestoreListaCategoriaActivity : AppCompatActivity(), View.OnClickListener,
    SearchView.OnQueryTextListener, AdapterRecycleViewCategoria.ClickCategoria,
    AdapterRecycleViewCategoria.UltimoItemRecycleView {

    private lateinit var binding: ActivityFirestoreListaCategoriaBinding
    private lateinit var searchView: SearchView
    private lateinit var adapterRecycleViewCategoria: AdapterRecycleViewCategoria
    private var categorias: ArrayList<Categoria> = ArrayList()

    private var isFiltrando = false

    private lateinit var database: FirebaseFirestore
    private lateinit var reference: CollectionReference
    private lateinit var proxQuery: Query

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFirestoreListaCategoriaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonFirestoreListaCategorias.setOnClickListener(this)

        database = FirebaseFirestore.getInstance()
        reference = database.collection("Categorias")

        binding.buttonFirestoreListaCategorias.visibility = View.GONE

        iniciaRecycleView()

        exibirPrimeriosItensBD()

    }

    override fun onClose(): Boolean {
        isFiltrando = false
        searchView?.onActionViewCollapsed()
        categorias.clear()
        adapterRecycleViewCategoria?.notifyDataSetChanged()
        exibirPrimeriosItensBD()
        return true
    }

    override fun onClick(v: View?) {
        exibirMais()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.search, menu)
        val search = menu!!.findItem(R.id.action_search)
        searchView = search.actionView as SearchView

        searchView?.queryHint = "Pesquisar nome ..."

        searchView?.setOnQueryTextListener(this)
        searchView?.inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES

        /*
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                TODO("Not yet implemented")
            }
        })
         */

        searchView.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)

    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        isFiltrando = true

        pesquisaNome(newText.toString())
        return true
    }

    private fun iniciaRecycleView() {

        /*
            neste espaço pode ser colocado os dados que serao gravaedos no FireStore
         */

        adapterRecycleViewCategoria =
            AdapterRecycleViewCategoria(baseContext, categorias, this, this)

        binding.recycleViewListaCategorias.layoutManager = LinearLayoutManager(this)
        binding.recycleViewListaCategorias.adapter = adapterRecycleViewCategoria

    }

    override fun clickCategoria(categoria: Categoria) {
        Util.exibirToast(this, categoria.nome.toString())
        Util.exibirToast(this, categoria.id.toString())
    }

    // ultimo item exibido
    override fun ultimoItemRecycleView(isExibido: Boolean) {
        if(isExibido){
            Util.exibirToast(this, "Filtrando !")
        }else{
            exibirMais()
        }

    }

    private fun exibirPrimeriosItensBD() {

        val dialogProgress = DialogProgress()
        dialogProgress.show(supportFragmentManager, "1")

        var query = database!!.collection("Categorias").orderBy("nome").limit(10)

        query.get().addOnSuccessListener { documentos ->

            dialogProgress.dismiss()

            val ultimoDocumento = documentos.documents[documentos.size() - 1]
            proxQuery =
                database!!.collection("Categorias").orderBy("nome").startAfter(ultimoDocumento)
                    .limit(3)

            for (document in documentos) {
                val categoria = document.toObject(Categoria::class.java)
                categorias.add(categoria)
            }

            adapterRecycleViewCategoria?.notifyDataSetChanged()

        }.addOnFailureListener { error ->
            dialogProgress.dismiss()
            Util.exibirToast(baseContext, "Falha ao efetuar leitura: ${error}")
        }

    }

    private fun exibirMais() {

        val dialogProgress = DialogProgress()
        dialogProgress.show(supportFragmentManager, "1")

        proxQuery!!.get().addOnSuccessListener { documentos ->

            dialogProgress.dismiss()

            if (documentos.size() > 0) {
                val ultimoDocumento = documentos.documents[documentos.size() - 1]
                proxQuery =
                    database!!.collection("Categorias").orderBy("nome").startAfter(ultimoDocumento)
                        .limit(3)

                for (document in documentos) {
                    val categoria = document.toObject(Categoria::class.java)
                    categorias.add(categoria)
                }

                adapterRecycleViewCategoria?.notifyDataSetChanged()
            } else {
                Util.exibirToast(baseContext, "Fim da Lista !!")
                binding.buttonFirestoreListaCategorias.visibility = View.GONE
            }


        }.addOnFailureListener { error ->
            dialogProgress.dismiss()

            Util.exibirToast(baseContext, "Falha ao efetuar leitura: ${error}")
        }
    }

    private fun pesquisaNome(newText: String) {

        val query = database!!.collection("Categorias").orderBy("nome").
        startAt(newText).endAt(newText +"\uf8ff").limit(3)

        query!!.get().addOnSuccessListener { documentos ->

            categorias.clear()

                for (document in documentos) {
                    val categoria = document.toObject(Categoria::class.java)
                    categorias.add(categoria)
                }

                adapterRecycleViewCategoria?.notifyDataSetChanged()
            }.addOnFailureListener{
                Util.exibirToast(this, "nenhum Item encontrado !")

        }

    }

}