package edu.ucsb.cs.cs184.kstubbsmultitouchfire

import android.graphics.Color
import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.drawing_fragment.*


class DrawingFragment : Fragment() {

    lateinit var database: FirebaseDatabase
    lateinit var myRef: DatabaseReference

    private lateinit var viewModel: DrawingViewModel
    private lateinit var drawView: DrawView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProviders.of(this).get(DrawingViewModel::class.java)
        val root = inflater.inflate(R.layout.drawing_fragment, container, false)
        drawView = root.findViewById(R.id.draw_view)
        drawView.setBackgroundColor(Color.WHITE)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        drawView.fragment = this
        database = Firebase.database
        myRef = database.getReference("currentPaths")

        viewModel.pathsX.observe(viewLifecycleOwner, Observer {
            drawView.xval = viewModel.pathsX.value!!
        })
        viewModel.pathsY.observe(viewLifecycleOwner, Observer {
            drawView.yval = viewModel.pathsY.value!!
        })

        myRef = database.getReference()

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!viewModel.firstInitialized) {
                    for (i in 0 until snapshot.child("currentPaths").childrenCount) {
                        var returnedListX =
                            snapshot.child("currentPaths").child(i.toString()).child("x").value
                        var returnedListY =
                            snapshot.child("currentPaths").child(i.toString()).child("y").value

                        viewModel.pathsX.value?.add((returnedListX as? java.util.ArrayList<Float>)!!)
                        viewModel.pathsY.value?.add((returnedListY as? java.util.ArrayList<Float>)!!)
                    }
                    for (i in 0 until snapshot.child("removedPaths").childrenCount) {
                        var returnedListX =
                            snapshot.child("removedPaths").child(i.toString()).child("x").value
                        var returnedListY =
                            snapshot.child("removedPaths").child(i.toString()).child("y").value

                        viewModel.removedPathsX.value?.add((returnedListX as? java.util.ArrayList<Float>)!!)
                        viewModel.removedPathsY.value?.add((returnedListY as? java.util.ArrayList<Float>)!!)
                    }
                    viewModel.firstInitialized = true
                    drawView.invalidate()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        fab1.setOnClickListener {
            if (!drawView.isCurrentlyDrawing) {
                myRef = database.getReference()
                myRef.removeValue()
                //viewModel.pathsX.value
                viewModel.pathsX.value = ArrayList()
                viewModel.pathsY.value = ArrayList()
                viewModel.removedPathsX.value = ArrayList()
                viewModel.removedPathsY.value = ArrayList()
                drawView.activePointerArray.clear()
                drawView.activePointerArray = SparseArray()
                drawView.invalidate()
            }
        }
        fab2.setOnClickListener {
            if (!drawView.isCurrentlyDrawing && viewModel.pathsX.value!!.size > 0) {
                myRef = database.getReference("currentPaths")
                if (viewModel.pathsX.value?.isEmpty() == false) {
                    viewModel.removedPathsX.value?.add(viewModel.pathsX.value!![viewModel.pathsX.value!!.size - 1])
                    myRef.child((viewModel.pathsX.value!!.size - 1).toString()).removeValue()
                    viewModel.pathsX.value!!.removeAt(viewModel.pathsX.value!!.size - 1)
                }
                if (viewModel.pathsY.value?.isEmpty() == false) {
                    viewModel.removedPathsY.value?.add(viewModel.pathsY.value!![viewModel.pathsY.value!!.size - 1])
                    myRef.child((viewModel.pathsY.value!!.size - 1).toString()).removeValue()
                    viewModel.pathsY.value!!.removeAt(viewModel.pathsY.value!!.size - 1)
                }
                viewModel.addRemovedPath(viewModel.removedPathsX.value!!.size - 1)
                drawView.invalidate()
            }
        }
        fab3.setOnClickListener {
            if (!drawView.isCurrentlyDrawing && viewModel.removedPathsX.value!!.size > 0) {
                myRef = database.getReference("removedPaths")
                if (viewModel.removedPathsX.value?.isEmpty() == false) {
                    viewModel.pathsX.value?.add(viewModel.removedPathsX.value!![viewModel.removedPathsX.value!!.size - 1])
                    myRef.child((viewModel.removedPathsX.value!!.size - 1).toString()).removeValue()
                    viewModel.removedPathsX.value!!.removeAt(viewModel.removedPathsX.value!!.size - 1)
                }
                if (viewModel.removedPathsY.value?.isEmpty() == false) {
                    viewModel.pathsY.value?.add(viewModel.removedPathsY.value!![viewModel.removedPathsY.value!!.size - 1])
                    myRef.child((viewModel.removedPathsY.value!!.size - 1).toString()).removeValue()
                    viewModel.removedPathsY.value!!.removeAt(viewModel.removedPathsY.value!!.size - 1)
                }
                viewModel.addPath(viewModel.pathsX.value!!.size - 1, true)
                drawView.invalidate()
            }
        }
    }

    fun updateViewModel(index: Int) {
        viewModel.pathsX.value = drawView.xval
        viewModel.pathsY.value = drawView.yval
        viewModel.addPath(index, false)
    }

}