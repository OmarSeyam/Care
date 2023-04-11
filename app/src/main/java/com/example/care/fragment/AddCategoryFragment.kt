package com.example.care.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.care.R
import com.example.care.activity.MainActivity
import com.example.care.databinding.FragmentAddArticleBinding
import com.example.care.databinding.FragmentAddCategoryBinding
import com.example.care.model.Category
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream


class AddCategoryFragment : Fragment() {
    private var _binding: FragmentAddCategoryBinding? = null
    private val binding get() = _binding!!
    lateinit var db: FirebaseFirestore
    private var progressDialog: ProgressDialog? = null
    private var fileURI: Uri? = null
    private val PICK_IMAGE_REQUEST = 111
    var imageURI: Uri? = null
    lateinit var d: Activity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = Firebase.firestore
        val storage = Firebase.storage
        val storageRef = storage.reference
        val imageRef = storageRef.child("images")
        d = (activity as MainActivity)
        binding.btnAddImg.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_PICK
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        binding.btnAdd.setOnClickListener {
            showDialog()
            val name = binding.txtName.text.toString()
            val description = binding.txtDescription.text.toString()

            if (name.isNotEmpty() && description.isNotEmpty() && imageURI != null) {
                val bitmap = (binding.imageView.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                val data = baos.toByteArray()
                val imgName = System.currentTimeMillis().toString() + "_omrimages.png"
                val childRef =
                    imageRef.child(imgName)
                var uploadTask = childRef.putBytes(data)
                uploadTask
                    .addOnFailureListener { exception ->
                        Toast.makeText(
                            d,
                            "فشل التحميل${exception.message})",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        hideDialog()
                    }
                    .addOnSuccessListener {
                        childRef.downloadUrl.addOnSuccessListener { uri ->
                            fileURI = uri
                            if (fileURI != null) {
                                val category = Category("", name, fileURI.toString(),imgName, description)
                                db.collection("Category")
                                    .add(category)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            d,
                                            "تم التحميل!",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        binding.txtName.text.clear()
                                        binding.txtDescription.text.clear()
                                        hideDialog()
                                        (d as MainActivity).makeCurrentFragment(CategoryFragment())
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            d,
                                            "فشل تحميل${it.message}",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        hideDialog()
                                    }
                            } else {
                                Toast.makeText(d, "حاول مرة اخرى!", Toast.LENGTH_SHORT).show()
                                hideDialog()
                            }
                        }
                    }
            } else {
                Toast.makeText(d, "أكمل البيانات!", Toast.LENGTH_SHORT).show()
                hideDialog()
            }

        }
    }

    private fun showDialog() {
        progressDialog = ProgressDialog(MainActivity@ d)
        progressDialog!!.setMessage("تحميل التصنيف ...")
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    private fun hideDialog() {
        if (progressDialog!!.isShowing)
            progressDialog!!.dismiss()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            imageURI = data!!.data
            binding.imageView.setImageURI(imageURI)
        }
    }
}