package com.example.newsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.example.newsapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    //https://newsapi.org/v2/top-headlines?country=us&category=general&apiKey=42cf9e612f5c4319874eee4604e18b32&pageSize=100

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadNews()
    }

    private fun loadNews(){
        val retrofit = Retrofit
        .Builder()
        .baseUrl("https://newsapi.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val call = retrofit.create(NewsCallable::class.java)
        call.getNews().enqueue(object : Callback<News>{
            override fun onResponse(call: Call<News>, response: Response<News>) {
                val news = response.body()
                val articles = news?.articles!!
                //Log.d("trace", "Articles: $articles")
                showNews(articles)
                binding.progress.isVisible = false
            }

            override fun onFailure(call: Call<News>, t: Throwable) {
                Log.d("trace", "Error: ${t.message}")
            }
        })
    }

    private fun showNews(article: ArrayList<Article>){
        val adapter = NewsAdapter(this, article)
        binding.newsList.adapter = adapter
    }
}