package com.example.newsapp

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.newsapp.databinding.ArticleListItemBinding

class NewsAdapter(val a: Activity, val article: ArrayList<Article>) :
    Adapter<NewsAdapter.NewsVH>() {

    class NewsVH(val binding: ArticleListItemBinding) :ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsVH {
        val b = ArticleListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsVH(b)
    }


    //Single expression function
    override fun getItemCount() = article.size


    override fun onBindViewHolder(holder: NewsVH, position: Int) {
        holder.binding.articleText.text = article[position].title

        Glide
            .with(holder.binding.articleImage.context)
            .load(article[position].urlToImage)
            .error(R.drawable.error_image)
            .transition(DrawableTransitionOptions.withCrossFade(1000))
            .into(holder.binding.articleImage)
        val url = article[position].url
        holder.binding.articleContainer.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW, url.toUri())
            a.startActivity(i)
        }

        holder.binding.shareFab.setOnClickListener {
            ShareCompat
                .IntentBuilder(a)
                .setType("text/plain")
                .setChooserTitle("Share article with:")
                .setText(url)
                .startChooser()
        }
    }
}