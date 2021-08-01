package com.example.checklist

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_result.*
import org.jetbrains.anko.textColor
import java.lang.StringBuilder


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val infoName = intent.getStringExtra("infoName")
        val info4Number = intent.getStringExtra("info4Number")
        val answeredYes = intent.getStringArrayListExtra("yesItems")

        textViewName.text = infoName
        textViewNumber.text = info4Number
        if (answeredYes.isEmpty()) {
            textViewYes.text = "해당 없음"
            textViewExp.text = "훈련이 가능합니다"
            viewForColor.setBackgroundColor(Color.parseColor("#72CC82"))
        } else {
            textViewExp.text = "훈련이 불가능합니다"

            var i = 1
            var stringContent = ""

            answeredYes.forEach { item ->
                val detail = stringFormat(item)
                stringContent = "$stringContent$i. $detail\n"
                i++
            }
            textViewYes.text = stringContent

            viewForColor.setBackgroundColor(Color.parseColor("#B3413C"))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }


    private fun stringFormat(item: String): String {
        return when (item) {
            "4" -> {
                "발열"
            }
            "5" -> {
                "코로나 증상 발현"
            }
            "6" -> {
                "확진자 또는 확진자의 접촉자 접촉"
            }
            "7" -> {
                "고위험시설 방문"
            }
            "8" -> {
                "위험인원 접촉"
            }
            "9" -> {
                "확진자 동선 중복"
            }
            else -> {
                "None"
            }
        }
    }
}