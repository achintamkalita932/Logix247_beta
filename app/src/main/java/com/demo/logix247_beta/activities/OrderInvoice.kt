package com.demo.logix247_beta.activities

import android.content.Context
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.print.pdf.PrintedPdfDocument
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.demo.logix247_beta.Models.Orders
import com.demo.logix247_beta.R
import com.google.firebase.firestore.FirebaseFirestore
import java.io.FileOutputStream
import java.io.IOException

class OrderInvoice : AppCompatActivity() {

    private lateinit var orderID: String
    private lateinit var print: Button
    private lateinit var id: TextView
    private lateinit var date: TextView
    private lateinit var createdBy: TextView
    private lateinit var assignedTo: TextView
    private lateinit var orderDesc: TextView
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_order_invoice)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        init()
        orderID = intent.getStringExtra("orderID") ?: ""

        db = FirebaseFirestore.getInstance()
        db.collection("Orders").whereEqualTo("id", orderID)  // Query to find the order by its ID
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val order = documents.documents[0].toObject(Orders::class.java)
                    updateUI(order)
                } else {
                    Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show()
                    Log.d("OrderDetailActivity", "No such order")
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error getting order details", Toast.LENGTH_SHORT).show()
                Log.d("OrderDetailActivity", "Error getting order details: ", exception)
            }

        print.setOnClickListener {
            printDocument()
        }
    }

    private fun updateUI(order: Orders?) {
        if (order != null) {
            id.text = order.id
            date.text = order.shippedDate // Assuming 'shippedDate' is the correct field name
            createdBy.text = order.itemOwner
            assignedTo.text = order.deliveryPartner
            orderDesc.text = order.itemDescription
        } else {
            Log.d("OrderInvoice", "Order data is null")
        }
    }

    private fun createPrintDocumentAdapter(docName: String): PrintDocumentAdapter {
        val view = findViewById<View>(R.id.mainOrderInvoice)
        return object : PrintDocumentAdapter() {
            var pdfDocument: PrintedPdfDocument? = null

            override fun onLayout(
                oldAttributes: PrintAttributes?,
                newAttributes: PrintAttributes,
                cancellationSignal: CancellationSignal,
                callback: LayoutResultCallback,
                extras: Bundle?
            ) {
                if (cancellationSignal.isCanceled) {
                    callback.onLayoutCancelled()
                    return
                }

                pdfDocument = PrintedPdfDocument(this@OrderInvoice, newAttributes)

                val info = PrintDocumentInfo.Builder(docName)
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(1)
                    .build()

                callback.onLayoutFinished(info, !newAttributes.equals(oldAttributes))
            }

            override fun onWrite(
                pages: Array<PageRange>,
                destination: ParcelFileDescriptor,
                cancellationSignal: CancellationSignal,
                callback: WriteResultCallback
            ) {
                val page = pdfDocument?.startPage(1)

                // Draw the entire view hierarchy onto the PDF canvas
                if (page != null) {
                    val canvas = page.canvas
                    val scale = page.info.pageWidth.toFloat() / view.width
                    canvas.save()
                    canvas.scale(scale, scale)
                    view.draw(canvas)
                    canvas.restore()
                    pdfDocument?.finishPage(page)
                }

                try {
                    pdfDocument?.writeTo(FileOutputStream(destination.fileDescriptor))
                    callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                } catch (e: IOException) {
                    Log.e("PrintDocumentAdapter", "Error writing document", e)
                    callback.onWriteFailed(e.toString())
                } finally {
                    pdfDocument?.close()
                    pdfDocument = null
                }
            }

        }
    }

    private fun printDocument() {
        val printManager = getSystemService(Context.PRINT_SERVICE) as PrintManager
        val adapter = createPrintDocumentAdapter("Invoice")
        val attributes = PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
            .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
            .setResolution(PrintAttributes.Resolution("id", PRINT_SERVICE, 900, 450))
            .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
            .build()

        printManager.print("Order_Invoice", adapter, attributes)
    }


    private fun init() {
        print = findViewById(R.id.printPDFInvoice)
        id = findViewById(R.id.orderIDInvoice)
        date = findViewById(R.id.dateInvoice)
        createdBy = findViewById(R.id.createdByInvoice)
        assignedTo = findViewById(R.id.assignedToInvoice)
        orderDesc = findViewById(R.id.orderDescInvoice)
    }
}