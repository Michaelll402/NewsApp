package com.example.newsapp


import retrofit2.Call
import retrofit2.http.GET

interface NewsCallable {

    @GET("/v2/top-headlines?country=us&category=general&apiKey=42cf9e612f5c4319874eee4604e18b32&pageSize=100")
    fun getNews(): Call<News>

}