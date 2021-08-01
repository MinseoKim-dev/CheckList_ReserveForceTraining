package com.example.checklist

import android.app.DatePickerDialog
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.widget.ArrayAdapter
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import kotlinx.android.synthetic.main.activity_check.*
import org.jetbrains.anko.toast
import java.io.File
import java.io.FileInputStream
import java.util.*

class CheckActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check)

        lateinit var downloadedFile: ParcelFileDescriptor
        //fetch id from MainActivity
        var id: Long = 0
        var fileDownloaded = false
        var fileLoaded = false

        downloadFile4.setOnClickListener {
            if (editLink4.text.toString().contains("https")) {
                Thread {
                    id = downloadFile(editLink4.text.toString())
                    fileDownloaded = true
                    fileLoaded = true
                }.start()
                statusTextView4.text = "파일 로드 완료"
            } else {
                toast("Error: 유효한 주소를 입력해 주세요.")
            }
        }

        textViewDate2.setOnClickListener{
            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val day = cal.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                val realmMonth = mMonth+1
                textViewDate2.text = "$mYear. $realmMonth. $mDay"
            }, year, month, day)

            dpd.show()
        }

        buttonCheckWhether.setOnClickListener {
            //불러온 List에 있는 사람들 파일에다 이름 검색해서 TextViewResult에 몇명 문진 했는지 표시하기
            var peopleToCome = mutableListOf<String>()
            var comeIndex = false
            val dayInfo = textViewDate2.text.split(". ")
            val mYear = dayInfo[0]
            val realmMonth = dayInfo[1]
            val mDay = dayInfo[2]

            val filename = "$mYear$realmMonth$mDay.csv"
            val dayFile = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), filename)
            val nameFileList = mutableListOf<List<String>>()

            val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadedFile = manager.openDownloadedFile(id)
            val csv = FileInputStream(downloadedFile.fileDescriptor)

            csvReader().open(dayFile){
                readAllAsSequence().forEach { name ->
                    nameFileList.add(name)
                }
            }

            val rows = csvReader().readAll(csv)
            nameFileList.forEach { name ->
                rows.forEach { row ->
                    if (row.contains(name[0]) && row.contains(name[1])) {
                        comeIndex = true
                    }
                }
                if(comeIndex){
                    peopleToCome.add(name.joinToString(", "))
                    comeIndex = false
                }
            }

            val number = peopleToCome.size
            textViewResult.text = "문진 작성한 수: $number"
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, peopleToCome)
            participantList.adapter = adapter
        }

    }

    private fun downloadFile(uri: String): Long {
        val request = DownloadManager.Request(Uri.parse(uri))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        request.setTitle("Download")
        request.setDescription("The file is downloading...")

        val timestamp = System.currentTimeMillis()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$timestamp")

        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        return manager.enqueue(request)
        //return timestamp.toString()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }
}