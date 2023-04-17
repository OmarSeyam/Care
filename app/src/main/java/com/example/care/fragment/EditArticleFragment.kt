package com.example.care.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
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
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.care.R
import com.example.care.activity.ArticlesActivity
import com.example.care.activity.MainActivity
import com.example.care.adapter.ArticleAdapter
import com.example.care.databinding.FragmentCategoryBinding
import com.example.care.databinding.FragmentEditArticleBinding
import com.example.care.model.Article
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class EditArticleFragment : Fragment() {
    private var _binding: FragmentEditArticleBinding? = null
    private val binding get() = _binding!!
    lateinit var db: FirebaseFirestore
    private var progressDialog: ProgressDialog? = null
    private var fileImgURI: Uri? = null
    private var fileVideoURI: Uri? = null
    private var fileAudioURI: Uri? = null
    private var fileImgURI1: String? = null
    private var fileVideoURI1: String? = null
    private var fileAudioURI1: String? = null
    private val PICK_IMAGE_REQUEST = 111
    private val PICK_Video_REQUEST = 222
    private val PICK_Audio_REQUEST = 333
    var imageURI: Uri? = null
    var videoURI: Uri? = null
    var audioURI: Uri? = null
    lateinit var imgName: String
    lateinit var videoName: String
    lateinit var audioName: String
    lateinit var imgName1: String
    lateinit var videoName1: String
    lateinit var audioName1: String
    lateinit var name: String
    lateinit var description: String
    lateinit var imageRef: StorageReference
    lateinit var videoRef: StorageReference
    lateinit var audioRef: StorageReference
    lateinit var storageRef: StorageReference
    lateinit var d: Activity
    lateinit var id: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = Firebase.firestore
        val storage = Firebase.storage
        storageRef = storage.reference
        imageRef = storageRef.child("images")
        videoRef = storageRef.child("videos")
        audioRef = storageRef.child("audios")
        d = (activity as ArticlesActivity)
        getArticle()
        editArticle()
        binding.btnDelete.setOnClickListener {
            val dialog = AlertDialog.Builder(d)
            dialog.setTitle("حذف")
            dialog.setMessage("do you want delete this Caegory!")
            dialog.setPositiveButton("yes") { _, _ ->
                db.collection("Article").document(id).delete()
                    .addOnSuccessListener {
                        imageRef.child(imgName1).delete()
                        videoRef.child(videoName1).delete()
                        audioRef.child(audioName1).delete()
                        Toast.makeText(d, "تم الحذف", Toast.LENGTH_SHORT).show()
                        (d as ArticlesActivity).makeCurrentFragment(ArticleFragment())
                    }
                    .addOnFailureListener {
                        Toast.makeText(activity, "فشل الحذف", Toast.LENGTH_SHORT).show()

                    }
            }
            dialog.setNegativeButton("No") { dis, _ ->
                dis.dismiss()
            }
            dialog.create().show()
        }
    }


    fun getArticle() {
        showDialog()
        val sharedP = d.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        id = sharedP.getString("idArticle", "0").toString()
        db.collection("Article").document(id)
            .get()
            .addOnSuccessListener { document ->
                audioName1 = document.getString("audioName").toString()
                videoName1 = document.getString("videoName").toString()
                imgName1 = document.getString("imgName").toString()
                fileImgURI1 = document.getString("img").toString()
                fileVideoURI1 = document.getString("video").toString()
                fileAudioURI1 = document.getString("audio").toString()
                binding.txtName.setText(document.getString("name"))
                binding.txtDescription.setText(document.getString("description"))
                binding.txtName.setText(document.getString("name"))
                if (document.getString("img") != null) {
                    binding.img.setImageResource(R.drawable.done)
                }
                if (document.getString("audio") != null) {
                    binding.imgAudio.setImageResource(R.drawable.done)
                }
                if (document.getString("video") != null) {
                    binding.imgVideo.setImageResource(R.drawable.done)
                }
                hideDialog()
            }
            .addOnFailureListener {
                Toast.makeText(d, it.message, Toast.LENGTH_SHORT).show()
                hideDialog()
            }
    }


    fun editArticle() {
        binding.btnAddAudio.setOnClickListener {
            selsectAudio()
        }
        binding.btnAddVideo.setOnClickListener {
            selsectVideo()
        }
        binding.btnAddImg.setOnClickListener {
            selsectImg()
        }

        binding.btnAdd.setOnClickListener {
            showDialog()
            name = binding.txtName.text.toString()
            description = binding.txtDescription.text.toString()
            if (imageURI == null) {
                fileImgURI = fileImgURI1!!.toUri()
                imgName = imgName1
            }
            if (videoURI == null) {
                fileVideoURI = fileVideoURI1!!.toUri()
                videoName = videoName1
            }
            if (audioURI == null) {
                fileAudioURI = fileAudioURI1!!.toUri()
                audioName = audioName1
            }


            if (name.isNotEmpty() && description.isNotEmpty() && imageURI != null && videoURI != null && audioURI != null) {
                uploadImg(true, true)
            } else if (name.isNotEmpty() && description.isNotEmpty() && imageURI == null && videoURI != null && audioURI != null) {
                uploadVideo(true)
            } else if (name.isNotEmpty() && description.isNotEmpty() && imageURI != null && videoURI == null && audioURI != null) {
                uploadImg(false, true)
            } else if (name.isNotEmpty() && description.isNotEmpty() && imageURI != null && videoURI != null && audioURI == null) {
                uploadImg(true, false)
            } else if (name.isNotEmpty() && description.isNotEmpty() && imageURI == null && videoURI == null && audioURI != null) {
                uploadAudio()
            } else if (name.isNotEmpty() && description.isNotEmpty() && imageURI == null && videoURI != null && audioURI == null) {
                uploadVideo(false)
            } else if (name.isNotEmpty() && description.isNotEmpty() && imageURI != null && videoURI == null && audioURI == null) {
                uploadImg(false, false)
            } else if (name.isNotEmpty() && description.isNotEmpty() && imageURI == null && videoURI == null && audioURI == null) {
                udpateArticle(
                    fileImgURI.toString(),
                    imgName,
                    fileVideoURI.toString(),
                    videoName,
                    fileAudioURI.toString(),
                    audioName
                )
            } else {
                Toast.makeText(
                    d,
                    "أكمل البيانات!",
                    Toast.LENGTH_SHORT
                ).show()
                hideDialog()
            }

        }
    }

    fun udpateArticle(
        img: String,
        imgName: String,
        video: String,
        videoName: String,
        audio: String,
        audioName: String
    ) {
        val sharedP = d.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        val idCategory = sharedP.getString("idCategory", "0").toString()
        val article = Article(
            "",
            name,
            description,
            img,
            video,
            audio,
            imgName,
            videoName,
            audioName,
            idCategory
        )
        db.collection("Article").document(id)
            .set(article)
            .addOnSuccessListener {
                Toast.makeText(
                    d,
                    "تم التعديل!",
                    Toast.LENGTH_SHORT
                )
                    .show()
                binding.txtName.text.clear()
                binding.txtDescription.text.clear()
                hideDialog()
                (d as ArticlesActivity).makeCurrentFragment(
                    ArticleFragment()
                )
            }
            .addOnFailureListener {
                Toast.makeText(
                    d,
                    " فشل تحديث المقال${it.message}",
                    Toast.LENGTH_SHORT
                )
                    .show()
                hideDialog()
            }
    }

    fun selsectImg() {
        val intent = Intent()
        intent.action = Intent.ACTION_PICK
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    fun selsectVideo() {
        val intent = Intent()
        intent.action = Intent.ACTION_PICK
        intent.type = "video/*"
        startActivityForResult(intent, PICK_Video_REQUEST)
    }

    fun selsectAudio() {
        val intent = Intent()
        intent.action = Intent.ACTION_PICK
        intent.type = "audio/*"
        startActivityForResult(intent, PICK_Audio_REQUEST)

    }

    fun uploadImg(isVideo: Boolean, isAudio: Boolean) {
        val bitmap = (binding.img1.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val data = baos.toByteArray()
        imgName = System.currentTimeMillis().toString() + "_omrimages.png"
        val childRef =
            imageRef.child(imgName)
        var uploadTask = childRef.putBytes(data)
        uploadTask
            .addOnFailureListener { exception ->
                Toast.makeText(
                    d,
                    "فشل تحميل الصورة${exception.message})",
                    Toast.LENGTH_SHORT
                )
                    .show()
                hideDialog()
            }
            .addOnSuccessListener {
                childRef.downloadUrl.addOnSuccessListener { uri0 ->
                    fileImgURI = uri0
                    if (fileImgURI != null)
                        imageRef.child(imgName1).delete()
                    if (fileImgURI != null && isVideo == true) {
                        uploadVideo(isAudio)
                    }
                    if (fileImgURI != null && isAudio == true && isVideo == false) {
                        uploadAudio()
                    }
                    if (fileImgURI != null && isAudio == false && isVideo == false)
                        udpateArticle(
                            fileImgURI.toString(),
                            imgName,
                            fileVideoURI.toString(),
                            videoName,
                            fileAudioURI.toString(),
                            audioName
                        )

                }
            }
    }

    fun uploadVideo(isAudio: Boolean) {
        videoName =
            System.currentTimeMillis().toString() + "_omrvideos.mp4"
        val childRef1 =
            videoRef.child(videoName)
        val uploadTask1 = childRef1.putFile(videoURI!!)
        uploadTask1
            .addOnFailureListener { exception ->
                Toast.makeText(
                    d,
                    " فشل تحميل الفيديو${exception.message})",
                    Toast.LENGTH_SHORT
                )
                    .show()
                hideDialog()
            }
            .addOnSuccessListener {
                childRef1.downloadUrl.addOnSuccessListener { uri ->
                    fileVideoURI = uri
                    if (fileVideoURI != null && isAudio == true) {
                        videoRef.child(videoName1).delete()
                        uploadAudio()
                    } else if (fileVideoURI != null) {
                        videoRef.child(videoName1).delete()
                        udpateArticle(
                            fileImgURI.toString(),
                            imgName,
                            fileVideoURI.toString(),
                            videoName,
                            fileAudioURI.toString(),
                            audioName
                        )
                    }
                }
            }
    }

    fun uploadAudio() {
        audioName =
            System.currentTimeMillis()
                .toString() + "_omraudios.mp3"
        val childRef2 =
            audioRef.child(audioName)
        val uploadTask2 = childRef2.putFile(audioURI!!)
        uploadTask2
            .addOnFailureListener { exception ->
                Toast.makeText(
                    d,
                    " فشل تحميل الصوت${exception.message})",
                    Toast.LENGTH_SHORT
                )
                    .show()
                hideDialog()
            }
            .addOnSuccessListener {
                childRef2.downloadUrl.addOnSuccessListener { uri1 ->
                    fileAudioURI = uri1
                    if (fileAudioURI != null) {
                        audioRef.child(audioName1).delete()
                        udpateArticle(
                            fileImgURI.toString(),
                            imgName,
                            fileVideoURI.toString(),
                            videoName,
                            fileAudioURI.toString(),
                            audioName
                        )
                    } else {
                        Toast.makeText(
                            d,
                            "حاول مرة اخرى!",
                            Toast.LENGTH_SHORT
                        ).show()
                        hideDialog()
                    }
                }
            }
    }

    private fun showDialog() {
        progressDialog = ProgressDialog(MainActivity@ d)
        progressDialog!!.setMessage("حفظ التغيرات  ...")
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
            if (imageURI != null) {
                binding.img.setImageResource(R.drawable.done)
                binding.img1.setImageURI(imageURI)
            }
        }
        if (requestCode == PICK_Video_REQUEST && resultCode == Activity.RESULT_OK) {
            videoURI = data!!.data
            if (videoURI != null) {
                binding.imgVideo.setImageResource(R.drawable.done)
            }
        }
        if (requestCode == PICK_Audio_REQUEST && resultCode == Activity.RESULT_OK) {
            audioURI = data!!.data
            if (audioURI != null) {
                binding.imgAudio.setImageResource(R.drawable.done)
            }
        }
    }
}