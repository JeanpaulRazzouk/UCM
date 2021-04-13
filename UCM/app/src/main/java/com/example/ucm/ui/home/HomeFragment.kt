package com.example.ucm.ui.home

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ucm.R
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File

class HomeFragment : Fragment() {
    var textView: TextView? = null
    var textView2: TextView? = null
    var imageButton: ImageButton? = null
    var link: Uri? = null
    var user: FirebaseUser? = null
    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var homeViewModel: HomeViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        access_Dta()
        try {
            uploadImage()
            GetImage()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        textView = root.findViewById(R.id.textView)
        textView2 = root.findViewById(R.id.textView7)
        imageButton = root.findViewById(R.id.imageButton)
        val aaChartView = root.findViewById<AAChartView>(R.id.aa_chart_view)
        //imageButton.setClipToOutline(true)
        return root
    }

    fun firstgraph(){
        val aaChartModel : AAChartModel = AAChartModel()
                .chartType(AAChartType.Area)
                .title("title")
                .subtitle("subtitle")
                .backgroundColor("#4b2b7f")
                .dataLabelsEnabled(true)
                .series(arrayOf(
                        AASeriesElement()
                                .name("Tokyo")
                                .data(arrayOf(7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6)),
                        AASeriesElement()
                                .name("NewYork")
                                .data(arrayOf(0.2, 0.8, 5.7, 11.3, 17.0, 22.0, 24.8, 24.1, 20.1, 14.1, 8.6, 2.5)),
                        AASeriesElement()
                                .name("London")
                                .data(arrayOf(0.9, 0.6, 3.5, 8.4, 13.5, 17.0, 18.6, 17.9, 14.3, 9.0, 3.9, 1.0)),
                        AASeriesElement()
                                .name("Berlin")
                                .data(arrayOf(3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8))
                )
                )
    }


    fun access_Dta() {
        user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            user = FirebaseAuth.getInstance().currentUser
            textView!!.text = " Hello,"
            textView2!!.text = user!!.displayName
            //val shader: Shader = LinearGradient(180, 220, 0, textView!!.lineHeight,
                    //Color.parseColor("#2196F3"), Color.parseColor("#D267E4"), Shader.TileMode.REPEAT)
           // textView2!!.paint.shader = shader
        }
    }

    fun Gallery(view: View?) {
        val pickImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageIntent.type = "image/*"
        startActivityForResult(pickImageIntent, 1)
    }

    fun uploadImage() {
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.getReference()
        if (link != null) {
            val progressDialog = ProgressDialog(context)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()
            val ref: StorageReference = storageReference!!.child("images/" + user!!.uid)
            ref.putFile(link!!)
                    .addOnSuccessListener(OnSuccessListener<Any?> {
                        progressDialog.dismiss()
                        try {
                            GetImage()
                        } catch (e: Exception) {
                        }
                        Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show()
                    })
                    .addOnFailureListener(OnFailureListener { e ->
                        progressDialog.dismiss()
                        Toast.makeText(context, "Failed " + e.message, Toast.LENGTH_SHORT).show()
                    })
                    .addOnProgressListener { TODO("Not yet implemented") }
        }
    }

    @Throws(Exception::class)
    fun GetImage() {
        val storageRef: StorageReference = storageReference!!.child("images/" + user!!.uid)
        val localFile = File.createTempFile("images", "jpg")
        localFile.mkdir()
        storageRef.getFile(localFile)
                .addOnSuccessListener(OnSuccessListener<Any?> {
                    val bitmap = BitmapFactory.decodeFile(localFile.path)
                    imageButton!!.setImageBitmap(bitmap)
                }).addOnFailureListener(OnFailureListener {
                    // Handle failed download
                    // ...
                })
    }
}