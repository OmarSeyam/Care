package com.example.care.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.care.R
import com.example.care.fragment.ArticleFragment
import com.example.care.fragment.CategoryFragment

class ArticlesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_articles)
        makeCurrentFragment(ArticleFragment())
    }

    fun makeCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container2, fragment)
            commit()
        }
    }
}