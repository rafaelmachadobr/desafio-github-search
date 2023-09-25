package br.com.igorbag.githubsearch.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.data.GitHubService
import br.com.igorbag.githubsearch.domain.Repository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    lateinit var nomeUsuario: EditText
    lateinit var btnConfirmar: Button
    lateinit var listaRepositories: RecyclerView
    lateinit var githubApi: GitHubService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
        setupListeners()
        showUserName()
        setupRetrofit()
    }

    // Metodo responsavel por realizar o setup da view e recuperar os Ids do layout
    fun setupView() {
        //@TODO 1 - Recuperar os Id's da tela para a Activity com o findViewById
        nomeUsuario = findViewById(R.id.et_nome_usuario)
        btnConfirmar = findViewById(R.id.btn_confirmar)
        listaRepositories = findViewById(R.id.rv_lista_repositories)
    }

    //metodo responsavel por configurar os listeners click da tela
    private fun setupListeners() {
        //@TODO 2 - colocar a acao de click do botao confirmar
        btnConfirmar.setOnClickListener {
            Toast.makeText(
                this,
                "Procurando repositórios de ${nomeUsuario.text}",
                Toast.LENGTH_LONG
            )
                .show()
            saveUserLocal(nomeUsuario.text.toString())
            getAllReposByUserName()
        }
    }


    // salvar o usuario preenchido no EditText utilizando uma SharedPreferences
    private fun saveUserLocal(userName: String) {
        //@TODO 3 - Persistir o usuario preenchido na editText com a SharedPref no listener do botao salvar
        val sharedPref = this.getPreferences(MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(getString(R.string.saved_user_name), userName)
            apply()
        }
    }

    private fun showUserName() {
        //@TODO 4- depois de persistir o usuario exibir sempre as informacoes no EditText  se a sharedpref possuir algum valor, exibir no proprio editText o valor salvo
        val sharedPref = this.getPreferences(MODE_PRIVATE)
        val userName = sharedPref.getString(getString(R.string.saved_user_name), "")

        if (!userName.isNullOrEmpty() || userName != "") {
            nomeUsuario.setText(userName)
        }
    }

    //Metodo responsavel por fazer a configuracao base do Retrofit
    fun setupRetrofit() {
        /*
           @TODO 5 -  realizar a Configuracao base do retrofit
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
        // TODO 6 - realizar a implementacao do callback do retrofit e chamar o metodo setupAdapter se retornar os dados com sucesso
        try {
            val call = githubApi.getAllRepositoriesByUser(nomeUsuario.text.toString())
            call.enqueue(object : retrofit2.Callback<List<Repository>> {
                override fun onResponse(
                    call: retrofit2.Call<List<Repository>>,
                    response: retrofit2.Response<List<Repository>>
                ) {
                    if (response.isSuccessful) {
                        val repositories = response.body()
                        setupAdapter(repositories!!)
                    }
                }

                override fun onFailure(call: retrofit2.Call<List<Repository>>, t: Throwable) {
                    Toast.makeText(
                        this@MainActivity,
                        "Erro ao buscar os repositorios",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        } catch (e: Exception) {
            Toast.makeText(
                this@MainActivity,
                "Erro ao buscar os repositorios",
                Toast.LENGTH_LONG
            ).show()
        } finally {
            Toast.makeText(
                this@MainActivity,
                "Busca finalizada",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Metodo responsavel por realizar a configuracao do adapter
    fun setupAdapter(list: List<Repository>) {
        /*
            @TODO 7 - Implementar a configuracao do Adapter , construir o adapter e instancia-lo
            passando a listagem dos repositorios
         */
    }


    // Metodo responsavel por compartilhar o link do repositorio selecionado
    // @Todo 11 - Colocar esse metodo no click do share item do adapter
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

    // @Todo 12 - Colocar esse metodo no click item do adapter
    fun openBrowser(urlRepository: String) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(urlRepository)
            )
        )

    }

}