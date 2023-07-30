package com.joaovitormo.pdfgenerator



import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.print.PrintAttributes
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.uttampanchasara.pdfgenerator.CreatePdf
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), CreatePdf.PdfCallbackListener {

    override fun onSuccess(filePath: String) {
        Log.i("MainActivity", "Pdf Saved at: $filePath")

        Toast.makeText(this, "Pdf Saved at: $filePath", Toast.LENGTH_SHORT).show()
    }

    override fun onFailure(errorMsg: String) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
    }

    var openPrintDialog: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnPrint: Button = findViewById<Button>(R.id.btnPrint)
        val btnPrintAndSave: Button = findViewById<Button>(R.id.btnPrintAndSave)


        btnPrint.setOnClickListener {
            openPrintDialog = false
            doPrint()
        }

        btnPrintAndSave.setOnClickListener {
            openPrintDialog = true
            doPrint()
        }

        if (!isStoragePermissionGranted()){
            requestStoragePermission()
        }

    }

    private fun isStoragePermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE),
            READ_EXTERNAL_STORAGE_CODE
        )
    }

    companion object {
        private const val READ_EXTERNAL_STORAGE_CODE = 1000
        private const val READ_EXTERNAL_STORAGE ="android.permission.READ_EXTERNAL_STORAGE"
        private const val WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE"
    }

    private fun doPrint() {
        val pdfname : String = java.time.Instant.now().toString().replace(":", "").replace(".","").replace("-","")
        val htmlContent : String = "<html><body><h1> $pdfname Exemplo de PDF gerado a partir de HTML</h1><p>Seu conteúdo aqui...</p></body></html>"

        CreatePdf(this)
            .setPdfName("PDF" + pdfname)
            .openPrintDialog(openPrintDialog)
            .setContentBaseUrl(null)
            .setPageSize(PrintAttributes.MediaSize.ISO_A4)
            .setFilePath(Environment.getExternalStorageDirectory().absolutePath + "/MyPdf")
            .setContent(htmlContent)
            .setCallbackListener(object : CreatePdf.PdfCallbackListener {
                override fun onFailure(errorMsg: String) {
                    Toast.makeText(this@MainActivity, errorMsg, Toast.LENGTH_SHORT).show()
                }

                override fun onSuccess(filePath: String) {
                    Toast.makeText(this@MainActivity, "Pdf Saved at: $filePath", Toast.LENGTH_SHORT).show()
                }
            })
            .create()
    }


}

