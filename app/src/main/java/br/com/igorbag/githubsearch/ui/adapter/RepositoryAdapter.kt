package br.com.igorbag.githubsearch.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.domain.Repository

class RepositoryAdapter(private val repositories: List<Repository>) :
    RecyclerView.Adapter<RepositoryAdapter.ViewHolder>() {

    var itemListener: (Repository) -> Unit = {}
    var btnShareListener: (Repository) -> Unit = {}

    // Cria uma nova view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.repository_item, parent, false)
        return ViewHolder(view)
    }

    // Pega o conteudo da view e troca pela informacao de item de uma lista
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //@ 8 -  Realizar o bind do viewHolder
        holder.repositoryName.text = repositories[position].name

        holder.repositoryName.setOnClickListener {
            itemListener(repositories[position])
        }

        holder.shareButton.setOnClickListener {
            btnShareListener(repositories[position])
        }
    }

    // Pega a quantidade de repositorios da lista
    //@ 9 - realizar a contagem da lista
    override fun getItemCount(): Int = repositories.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //@ 10 - Implementar o ViewHolder para os repositorios
        val repositoryName: TextView
        val shareButton: ImageView

        init {
            view.apply {
                repositoryName = findViewById(R.id.tv_repository_name)
                shareButton = findViewById(R.id.iv_share)
            }
        }

    }
}


