package br.com.igorbag.githubsearch.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.data.GitHubService
import br.com.igorbag.githubsearch.domain.Repository
import br.com.igorbag.githubsearch.data.local.SharedPreferencesKey
import br.com.igorbag.githubsearch.data.local.SharedPreferencesManager
import br.com.igorbag.githubsearch.ui.adapter.RepositoryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    lateinit var nomeUsuario: EditText
    lateinit var btnConfirmar: Button
    lateinit var listaRepositories: RecyclerView
    lateinit var githubApi: GitHubService
    lateinit var loader: ProgressBar

    private val sharedManager = SharedPreferencesManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
        showUserName()
        setupRetrofit()
        getAllReposByUserName()
    }

    // Metodo responsavel por realizar o setup da view e recuperar os Ids do layout
    fun setupView() {
        //@ 1 - Recuperar os Id's da tela para a Activity com o findViewById
        nomeUsuario = findViewById(R.id.et_nome_usuario)
        btnConfirmar = findViewById(R.id.btn_confirmar)
        listaRepositories = findViewById(R.id.rv_lista_repositories)
        loader = findViewById(R.id.pb_loader)

        setupListeners()
    }

    //metodo responsavel por configurar os listeners click da tela
    private fun setupListeners() {
        //@ 2 - colocar a acao de click do botao confirmar
        btnConfirmar.setOnClickListener {
            nomeUsuario.onEditorAction(EditorInfo.IME_ACTION_DONE)
            saveUserLocal()
            getAllReposByUserName()
        }
    }

    // salvar o usuario preenchido no EditText utilizando uma SharedPreferences
    private fun saveUserLocal() {
        //@ 3 - Persistir o usuario preenchido na editText com a SharedPref no listener do botao salvar
        val userNameValue = nomeUsuario.text.toString()
        sharedManager.save(SharedPreferencesKey.USER_NAME, userNameValue)
    }

    private fun showUserName() {
        //@ 4- depois de persistir o usuario exibir sempre as informacoes no EditText  se a sharedpref possuir algum valor, exibir no proprio editText o valor salvo
        val sharedUserName = sharedManager.get(SharedPreferencesKey.USER_NAME) ?: return
        nomeUsuario.setText(sharedUserName)
    }

    //Metodo responsavel por fazer a configuracao base do Retrofit
    fun setupRetrofit() {
        /*
           @ 5 -  realizar a Configuracao base do retrofit
           Documentacao oficial do retrofit - https://square.github.io/retrofit/
           URL_BASE da API do  GitHub= https://api.github.com/
           lembre-se de utilizar o GsonConverterFactory mostrado no curso
        */
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        githubApi = retrofit.create(GitHubService::class.java)
    }

    //Metodo responsavel por buscar todos os repositorios do usuario fornecido
    fun getAllReposByUserName() {
        //@ 6 - realizar a implementacao do callback do retrofit e chamar o metodo setupAdapter se retornar os dados com sucesso
        listaRepositories.isVisible = false
        loader.isVisible = true
        val userName = nomeUsuario.text.toString()
        githubApi.getAllRepositoriesByUser(userName).enqueue(object : Callback<List<Repository>> {

            override fun onResponse(
                call: Call<List<Repository>>,
                response: Response<List<Repository>>
            ) {
                loader.isVisible = false

                if (!response.isSuccessful) {
                    fail()
                    return
                }

                response.body()?.let {
                    setupAdapter(it)
                }
            }

            override fun onFailure(call: Call<List<Repository>>, t: Throwable) {
                loader.isVisible = false
                fail()
            }

            fun fail() {
                Toast.makeText(applicationContext, R.string.response_fail, Toast.LENGTH_LONG)
                    .show()
            }

        })
    }

    // Metodo responsavel por realizar a configuracao do adapter
    fun setupAdapter(list: List<Repository>) {
        /*
            @ 7 - Implementar a configuracao do Adapter , construir o adapter e instancia-lo
            passando a listagem dos repositorios
         */
        val repositoryAdapter = RepositoryAdapter(list)
        listaRepositories.apply {
            isVisible = true
            adapter = repositoryAdapter
        }

        repositoryAdapter.btnShareListener = {
            shareRepositoryLink(it.htmlUrl)
        }
        repositoryAdapter.itemListener = {
            openBrowser(it.htmlUrl)
        }
    }

    // Metodo responsavel por compartilhar o link do repositorio selecionado
    // @ 11 - Colocar esse metodo no click do share item do adapter
    fun shareRepositoryLink(urlRepository: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, urlRepository)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    // Metodo responsavel por abrir o browser com o link informado do repositorio
    // @ 12 - Colocar esse metodo no click item do adapter
    fun openBrowser(urlRepository: String) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(urlRepository)
            )
        )
    }

}