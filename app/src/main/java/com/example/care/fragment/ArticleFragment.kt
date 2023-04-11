package com.example.care.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.care.R
import com.example.care.activity.ArticlesActivity
import com.example.care.activity.MainActivity
import com.example.care.adapter.ArticleAdapter
import com.example.care.adapter.CategoryAdapter
import com.example.care.databinding.FragmentAddCategoryBinding
import com.example.care.databinding.FragmentArticleBinding
import com.example.care.model.Article
import com.example.care.model.Category
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ArticleFragment : Fragment() {
    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!
    lateinit var data:ArrayList<Article>
    lateinit var db: FirebaseFirestore
    lateinit var d: Activity
    private var progressDialog: ProgressDialog? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = Firebase.firestore
        d=(activity as ArticlesActivity)
        data= ArrayList()
        showDialog()
        getAllArticles()
        binding.btnAdd.setOnClickListener {
            (d as ArticlesActivity).makeCurrentFragment(AddArticleFragment())
        }
    }
    fun getAllArticles(){
        db.collection("Article")
            .get()
            .addOnSuccessListener {
                for (document in it) {
                    val id =document.id
                    val name = document.getString("name")
                    val description = document.getString("description")
                    val img = document.getString("img")
                    val imgName = document.getString("imgName")
                    val audio = document.getString("audio")
                    val audioName = document.getString("audioName")
                    val video = document.getString("video")
                    val videoName = document.getString("videoName")
                    val article=Article(id,name!!,description!!,img!!,video!!,audio!!,imgName!!,videoName!!,audioName!!)
                    data.add(article)
                }
                var articleAdapter = ArticleAdapter(d, data)
                binding.rv.layoutManager = LinearLayoutManager(d)
                binding.rv.adapter = articleAdapter
                hideDialog()
            }
            .addOnFailureListener {
                Toast.makeText(d,it.message, Toast.LENGTH_SHORT).show()
                hideDialog()
            }
    }
    private fun showDialog() {
        progressDialog = ProgressDialog(ArticlesActivity@ d)
        progressDialog!!.setMessage("جار التحميل ...")
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    private fun hideDialog() {
        if (progressDialog!!.isShowing)
            progressDialog!!.dismiss()
    }
}