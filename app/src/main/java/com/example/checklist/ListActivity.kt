package com.example.checklist

import android.os.Bundle
import android.os.Parcelable
import android.os.PersistableBundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_list.*
import org.jetbrains.anko.startActivity
import java.util.ArrayList

class ListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val searchedName: List<String> = intent.getStringArrayListExtra("searchedName")
        var searchedListMenu = mutableListOf<String>()

        searchedName.forEach { person ->
            val row: List<String> = person.split(", ")
            val strippedDate = row[0].split(" ").slice(0..2).joinToString(" ")
            val rowForList = listOf<String>(row[2], row[3], strippedDate)
            searchedListMenu.add(rowForList.joinToString(", "))
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, searchedListMenu)
        searchedListView.adapter = adapter

        searchedListView.onItemClickListener =
            AdapterView.OnItemClickListener { p0, p1, p2, id ->
                var answeredYes = mutableListOf<String>()
                val row: List<String> = searchedName[p2].split(", ")
                for (i in row.indices){
                    if (row[i] == "예" || row[i] == "확진자" || row[i] == "확진자의 접촉자" || row[i] == "PCR검사자") {
                        answeredYes.add(i.toString())
                    }
                }
                startActivity<ResultActivity>(
                    "infoName" to row[2],
                    "info4Number" to row[3],
                    "yesItems" to answeredYes
                )
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }

}