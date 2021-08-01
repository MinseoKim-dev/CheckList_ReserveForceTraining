package com.example.checklist

import android.app.DatePickerDialog
import android.app.DownloadManager
import android.app.DownloadManager.*
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_second.*
import org.jetbrains.anko.*
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.time.LocalDate
import java.util.*

class SecondActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        var fileDownloaded = false
        var fileLoaded = false
        var id: Long = 0
        lateinit var downloadedFile: ParcelFileDescriptor
        lateinit var csv: InputStream

        downloadFile2.setOnClickListener {
            if (editLink2.text.toString().contains("https")) {
                Thread {
                    id = downloadFile(editLink2.text.toString())
                    fileDownloaded = true
                    fileLoaded = true
                }.start()
                statusTextView2.text = "파일 로드 완료"
            } else {
                toast("Error: 유효한 주소를 입력해 주세요.")
            }
        }

        textViewFrom.setOnClickListener {
            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val day = cal.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                val realmMonth = mMonth+1
                textViewFrom.text = "$mYear. $realmMonth. $mDay"
            }, year, month, day)

            dpd.show()
        }

        textViewTo.setOnClickListener {
            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val day = cal.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                val realmMonth = mMonth+1
                textViewTo.text = "$mYear. $realmMonth. $mDay"
            }, year, month, day)

            dpd.show()
        }

        searchSpecialList.setOnClickListener {
            if (fileLoaded) {
                val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                try {
                    downloadedFile = manager.openDownloadedFile(id)
                    csv = FileInputStream(downloadedFile.fileDescriptor)

                    val startDate = textViewFrom.text
                    val endDate = textViewTo.text

                    val startNotEntered: Boolean = startDate == "터치하여 검색 시작일 선택"
                    val endNotEntered: Boolean = endDate == "터치하여 검색 종료일 선택"

                    if (startNotEntered || endNotEntered){
                        toast("검색 범위를 지정해 주세요.")
                    } else {
                        val startDateList = startDate.split(". ")
                        val endDateList = endDate.split(". ")

                        val lStartDate = LocalDate.of(startDateList[0].toInt(), startDateList[1].toInt(), startDateList[2].toInt())
                        val lEndDate = LocalDate.of(endDateList[0].toInt(), endDateList[1].toInt(), endDateList[2].toInt())

                        var foundRow = mutableListOf<String>()
                        csvReader().open(csv) {
                            readAllAsSequence().forEach { row ->
                                if (row.contains("예") || row.contains("확진자") || row.contains("확진자의 접촉자") || row.contains("PCR검사자")) {
                                    val dateList = row[0].split(". ")
                                    val realDay = dateList[2].split(" ")[0]
                                    val lDate = LocalDate.of(dateList[0].toInt(), dateList[1].toInt(), realDay.toInt())
                                    if (lDate in lStartDate..lEndDate) {
                                        foundRow.add(row.joinToString(", "))
                                    }
                                }
                            }
                        }
                        if (foundRow.isEmpty()) {
                            toast("해당되는 예비군이 없습니다.")
                        } else {
                            startActivity<ListActivity>(
                                "searchedName" to foundRow
                            )
                        }
                    }
                } catch (e: FileNotFoundException) {
                    toast("Error: 다운로드가 완료되지 않았습니다.")
                }
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