package br.arc_camp.firebaseactivity.firestore_lista_categoria

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import br.arc_camp.firebaseactivity.R
import br.arc_camp.firebaseactivity.databinding.ActivityFirestoreListaCategoriaBinding
import com.br.jafapps.bdfirestore.util.Util
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class FirestoreListaCategoriaActivity : AppCompatActivity(), View.OnClickListener,
    SearchView.OnQueryTextListener, AdapterRecycleViewCategoria.ClickCategoria {

    private lateinit var binding : ActivityFirestoreListaCategoriaBinding
    private lateinit var searchView : SearchView
    private lateinit var adapterRecycleViewCategoria: AdapterRecycleViewCategoria
    private  var categorias : ArrayList<Categoria> = ArrayList()

    private lateinit var database : FirebaseFirestore
    private lateinit var reference : CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFirestoreListaCategoriaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonFirestoreListaCategorias.setOnClickListener(this)

        database = FirebaseFirestore.getInstance()
        reference = database.collection("Categorias")

        iniciaRecycleView()

        exibirPrimeriosItensBD()

    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.search, menu)
        val search = menu!!.findItem(R.id.action_search)
        searchView = search.actionView as SearchView

        searchView?.queryHint = "Pesquisar nome ..."

        searchView?.setOnQueryTextListener(this)

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

        searchView.setOnCloseListener(object : SearchView.OnCloseListener{
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
        return true
    }

    private fun iniciaRecycleView(){

        /*
            neste espaço pode ser colocado os dados que serao gravaedos no FireStore
         */

        adapterRecycleViewCategoria = AdapterRecycleViewCategoria(baseContext, categorias, this)

        binding.recycleViewListaCategorias.layoutManager = LinearLayoutManager(this)
        binding.recycleViewListaCategorias.adapter = adapterRecycleViewCategoria

    }

    override fun clickCategoria(categoria: Categoria) {
        Util.exibirToast(this, categoria.nome.toString())
    }

    private fun exibirPrimeriosItensBD(){

        var query = database!!.collection("Categorias").orderBy("nome")

        query.get().addOnSuccessListener {  documentos ->

            for(document in documentos){
                val categoria = document.toObject(Categoria::class.java)
                categorias.add(categoria)
            }

            adapterRecycleViewCategoria?.notifyDataSetChanged()

            Util.exibirToast(baseContext,"Leitura feita com sucesso")
        }.addOnFailureListener{ error ->
            Util.exibirToast(baseContext,"Falha ao efetuar leitura: ${error}")
        }

    }

}