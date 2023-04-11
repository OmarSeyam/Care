package com.example.care.adapter

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.care.R
import com.example.care.activity.ArticlesActivity
import com.example.care.databinding.LayoutViewArticleBinding
import com.example.care.fragment.EditArticleFragment
import com.example.care.model.Article
import com.squareup.picasso.Picasso

class ArticleAdapter(var activity: Activity, var data: ArrayList<Article>) :
    RecyclerView.Adapter<ArticleAdapter.MyViewHolder>() {
    class MyViewHolder(var binding: LayoutViewArticleBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            LayoutViewArticleBinding.inflate(activity.layoutInflater, parent, false)
        return MyViewHolder(binding)
    }


    lateinit var playIcon: ImageView
    lateinit var mMediaPlayer: MediaPlayer
    lateinit var voice: String
    override fun onBindViewHolder(holder: MyViewHolder, p: Int) {
        voice = data[p].audio
        playIcon = holder.binding.imgPlay
        holder.binding.tv.setText(data[p].description)
        holder.binding.tv2.setText(data[p].name)
        Picasso.get().load(data[p].img).into(holder.binding.image)
        voices(holder, p)
        video(holder,p)
        holder.binding.tvEdit.setOnClickListener {
            val sharedP = activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
            val edit = sharedP!!.edit()
            edit.putString("idArticle", data[p].id)
            edit.apply()
            (activity as ArticlesActivity).makeCurrentFragment(EditArticleFragment())
        }
    }


    fun video(holder: MyViewHolder, p: Int){
        holder.binding.videoView.setVideoURI(data[p].video.toUri())
        holder.binding.imgPlayVideo.setOnClickListener {
            if (holder.binding.videoView.isPlaying) {
                holder.binding.videoView.pause()
                holder.binding.imgPlayVideo.setImageResource(R.drawable.ic_play_arrow_black_24dp)
            } else {
                holder.binding.videoView.start()
                holder.binding.imgPlayVideo.setImageResource(R.drawable.ic_pause_black_24dp)
            }
        }
    }


    fun voices(holder: MyViewHolder, p: Int) {
        mMediaPlayer=MediaPlayer()
        holder.binding.imgPlay.setOnClickListener {
            if (!mMediaPlayer.isPlaying) {
                mMediaPlayer = MediaPlayer.create(activity, data[p].audio.toUri())
            }
            if (mMediaPlayer != null && !mMediaPlayer.isPlaying) {
                mMediaPlayer.start()
                holder.binding.imgPlay.setImageResource(R.drawable.ic_pause_black_24dp)
            } else {
                mMediaPlayer.stop()
                holder.binding.imgPlay.setImageResource(R.drawable.ic_play_arrow_black_24dp)

            }
        }

    }


    override fun getItemCount(): Int {
        return data.size
    }

}