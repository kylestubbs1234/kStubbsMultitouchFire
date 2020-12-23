package edu.ucsb.cs.cs184.kstubbsmultitouchfire

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DrawingViewModel : ViewModel() {
    var pathsX = MutableLiveData<ArrayList<ArrayList<Float>>>().apply {
        value = ArrayList()
    }

    var pathsY = MutableLiveData<ArrayList<ArrayList<Float>>>().apply {
        value = ArrayList()
    }

    var removedPathsX = MutableLiveData<ArrayList<ArrayList<Float>>>().apply {
        value = ArrayList()
    }

    var removedPathsY = MutableLiveData<ArrayList<ArrayList<Float>>>().apply {
        value = ArrayList()
    }

    var firstInitialized = false

    fun addPath(index: Int, fab2Pressed: Boolean) {
        var database: FirebaseDatabase = Firebase.database
        var myRef: DatabaseReference
        if (!fab2Pressed) {
            myRef = database.getReference("removedPaths")
            myRef.removeValue()
            removedPathsX.value!!.clear()
            removedPathsY.value!!.clear()
            removedPathsX.value = ArrayList()
            removedPathsY.value = ArrayList()
        }
        myRef = database.getReference("currentPaths")
        for (i in 0 until pathsX.value!!.get(index).size) {
            myRef.child(index.toString()).child("x").child(i.toString()).setValue(pathsX.value!!.get(index).get(i))
            myRef.child(index.toString()).child("y").child(i.toString()).setValue(pathsY.value!!.get(index).get(i))
        }
    }

    fun addRemovedPath(index: Int) {
        var database: FirebaseDatabase = Firebase.database
        var myRef: DatabaseReference = database.getReference("removedPaths")
        for (i in 0 until removedPathsX.value!!.get(index).size) {
            myRef.child(index.toString()).child("x").child(i.toString()).setValue(removedPathsX.value!!.get(index).get(i))
            myRef.child(index.toString()).child("y").child(i.toString()).setValue(removedPathsY.value!!.get(index).get(i))
        }
    }
}