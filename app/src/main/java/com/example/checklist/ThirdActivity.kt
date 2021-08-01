package com.example.checklist

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import kotlinx.android.synthetic.main.activity_third.*
import org.jetbrains.anko.toast
import java.io.File
import java.util.*
import android.os.Environment as Environment

class ThirdActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        textViewDate.setOnClickListener{
            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val day = cal.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                val realmMonth = mMonth+1
                textViewDate.text = "$mYear. $realmMonth. $mDay"

                val filename = "$mYear$realmMonth$mDay.csv"
                val dayFile = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), filename)
                var registeredNames = mutableListOf<String>()

                if (dayFile.exists()){
                    csvReader().open(dayFile){
                        readAllAsSequence().forEach { row ->
                            registeredNames.add(row.joinToString(", "))
                        }
                    }
                    val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, registeredNames)
                    registeredNameListView.adapter = adapter
                } else {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                                    requestPermissions(arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE), 1)
                                } else {
                                    dayFile.createNewFile()
                                }
                    } else {
                        dayFile.parentFile.mkdirs()
                        dayFile.createNewFile()
                    }

                }

            }, year, month, day)

            dpd.show()
        }

        buttonRegisterName.setOnClickListener {
            //write input data in csv file
            //Update ListView

            val dayInfo = textViewDate.text.split(". ")
            val mYear = dayInfo[0]
            val realmMonth = dayInfo[1]
            val mDay = dayInfo[2]

            val filename = "$mYear$realmMonth$mDay.csv"
            val dayFile = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), filename)
            var registeredNames = mutableListOf<String>()

            val inputRow = listOf(editTextPersonName.text, editTextNumber.text)

            csvWriter().writeAll(listOf(inputRow), dayFile, append = true)

            csvReader().open(dayFile){
                readAllAsSequence().forEach { row ->
                    registeredNames.add(row.joinToString(", "))
                }
            }
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, registeredNames)
            registeredNameListView.adapter = adapter

            //clear input information
            editTextPersonName.text.clear()
            editTextNumber.text.clear()
        }

        buttonReset.setOnClickListener {
            //TODO
            //reset the csv file
            //then update listview
            toast("Error: 준비중인 기능입니다.")
        }

        registeredNameListView.onItemClickListener =
                AdapterView.OnItemClickListener { p0, p1, p2, id ->
                    //TODO
                    //erase touched item
                    //then update listview
                    toast("Error: 준비중인 기능입니다.")
                }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }
}