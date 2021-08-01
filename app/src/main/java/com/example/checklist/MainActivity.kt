package com.example.checklist

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button1.setOnClickListener {
            startActivity<FirstActivity>()
        }

        button2.setOnClickListener {
            startActivity<SecondActivity>()
        }

        button3.setOnClickListener {
            startActivity<ThirdActivity>()
        }

        button4.setOnClickListener {
            startActivity<CheckActivity>()
        }

    }

}