package com.example.care.model

import com.google.firebase.firestore.DocumentId

data class Article(@DocumentId var id:String,var name:String,var description:String,var img:String,var video:String,var audio:String,var imgName:String,var videoName:String,var audioName:String)