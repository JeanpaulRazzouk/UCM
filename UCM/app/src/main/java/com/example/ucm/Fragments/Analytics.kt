package com.example.ucm.Fragments

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
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Analytics : Fragment() {
    private var user: FirebaseUser? = null
    private val storage: FirebaseStorage? = null
    private val storageReference: StorageReference? = null
    private val mDatabase: DatabaseReference? = null
    //
    var aaChartView: AAChartView? = null
    var aaChartView2: AAChartView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_analytics, container, false)
        aaChartView = view.findViewById(R.id.aa_chart_view)
        aaChartView2 = view.findViewById(R.id.aa_chart_view2)
        //
        FirstGraph()
        SecondGraph()
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
                    array_location?.add(dataSnapshot.child("Transactions").child(i.toString()).child("Name").value.toString())
                    array_amount?.add(dataSnapshot.child("Transactions").child(i.toString()).child("Amount").value.toString())
                }

                for (j in 0 until Count.toInt()) {
                    for (i in j+1 until Count.toInt()) {
                        if (array_location.get(j)==array_location.get(i)) {
                            var TheMethod2: ArrayList<Float> = arrayListOf()

                            if (TheMethodName.contains(array_location.get(j))) {
                                val c = TheMethodName.indexOf(array_location.get(j))
                                TheMethod.get(c).add(array_amount.get(i).toFloat())
                            }else{
                                TheMethodName.add(array_location.get(i))
                                TheMethod2.add(array_amount.get(i).toFloat())
                                TheMethod2.add(array_amount.get(j).toFloat())
                                TheMethod.add(TheMethod2)
                            }
                        }
                        else{
                            var TheMethod3: ArrayList<Float> = arrayListOf()
                            if (TheMethodName.contains(array_location.get(j))) {
                                if (i<1) {
                                    val c = TheMethodName.indexOf(array_location.get(j))
                                    TheMethod.get(c).add(array_amount.get(i).toFloat())
                                }
                            }
                            else {
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
                        .chartType(AAChartType.Polygon)
                        .title("Spending Spree By Places")
                        .subtitle("Amount Spent By User in different Places")
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
                    array_location?.add(dataSnapshot.child("Transactions").child(i.toString()).child("Location").value.toString())
                    array_amount?.add(dataSnapshot.child("Transactions").child(i.toString()).child("Amount").value.toString())
                }

                for (j in 0 until Count.toInt()) {
                    for (i in j+1 until Count.toInt()) {
                        if (array_location.get(j)==array_location.get(i)) {
                            var TheMethod2: ArrayList<Float> = arrayListOf()

                            if (TheMethodName.contains(array_location.get(j))) {
                                val c = TheMethodName.indexOf(array_location.get(j))
                                TheMethod.get(c).add(array_amount.get(i).toFloat())
                            }else{
                                TheMethodName.add(array_location.get(i))
                                TheMethod2.add(array_amount.get(i).toFloat())
                                TheMethod2.add(array_amount.get(j).toFloat())
                                TheMethod.add(TheMethod2)
                            }
                        }
                        else{
                            var TheMethod3: ArrayList<Float> = arrayListOf()
                            if (TheMethodName.contains(array_location.get(j))) {
                                if (i<1) {
                                    val c = TheMethodName.indexOf(array_location.get(j))
                                    TheMethod.get(c).add(array_amount.get(i).toFloat())
                                }
                            }
                            else {
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
                        .chartType(AAChartType.Bubble)
                        .title("Spending Spree By Places")
                        .subtitle("Amount Spent By User in different Places")
                        .backgroundColor("#fafafa")
                        .dataLabelsEnabled(true)
                        .xAxisLabelsEnabled(false)
                        .series(aaSeries.toTypedArray())
                aaChartView2?.aa_drawChartWithChartModel(aaChartModel)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}