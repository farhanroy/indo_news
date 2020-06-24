package com.indo.news.modules.entertainment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.indo.news.R
import com.indo.news.data.model.News
import com.indo.news.services.db.entity.Article
import com.indo.news.utils.extension.TimeAgo

class EntertainmentAdapter(
    private val context: Context,
    private val listNews: News,
    private val clickListener: (Article) -> Unit
) : RecyclerView.Adapter<EntertainmentAdapter.EntertainmentVH>() {

    private lateinit var layoutInflater: LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntertainmentVH {
        layoutInflater = LayoutInflater.from(context)
        return if (viewType == NEWS_ITEM1) {
            val view: View = layoutInflater.inflate(R.layout.item_news1, parent, false)
            EntertainmentVH(view)
        } else {
            val view: View = layoutInflater.inflate(R.layout.item_news2, parent, false)
            EntertainmentVH(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 || position % 3 == 0) NEWS_ITEM1
        else NEWS_ITEM2
    }

    override fun getItemCount(): Int = listNews.articles.size

    override fun onBindViewHolder(holder: EntertainmentVH, position: Int) {
        val item = listNews.articles[position]
        holder.bind(item)
        holder.itemView.setOnClickListener { clickListener(item) }
    }

    inner class EntertainmentVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle = itemView.findViewById<TextView>(R.id.tv_title)
        private val tvSource = itemView.findViewById<TextView>(R.id.tv_source)
        private val tvTime = itemView.findViewById<TextView>(R.id.tv_time)
        private val ivNews = itemView.findViewById<AppCompatImageView>(R.id.iv_news)

        fun bind(article: Article) {
            tvTitle.text = article.title
            tvSource.text = article.source.name
            tvTime.text = TimeAgo.getTimeAgo(article.publishedAt)

            Glide.with(context).load(article.urlToImage).into(ivNews)
        }
    }

    companion object {
        const val NEWS_ITEM1 = 0
        const val NEWS_ITEM2 = 1
    }
}