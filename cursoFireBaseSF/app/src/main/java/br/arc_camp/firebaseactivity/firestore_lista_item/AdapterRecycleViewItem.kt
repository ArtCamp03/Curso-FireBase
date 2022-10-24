package br.arc_camp.firebaseactivity.firestore_lista_item

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import br.arc_camp.firebaseactivity.R
import com.bumptech.glide.Glide

class AdapterRecycleViewItem(var context: Context, var itens: ArrayList<Item>, var clickItem: ClickItem):
    RecyclerView.Adapter<AdapterRecycleViewItem.ViewHolder>() {

    // responsavel pelo layout dos itens
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_lista_item_recycleview, parent, false)
        val holder = AdapterRecycleViewItem.ViewHolder(view)
        return holder
    }

    // Insere informaçoes do layout
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val item = itens.get(position)
        holder.nome.setText(item.nome)
        holder.descricao.setText(item.descricao)
        Glide.with(context).load(item.url_image).into(holder.imagem)

        holder.cardView.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                clickItem.clickItem(item)
            }
        })

    }

    // tamanho da lista
    override fun getItemCount(): Int {
        return itens.size
    }

    // liga variaveis com itens no layout
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val cardView = itemView.findViewById<CardView>(R.id.cardView_listaItem)
        val nome = itemView.findViewById<TextView>(R.id.texview_lista_item_nome)
        val descricao = itemView.findViewById<TextView>(R.id.texview_lista_item_descricao)
        val imagem = itemView.findViewById<ImageView>(R.id.imageview_lista_item_imagem)
    }

    interface ClickItem{
        fun clickItem(item: Item)
    }
}