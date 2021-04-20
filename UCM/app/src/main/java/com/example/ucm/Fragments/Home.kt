package com.example.ucm.Fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.ucm.R
import com.example.ucm.Settings
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*
import kotlin.collections.ArrayList

class Home : Fragment() {
    private var user: FirebaseUser? = null
    private val storage: FirebaseStorage? = null
    private val storageReference: StorageReference? = null
    private var mDatabase: DatabaseReference? = null

    //
    var textView: TextView? = null
    var imageButton: ImageButton? = null
    var aaChartView:AAChartView? = null
    var aaChartView2:AAChartView? = null
    var aaChartView3:AAChartView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        //
        activity?.getWindow()?.getDecorView()?.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        textView = view.findViewById(R.id.welcome_name)
        imageButton = view.findViewById(R.id.imageButton2)
        aaChartView = view.findViewById<AAChartView>(R.id.aa_chart_view)
        aaChartView2 = view.findViewById<AAChartView>(R.id.aa_chart_view_2)
        aaChartView3 = view.findViewById<AAChartView>(R.id.aa_chart_view_3)
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
        return view
    }

   fun FirstGraph(){
       //TODO() Design Data Algorithm
       val aaChartModel : AAChartModel = AAChartModel()
               .chartType(AAChartType.Scatter)
               .title("title")
               .subtitle("subtitle")
               .backgroundColor("#fafafa")
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
       aaChartView?.aa_drawChartWithChartModel(aaChartModel)
   }

    fun SecondGraph(){
        //TODO() Design Data Algorithm

        user = FirebaseAuth.getInstance().currentUser
        FirebaseDatabase.getInstance().getReference("Users").child(user!!.uid).addValueEventListener(object : ValueEventListener {
            @RequiresApi(api = Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // retrieving Data
                val array_location: ArrayList<String> = arrayListOf();
                val array_amount: ArrayList<String> = arrayListOf();
                var aaSeriesElement: AASeriesElement
                var aaSeries: ArrayList<AASeriesElement> = arrayListOf()
                //
                val Count = dataSnapshot.child("User Data").child("Transaction count").value.toString()
                for (i in 0 until Count.toInt()) {
                    array_location?.add(dataSnapshot.child("Transactions").child(i.toString()).child("Location").value.toString())
                    array_amount?.add(dataSnapshot.child("Transactions").child(i.toString()).child("Amount").value.toString())
                }

                //TODO() Multiple Dots in one Location;

                for (j in 0 until array_amount.size) {
                    aaSeriesElement = AASeriesElement().name(array_location.get(j)).data(arrayOf(array_amount.get(j).toInt()));
                    aaSeries.add(aaSeriesElement)
                }

                val aaChartModel: AAChartModel = AAChartModel()
                        .chartType(AAChartType.Scatter)
                        .title("Spending Spree By Country")
                        .subtitle("Amount Spent By User in different Places")
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
        //TODO() Design Data Algorithm
        val aaChartModel : AAChartModel = AAChartModel()
                .chartType(AAChartType.Line)
                .title("title")
                .subtitle("subtitle")
                .backgroundColor("#fafafa")
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
        aaChartView3?.aa_drawChartWithChartModel(aaChartModel)

    }

}
