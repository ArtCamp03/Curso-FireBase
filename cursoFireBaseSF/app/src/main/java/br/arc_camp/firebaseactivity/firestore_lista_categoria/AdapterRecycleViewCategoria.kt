package br.arc_camp.firebaseactivity.firestore_lista_categoria

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import br.arc_camp.firebaseactivity.R

class AdapterRecycleViewCategoria(val context: Context,
                                  var categorias : ArrayList<Categoria>,
                                  var clickCategoria: ClickCategoria,
                                  var ultimoItemRecycleView: UltimoItemRecycleView):
    RecyclerView.Adapter<AdapterRecycleViewCategoria.ViewHolder>() {

    // pega os elementos dentro da lista
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val nome = itemView.findViewById<TextView>(R.id.textView_listaItem_item_nome)
        val cardView = itemView.findViewById<CardView>(R.id.cardView_lsita_categoria)
    }

    // responsavel pelo layout dos itens
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_lista_categoria_recycleview, parent, false)
        val holder = ViewHolder(view)
        return holder
    }


    // insere informaçeos dentro da lista
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val categoria : Categoria = categorias.get(position)
        holder.nome.text = categoria.nome

        holder.cardView.setOnClickListener{
            clickCategoria.clickCategoria(categoria)
        }

        if(position == getItemCount() - 1){
            ultimoItemRecycleView.ultimoItemRecycleView(true)
        }

    }

    // tamanho da lista
    override fun getItemCount(): Int {
        return categorias.size
    }

    interface ClickCategoria {
        fun clickCategoria(categoria: Categoria)
        fun onClose(): Boolean
    }

    interface UltimoItemRecycleView{
        fun ultimoItemRecycleView(isExibido: Boolean)
    }

}