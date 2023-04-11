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
import com.example.care.activity.ArticlesActivity
import com.example.care.activity.MainActivity
import com.example.care.databinding.FragmentAddArticleBinding
import com.example.care.model.Article
import com.example.care.model.Category
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class AddArticleFragment : Fragment() {
    private var _binding: FragmentAddArticleBinding? = null
    private val binding get() = _binding!!
    lateinit var db: FirebaseFirestore
    private var progressDialog: ProgressDialog? = null
    private var fileImgURI: Uri? = null
    private var fileVideoURI: Uri? = null
    private var fileAudioURI: Uri? = null
    private val PICK_IMAGE_REQUEST = 111
    private val PICK_Video_REQUEST = 222
    private val PICK_Audio_REQUEST = 333
    var imageURI: Uri? = null
    var videoURI: Uri? = null
    var audioURI: Uri? = null
    lateinit var imageRef: StorageReference
    lateinit var videoRef: StorageReference
    lateinit var audioRef: StorageReference
    lateinit var storageRef: StorageReference
    lateinit var imgName: String
    lateinit var videoName: String
    lateinit var audioName: String
    lateinit var d: Activity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddArticleBinding.inflate(inflater, container, false)
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

        binding.btnAddImg.setOnClickListener {
            selsectImg()
        }
        binding.btnAddVideo.setOnClickListener {
            selsectVideo()
        }
        binding.btnAddAudio.setOnClickListener {
            selsectAudio()
        }

        binding.btnAdd.setOnClickListener {
            uploadImg()
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

    fun uploadArticle(
        img: String,
        imgName: String,
        video: String,
        videoName: String,
        audio: String,
        audioName: String
    ) {
        showDialog()
        val name = binding.txtName.text.toString()
        val description = binding.txtDescription.text.toString()

        if (name.isNotEmpty() && description.isNotEmpty() && imageURI != null && videoURI != null && audioURI != null) {
            val article = Article(
                "",
                name,
                description,
                img,
                video,
                audio,
                imgName,
                videoName,
                audioName
            )
            db.collection("Article")
                .add(article)
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
                    (d as ArticlesActivity).makeCurrentFragment(
                        ArticleFragment()
                    )
                }
                .addOnFailureListener {
                    Toast.makeText(
                        d,
                        " فشل التحميل${it.message}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    hideDialog()
                }
        } else {
            Toast.makeText(
                d,
                "أكمل البيانات!",
                Toast.LENGTH_SHORT
            ).show()
            hideDialog()
        }
    }

    fun uploadImg() {
        showDialog()
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
                    if (fileImgURI != null) {
                        uploadVideo()
                    }
                }
            }
    }

    fun uploadVideo() {
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
                    if (fileVideoURI != null) {
                        uploadAudio()
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
                        uploadArticle(
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
        progressDialog!!.setMessage("تحميل المقال ...")
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