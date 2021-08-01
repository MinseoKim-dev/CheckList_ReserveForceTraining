package com.example.checklist

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import kotlinx.android.synthetic.main.activity_first.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.io.FileInputStream
import java.io.InputStream

class FirstActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)

        var fileDownloaded = false
        var fileLoaded = false
        var id: Long = 0
        lateinit var downloadedFile: ParcelFileDescriptor
        lateinit var csv: InputStream


        downloadFile1.setOnClickListener {
            if (editLink1.text.toString().contains("https")) {
                Thread {
                    id = downloadFile(editLink1.text.toString())
                    fileDownloaded = true
                    fileLoaded = true
                }.start()
                statusTextView1.text = "파일 로드 완료"
            } else {
                toast("Error: 유효한 주소를 입력해 주세요.")
            }
        }

        fabSearch.setOnClickListener {
            if (fileLoaded) {

                var useName = false
                var use4Number = false

                val inputName = editName1.text.toString()
                val input4Number = edit4Number1.text.toString()

                if (inputName != "") {
                    useName = true
                }
                if (input4Number != "") {
                    use4Number = true
                }

                val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                downloadedFile = manager.openDownloadedFile(id)
                csv = FileInputStream(downloadedFile.fileDescriptor)
                searchFromFile(inputName, input4Number, csv, useName, use4Number)

            } else {
                toast("Error: 파일을 먼저 불러와 주세요.")
            }
        }
    }

    private fun searchFromFile(inputName: String, input4Number: String, csv: InputStream, useName: Boolean, use4Number: Boolean) {
        var foundRow = mutableListOf<List<String>>()
        var foundRowsForList = mutableListOf<String>()
        var answeredYes = mutableListOf<String>()
        csvReader().open(csv) {
            readAllAsSequence().forEach { row ->
                if (!useName || row[2].contains(inputName)) {
                    if (!use4Number || input4Number == row[3]) {
                        foundRow.add(row)
                    }
                }
            }
        }
        if (foundRow.isEmpty()){
            toast("해당되는 예비군이 없습니다.")
        } else if (foundRow.size == 1) {
            for (i in foundRow[0].indices){
                if (foundRow[0][i] == "예" || foundRow[0][i] == "확진자" || foundRow[0][i] == "확진자의 접촉자" || foundRow[0][i] == "PCR검사자"){
                    answeredYes.add(i.toString())
                }
            }
            startActivity<ResultActivity>(
                "infoName" to foundRow[0][2],
                "info4Number" to foundRow[0][3],
                "yesItems" to answeredYes
            )
        } else {
            foundRow.forEach { row ->
                val stringedRow = row.joinToString(", ")
                foundRowsForList.add(stringedRow)
            }
            startActivity<ListActivity>(
                "searchedName" to foundRowsForList
            )
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