package com.example.ucm.Fragments

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.ucm.R
import com.example.ucm.Settings
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAStyle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File
import java.util.*

class Home : Fragment() {
    private var user: FirebaseUser? = null
    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var mDatabase: DatabaseReference? = null

    //
    var textView: TextView? = null
    var textView2: TextView? = null
    var textView3: TextView? = null
    var textView4: TextView? = null
    var imageButton: ImageButton? = null
    var imageview: ImageView? = null
    var aaChartView:AAChartView? = null
    var aaChartView2:AAChartView? = null
    var aaChartView3:AAChartView? = null
    var aaChartView4:AAChartView? = null
    //
    var link: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        //
        activity?.getWindow()?.getDecorView()?.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        textView = view.findViewById(R.id.welcome_name)
        textView2 = view.findViewById(R.id.textView4)
        textView3 = view.findViewById(R.id.textView3)
        textView4 = view.findViewById(R.id.textView8)
        imageButton = view.findViewById(R.id.imageButton2)
        imageview = view.findViewById(R.id.imageView)
        //
        aaChartView = view.findViewById<AAChartView>(R.id.aa_chart_view)
        aaChartView2 = view.findViewById<AAChartView>(R.id.aa_chart_view_2)
        aaChartView3 = view.findViewById<AAChartView>(R.id.aa_chart_view_3)
        aaChartView4 = view.findViewById<AAChartView>(R.id.aa_chart_view_4)

        //
        user = FirebaseAuth.getInstance().currentUser
        // Getting Username;
        textView?.setText(user?.getDisplayName())
        //
        imageButton?.setOnClickListener(View.OnClickListener {
            val i = Intent(context, Settings::class.java)
            startActivity(i)
        })
        // Graph Methods()
        FirstGraph()
        SecondGraph()
        thirdGraph()
        FourthGraph()
        //
        DataCalc()
        DataCalc2()
        try {
            uploadImage()
            GetImage()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val paint = textView!!.paint
        val width = paint.measureText(textView!!.text.toString())
        val textShader: Shader = LinearGradient(180f, 220f, width, textView!!.textSize, intArrayOf(
                Color.parseColor("#2196F3"), Color.parseColor("#D267E4")
        ), null, Shader.TileMode.REPEAT)

        textView!!.paint.shader = textShader
        return view
    }

   fun FirstGraph(){
       user = FirebaseAuth.getInstance().currentUser
       FirebaseDatabase.getInstance().getReference("Users").child(user!!.uid).addValueEventListener(object : ValueEventListener {
           @RequiresApi(api = Build.VERSION_CODES.O)
           override fun onDataChange(dataSnapshot: DataSnapshot) {
               // retrieving Data
               val array_location: ArrayList<String> = arrayListOf();
               val array_amount: ArrayList<String> = arrayListOf();
               var aaSeriesElement: AASeriesElement
               var aaSeries: ArrayList<AASeriesElement> = arrayListOf()
               var TheMethod: ArrayList<ArrayList<Float>> = arrayListOf()
               var TheMethodName: ArrayList<String> = arrayListOf()
               //
               val Count = dataSnapshot.child("User Data").child("Transaction count").value.toString()
               for (i in 0 until Count.toInt()) {
                   array_location?.add(dataSnapshot.child("Transactions").child(i.toString()).child("Location").value.toString())
                   array_amount?.add(dataSnapshot.child("Transactions").child(i.toString()).child("Amount").value.toString())
               }

               for (j in 0 until Count.toInt()) {
                   for (i in j + 1 until Count.toInt()) {
                       if (array_location.get(j) == array_location.get(i)) {
                           var TheMethod2: ArrayList<Float> = arrayListOf()

                           if (TheMethodName.contains(array_location.get(j))) {
                               val c = TheMethodName.indexOf(array_location.get(j))
                               TheMethod.get(c).add(array_amount.get(i).toFloat())
                           } else {
                               TheMethodName.add(array_location.get(i))
                               TheMethod2.add(array_amount.get(i).toFloat())
                               TheMethod2.add(array_amount.get(j).toFloat())
                               TheMethod.add(TheMethod2)
                           }
                       } else {
                           var TheMethod3: ArrayList<Float> = arrayListOf()
                           if (TheMethodName.contains(array_location.get(j))) {
                               if (i < 1) {
                                   val c = TheMethodName.indexOf(array_location.get(j))
                                   TheMethod.get(c).add(array_amount.get(i).toFloat())
                               }
                           } else {
                               TheMethodName.add(array_location.get(i))
                               TheMethod3.add(array_amount.get(i).toFloat())
                               TheMethod.add(TheMethod3)
                           }
                       }
                   }
               }


               for (j in 0 until TheMethodName.size) {
                   aaSeriesElement = AASeriesElement().name(TheMethodName.get(j))
                           .data(TheMethod.get(j).toTypedArray());
                   aaSeries.add(aaSeriesElement)
               }

               val aaChartModel: AAChartModel = AAChartModel()
                       .chartType(AAChartType.Line)
                       .title("Spending Spree By Country")
                       .titleStyle(AAStyle().fontWeight(AAChartFontWeightType.Bold).fontSize(25.0f))
                       .subtitle("Amount Spent By User in different Places")
                       .subtitleStyle(AAStyle().fontSize(15.0f))
                       .backgroundColor("#fafafa")
                       .dataLabelsEnabled(true)
                       .series(aaSeries.toTypedArray())
               aaChartView?.aa_drawChartWithChartModel(aaChartModel)
           }

           override fun onCancelled(error: DatabaseError) {
               TODO("Not yet implemented")
           }
       })
   }

    fun SecondGraph(){
        user = FirebaseAuth.getInstance().currentUser
        FirebaseDatabase.getInstance().getReference("Users").child(user!!.uid).addValueEventListener(object : ValueEventListener {
            @RequiresApi(api = Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // retrieving Data
                val array_location: ArrayList<String> = arrayListOf();
                val array_amount: ArrayList<String> = arrayListOf();
                var aaSeriesElement: AASeriesElement
                var aaSeries: ArrayList<AASeriesElement> = arrayListOf()
                var TheMethod: ArrayList<ArrayList<Float>> = arrayListOf()
                var TheMethodName: ArrayList<String> = arrayListOf()
                //
                val Count = dataSnapshot.child("User Data").child("Transaction count").value.toString()
                for (i in 0 until Count.toInt()) {
                    array_location?.add(dataSnapshot.child("Transactions").child(i.toString()).child("Name").value.toString())
                    array_amount?.add(dataSnapshot.child("Transactions").child(i.toString()).child("Amount").value.toString())
                }

                for (j in 0 until Count.toInt()) {
                    for (i in j + 1 until Count.toInt()) {
                        if (array_location.get(j) == array_location.get(i)) {
                            var TheMethod2: ArrayList<Float> = arrayListOf()

                            if (TheMethodName.contains(array_location.get(j))) {
                                val c = TheMethodName.indexOf(array_location.get(j))
                                TheMethod.get(c).add(array_amount.get(i).toFloat())
                            } else {
                                TheMethodName.add(array_location.get(i))
                                TheMethod2.add(array_amount.get(i).toFloat())
                                TheMethod2.add(array_amount.get(j).toFloat())
                                TheMethod.add(TheMethod2)
                            }
                        } else {
                            var TheMethod3: ArrayList<Float> = arrayListOf()
                            if (TheMethodName.contains(array_location.get(j))) {
                                if (i < 1) {
                                    val c = TheMethodName.indexOf(array_location.get(j))
                                    TheMethod.get(c).add(array_amount.get(i).toFloat())
                                }
                            } else {
                                TheMethodName.add(array_location.get(i))
                                TheMethod3.add(array_amount.get(i).toFloat())
                                TheMethod.add(TheMethod3)
                            }
                        }
                    }
                }


                for (j in 0 until TheMethodName.size) {
                    aaSeriesElement = AASeriesElement().name(TheMethodName.get(j))
                            .data(TheMethod.get(j).toTypedArray());
                    aaSeries.add(aaSeriesElement)
                }

                val aaChartModel: AAChartModel = AAChartModel()
                        .chartType(AAChartType.Scatter)
                        .title("Spending Spree By Places")
                        .titleStyle(AAStyle().fontWeight(AAChartFontWeightType.Bold).fontSize(25.0f))
                        .subtitle("Amount Spent By User in different Places")
                        .subtitleStyle(AAStyle().fontSize(15.0f))
                        .backgroundColor("#fafafa")
                        .dataLabelsEnabled(true)
                        .series(aaSeries.toTypedArray())
                aaChartView2?.aa_drawChartWithChartModel(aaChartModel)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun thirdGraph(){
        user = FirebaseAuth.getInstance().currentUser
        FirebaseDatabase.getInstance().getReference("Users").child(user!!.uid).addValueEventListener(object : ValueEventListener {
            @RequiresApi(api = Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // retrieving Data
                val array_location: ArrayList<String> = arrayListOf();
                val array_amount: ArrayList<String> = arrayListOf();
                var aaSeriesElement: AASeriesElement
                var aaSeries: ArrayList<AASeriesElement> = arrayListOf()
                var TheMethod: ArrayList<ArrayList<Float>> = arrayListOf()
                var TheMethodName: ArrayList<String> = arrayListOf()
                //
                val Count = dataSnapshot.child("User Data").child("Transaction count").value.toString()
                for (i in 0 until Count.toInt()) {
                    array_location?.add(dataSnapshot.child("Transactions").child(i.toString()).child("Date").value.toString())
                    array_amount?.add(dataSnapshot.child("Transactions").child(i.toString()).child("Amount").value.toString())
                }

                for (j in 0 until Count.toInt()) {
                    for (i in j + 1 until Count.toInt()) {
                        if (array_location.get(j) == array_location.get(i)) {
                            var TheMethod2: ArrayList<Float> = arrayListOf()

                            if (TheMethodName.contains(array_location.get(j))) {
                                val c = TheMethodName.indexOf(array_location.get(j))
                                TheMethod.get(c).add(array_amount.get(i).toFloat())
                            } else {
                                TheMethodName.add(array_location.get(i))
                                TheMethod2.add(array_amount.get(i).toFloat())
                                TheMethod2.add(array_amount.get(j).toFloat())
                                TheMethod.add(TheMethod2)
                            }
                        } else {
                            var TheMethod3: ArrayList<Float> = arrayListOf()
                            if (TheMethodName.contains(array_location.get(j))) {
                                if (i < 1) {
                                    val c = TheMethodName.indexOf(array_location.get(j))
                                    TheMethod.get(c).add(array_amount.get(i).toFloat())
                                }
                            } else {
                                TheMethodName.add(array_location.get(i))
                                TheMethod3.add(array_amount.get(i).toFloat())
                                TheMethod.add(TheMethod3)
                            }
                        }
                    }
                }


                for (j in 0 until TheMethodName.size) {
                    aaSeriesElement = AASeriesElement().name(TheMethodName.get(j))
                            .data(TheMethod.get(j).toTypedArray());
                    aaSeries.add(aaSeriesElement)
                }

                val aaChartModel: AAChartModel = AAChartModel()
                        .chartType(AAChartType.Bar)
                        .title("Spending Spree By Places")
                        .titleStyle(AAStyle().fontWeight(AAChartFontWeightType.Bold).fontSize(25.0f))
                        .subtitle("Amount Spent By User in different Places")
                        .subtitleStyle(AAStyle().fontSize(15.0f))
                        .backgroundColor("#fafafa")
                        .dataLabelsEnabled(true)
                        .series(aaSeries.toTypedArray())
                aaChartView3?.aa_drawChartWithChartModel(aaChartModel)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }
    fun FourthGraph(){
        user = FirebaseAuth.getInstance().currentUser
        FirebaseDatabase.getInstance().getReference("Users").child(user!!.uid).addValueEventListener(object : ValueEventListener {
            @RequiresApi(api = Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // retrieving Data
                val array_location: ArrayList<String> = arrayListOf();
                val array_amount: ArrayList<String> = arrayListOf();
                var aaSeriesElement: AASeriesElement
                var aaSeries: ArrayList<AASeriesElement> = arrayListOf()
                var TheMethod: ArrayList<ArrayList<Float>> = arrayListOf()
                var TheMethodName: ArrayList<String> = arrayListOf()
                //
                val Count = dataSnapshot.child("User Data").child("Transaction count").value.toString()
                for (i in 0 until Count.toInt()) {
                    array_location?.add(dataSnapshot.child("Transactions").child(i.toString()).child("Location").value.toString())
                    array_amount?.add(dataSnapshot.child("Transactions").child(i.toString()).child("Amount").value.toString())
                }

                for (j in 0 until Count.toInt()) {
                    for (i in j + 1 until Count.toInt()) {
                        if (array_location.get(j) == array_location.get(i)) {
                            var TheMethod2: ArrayList<Float> = arrayListOf()

                            if (TheMethodName.contains(array_location.get(j))) {
                                val c = TheMethodName.indexOf(array_location.get(j))
                                TheMethod.get(c).add(array_amount.get(i).toFloat())
                            } else {
                                TheMethodName.add(array_location.get(i))
                                TheMethod2.add(array_amount.get(i).toFloat())
                                TheMethod2.add(array_amount.get(j).toFloat())
                                TheMethod.add(TheMethod2)
                            }
                        } else {
                            var TheMethod3: ArrayList<Float> = arrayListOf()
                            if (TheMethodName.contains(array_location.get(j))) {
                                if (i < 1) {
                                    val c = TheMethodName.indexOf(array_location.get(j))
                                    TheMethod.get(c).add(array_amount.get(i).toFloat())
                                }
                            } else {
                                TheMethodName.add(array_location.get(i))
                                TheMethod3.add(array_amount.get(i).toFloat())
                                TheMethod.add(TheMethod3)
                            }
                        }
                    }
                }


                for (j in 0 until TheMethodName.size) {
                    aaSeriesElement = AASeriesElement().name(TheMethodName.get(j))
                            .data(TheMethod.get(j).toTypedArray());
                    aaSeries.add(aaSeriesElement)
                }

                val aaChartModel: AAChartModel = AAChartModel()
                        .chartType(AAChartType.Area)
                        .title("Spending Spree By Places")
                        .titleStyle(AAStyle().fontWeight(AAChartFontWeightType.Bold).fontSize(25.0f))
                        .subtitle("Amount Spent By User in different Places")
                        .subtitleStyle(AAStyle().fontSize(15.0f))
                        .backgroundColor("#fafafa")
                        .dataLabelsEnabled(true)
                        .series(aaSeries.toTypedArray())
                aaChartView4?.aa_drawChartWithChartModel(aaChartModel)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }




    fun DataCalc(){
        val array_data: ArrayList<Int> = arrayListOf();
        val array_location: ArrayList<String> = arrayListOf();
        val array_amount: ArrayList<String> = arrayListOf();
        user = FirebaseAuth.getInstance().currentUser
        FirebaseDatabase.getInstance().getReference("Users").child(user!!.uid).addValueEventListener(object : ValueEventListener {
            @RequiresApi(api = Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val Count = dataSnapshot.child("User Data").child("Transaction count").value.toString()
                for (i in 0 until Count.toInt()) {
                    array_location?.add(dataSnapshot.child("Transactions").child(i.toString()).child("Location").value.toString())
                    array_amount?.add(dataSnapshot.child("Transactions").child(i.toString()).child("Amount").value.toString())
                }
                //
                for (i in 0 until Count.toInt()) {
                    val occurrences = Collections.frequency(array_location, array_location.get(i))
                    array_data.add(occurrences)
                }
                var max: Int? = array_data.maxOrNull() // get maximum value
                var min: Int? = array_data.minOrNull()
                var index: Int? = array_data.indexOf(max)
                var index2: Int? = array_data.indexOf(min)

                var per: Float? = ((max!! / Count.toFloat()) * 100).toFloat()
                var per2: Float? = ((min!! / Count.toFloat()) * 100).toFloat()


                textView2?.setText(per.toString() + " % of Transactions have been made in \n" + array_location.get(index!!))
                textView3?.setText(per2.toString() + " % of Transactions have been made in \n" + array_location.get(index2!!))
            }


            override fun onCancelled(error: DatabaseError) {

            }
        });
    }

    fun DataCalc2(){
        val array_data: ArrayList<Int> = arrayListOf();
        val array_location: ArrayList<String> = arrayListOf();
        val array_amount: ArrayList<String> = arrayListOf();
        user = FirebaseAuth.getInstance().currentUser
        FirebaseDatabase.getInstance().getReference("Users").child(user!!.uid).addValueEventListener(object : ValueEventListener {
            @RequiresApi(api = Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val Count = dataSnapshot.child("User Data").child("Transaction count").value.toString()
                for (i in 0 until Count.toInt()) {
                    array_location?.add(dataSnapshot.child("Transactions").child(i.toString()).child("Name").value.toString())
                    array_amount?.add(dataSnapshot.child("Transactions").child(i.toString()).child("Amount").value.toString())
                }
                //
                for (i in 0 until Count.toInt()) {
                    val occurrences = Collections.frequency(array_location, array_location.get(i))
                    array_data.add(occurrences)
                }
                var max: Int? = array_data.maxOrNull() // get maximum value
                var index: Int? = array_data.indexOf(max)

                var per: Float? = ((max!! / Count.toFloat()) * 100).toFloat()


                textView4?.setText(per.toString() + " % of Transactions have been made at \n" + array_location.get(index!!))
                val paint = textView4!!.paint
                val width = paint.measureText(textView4!!.text.toString())
                val textShader: Shader = LinearGradient(270f, 220f, width, textView4!!.textSize, intArrayOf(
                        Color.parseColor("#879AF2"), Color.parseColor("#D3208B"),Color.parseColor("#FDA000")
                ), null, Shader.TileMode.REPEAT)

                textView4!!.paint.shader = textShader
            }


            override fun onCancelled(error: DatabaseError) {

            }
        });
    }


    @Throws(java.lang.Exception::class)
    fun GetImage() {
        val storageRef = storageReference!!.child("images/" + user!!.uid)
        val localFile = File.createTempFile("images", "jpg")
        localFile.mkdir()
        storageRef.getFile(localFile)
                .addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeFile(localFile.path)
                    imageview?.setImageBitmap(bitmap)
                }.addOnFailureListener {
                    // Handle failed download
                    // ...
                }
    }

    fun uploadImage() {
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.getReference()
        if (link != null) {
            val progressDialog = ProgressDialog(context)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()
            val ref = storageReference!!.child("images/" + user!!.uid)
            ref.putFile(link!!)
                    .addOnSuccessListener {
                        progressDialog.dismiss()
                        try {
                            GetImage()
                        } catch (e: java.lang.Exception) {
                        }
                        Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        progressDialog.dismiss()
                        Toast.makeText(context, "Failed " + e.message, Toast.LENGTH_SHORT).show()
                    }
                    .addOnProgressListener(OnProgressListener<UploadTask.TaskSnapshot?> {
                        fun onProgress(taskSnapshot: UploadTask.TaskSnapshot?) {}
                    })
        }
    }

}
