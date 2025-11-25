package com.example.ourdompet.ui.screen

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ourdompet.ui.screen.HomeViewModel
import java.io.File
import java.io.FileOutputStream

@Composable
fun ExportPdfScreen(
    onBack: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    var generating by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {

        Text("Export Laporan Transaksi ke PDF", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                generating = true
                generatePdf(context, viewModel.transactionList) {
                    generating = false
                }
            }
        ) {
            Text("Generate PDF")
        }

        Spacer(Modifier.height(20.dp))

        if (generating) {
            CircularProgressIndicator()
        }

        Spacer(Modifier.height(20.dp))

        Button(onClick = onBack) {
            Text("Kembali")
        }
    }
}

fun generatePdf(
    context: Context,
    transactions: List<com.example.ourdompet.model.Transaction>,
    done: () -> Unit
) {
    val pdf = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdf.startPage(pageInfo)
    val canvas = page.canvas
    val paint = Paint()

    var yPos = 40

    paint.textSize = 18f
    canvas.drawText("Laporan Transaksi - Dompet Kita", 20f, yPos.toFloat(), paint)

    paint.textSize = 12f
    yPos += 40

    for (t in transactions) {
        val line =
            "${t.category} | Rp ${t.amount} | ${t.type} | ${t.date}"
        canvas.drawText(line, 20f, yPos.toFloat(), paint)
        yPos += 20
    }

    pdf.finishPage(page)

    val folder = File(
        context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
        "OurDompet"
    )
    if (!folder.exists()) folder.mkdirs()

    val file = File(folder, "Laporan_Transaksi.pdf")
    pdf.writeTo(FileOutputStream(file))
    pdf.close()

    Toast.makeText(context, "PDF disimpan di: ${file.path}", Toast.LENGTH_LONG).show()
    done()
}
