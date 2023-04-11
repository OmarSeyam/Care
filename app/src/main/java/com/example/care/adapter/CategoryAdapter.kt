package com.example.care.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.care.activity.ArticlesActivity
import com.example.care.activity.MainActivity
import com.example.care.databinding.LayoutViewBinding
import com.example.care.fragment.EditArticleFragment
import com.example.care.fragment.EditCategoryFragment
import com.example.care.model.Article
import com.example.care.model.Category
import com.squareup.picasso.Picasso
import java.io.Serializable

class CategoryAdapter(var activity: Activity, var data: ArrayList<Category>):
    RecyclerView.Adapter<CategoryAdapter.MyViewHolder>(), Serializable {
    class MyViewHolder(var binding: LayoutViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            LayoutViewBinding.inflate(activity.layoutInflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Picasso.get().load(data[position].img).into(holder.binding.imgCategory)
        holder.binding.tvName.setText(data[position].name)
        holder.binding.tvDescription.setText(data[position].description)
        holder.binding.btnEdit.setOnClickListener {
            val sharedP=activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
            val edit=sharedP!!.edit()
            edit.putString("idCategory",data[position].id)
            edit.apply()
            (activity as MainActivity).makeCurrentFragment(EditCategoryFragment())
        }
        holder.binding.cardView.setOnClickListener {
            val i=Intent(activity,ArticlesActivity::class.java)
            activity.startActivity(i)
        }

    }

    override fun getItemCount(): Int {
        return  data.size
    }


}