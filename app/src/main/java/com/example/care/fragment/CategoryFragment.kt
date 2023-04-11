package com.example.care.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.care.R
import com.example.care.activity.MainActivity
import com.example.care.adapter.CategoryAdapter
import com.example.care.databinding.FragmentArticleBinding
import com.example.care.databinding.FragmentCategoryBinding
import com.example.care.model.Category
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class CategoryFragment : Fragment() {
    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!
    lateinit var db: FirebaseFirestore
    lateinit var data:ArrayList<Category>
    private var progressDialog: ProgressDialog? = null
    lateinit var d :Activity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = Firebase.firestore
        d=(activity as MainActivity)
        data=ArrayList()
        showDialog()
        getAllCategory()
        binding.btnAdd.setOnClickListener {
            (d as MainActivity).makeCurrentFragment(AddCategoryFragment())
        }
    }

    fun getAllCategory(){
        db.collection("Category")
            .get()
            .addOnSuccessListener {
                for (document in it) {
                    val id =document.id
                    val name = document.getString("name")
                    val description = document.getString("description")
                    val img = document.getString("img")
                    val imgName = document.getString("imgName")
                    val category=Category(id,name!!,img!!,imgName!!,description!!)
                    data.add(category)
                }
                var categoryAdapter = CategoryAdapter(d, data)
                binding.rv.layoutManager = LinearLayoutManager(d)
                binding.rv.adapter = categoryAdapter
                hideDialog()
            }
            .addOnFailureListener {
                Toast.makeText(d,it.message,Toast.LENGTH_SHORT).show()
                hideDialog()
            }
    }

    private fun showDialog() {
        progressDialog = ProgressDialog(MainActivity@ d)
        progressDialog!!.setMessage("جار التحميل ...")
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    private fun hideDialog() {
        if (progressDialog!!.isShowing)
            progressDialog!!.dismiss()
    }
}


