package edu.ucsb.cs.cs184.kstubbsmultitouchfire

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.SparseArray
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class DrawView : SurfaceView, SurfaceHolder.Callback {

    var xval = arrayListOf<ArrayList<Float>>()
    var yval = arrayListOf<ArrayList<Float>>()

    private val strokeColors: IntArray = resources.getIntArray(R.array.strokeColors)

    var paint: Paint = Paint(Color.RED)

    lateinit var surfaceHolder: SurfaceHolder

    var fragment: DrawingFragment = DrawingFragment()

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        setWillNotDraw(false)
        surfaceHolder = holder
        surfaceHolder.addCallback(this)
    }

    var activePointerArray: SparseArray<Int> = SparseArray()
    var isCurrentlyDrawing = false

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                isCurrentlyDrawing = true
                var index = event.actionIndex
                var pointerID = event.getPointerId(index)
                var x = event.getX(pointerID)
                var y = event.getY(pointerID)

                xval.add(ArrayList())
                yval.add(ArrayList())
                xval.get(xval.size - 1).add(x)
                yval.get(yval.size - 1).add(y)
                activePointerArray.put(pointerID, xval.size - 1) //put in xval.size - 1, out comes the pointerID
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                var index = event.actionIndex
                var pointerID = event.getPointerId(index)
                var x = event.getX(pointerID)
                var y = event.getY(pointerID)

                xval.add(ArrayList())
                yval.add(ArrayList())
                xval.get(xval.size - 1).add(x)
                yval.get(yval.size - 1).add(y)
                activePointerArray.put(pointerID, xval.size - 1)
            }
            MotionEvent.ACTION_MOVE -> {
                for (i in 0 until event.pointerCount) {
                    var pointerId = event.getPointerId(i)
                    xval.get(activePointerArray.get(pointerId)).add(event.getX(i))
                    yval.get(activePointerArray.get(pointerId)).add(event.getY(i))
                }
            }
            MotionEvent.ACTION_POINTER_UP -> {
                var index = event.actionIndex
                var pointerID = event.getPointerId(index)
                fragment.updateViewModel(activePointerArray.get(pointerID))
                activePointerArray.removeAt(pointerID)
            }
            MotionEvent.ACTION_UP -> {
                isCurrentlyDrawing = false
                var index = event.actionIndex
                var pointerID = event.getPointerId(index)
                fragment.updateViewModel(activePointerArray.get(pointerID))
                activePointerArray.removeAt(pointerID)
            }
            else -> return false
        }
        invalidate()

        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (xval.size <= 0 || xval.size != yval.size)
            return
        for (i in 0 until xval.size) {
            paint.color = strokeColors[i % 5]
            for (j in 0 until xval.get(i).size) {
                canvas.drawCircle(
                    xval.get(i).get(j),
                    yval.get(i).get(j),
                    4f,
                    paint
                )
                if (j < xval.get(i).size - 1) {
                    canvas.drawLine(
                        xval.get(i).get(j),
                        yval.get(i).get(j),
                        xval.get(i).get(j + 1),
                        yval.get(i).get(j + 1),
                        paint
                    )
                }
            }
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 10f
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {

    }
}
