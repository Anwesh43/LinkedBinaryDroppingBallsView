package com.anwesh.uiprojects.binarydroppingballsview

/**
 * Created by anweshmishra on 11/08/19.
 */

import android.view.View
import android.view.MotionEvent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color
import android.app.Activity
import android.content.Context

val nodes : Int = 5
val parts : Int = 2
val scGap : Float = 0.05f
val strokeFactor : Int = 90
val sizeFactor : Float = 4.8f
val foreColor : Int = Color.parseColor("#4527A0")
val backColor : Int = Color.parseColor("#BDBDBD")
val scDiv : Double = 0.51
val rFactor : Float = 3f

fun Int.inverse() : Float = 1f / this
fun Float.scaleFactor() : Float = Math.floor(this / scDiv).toFloat()
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int,  n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.mirrorValue(a : Int, b : Int) : Float {
    val k : Float = scaleFactor()
    return (1 - k) * a.inverse() + k * b.inverse()
}
fun Float.updateValue(dir : Float, a : Int, b : Int) : Float = mirrorValue(a, b) * dir * scGap

fun Canvas.drawDroppingBall(i : Int, h : Float, size : Float, sc1 : Float, sc2 : Float, paint : Paint) {
    val r : Float = size / rFactor
    save()
    rotate(45f * (1 - 2 * i))
    drawLine(0f, 0f, 0f, size, paint)
    drawCircle(0f, (h - size + r) * sc2.divideScale(i, parts), r * sc1.divideScale(i, parts), paint)
    restore()
}

fun Canvas.drawBDBNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val size : Float = h / sizeFactor
    val sc1 : Float = scale.divideScale(0, 2)
    val sc2 : Float = scale.divideScale(1, 2)
    paint.color = foreColor
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    for (j in 0..(parts - 1)) {
        drawDroppingBall(j, h, size, sc1, sc2, paint)
    }
}

class BinaryDroppingBallsView(ctx : Context) : View(ctx)  {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scale.updateValue(dir, parts, parts)
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            cb()
            try {
                Thread.sleep(50)
                view.invalidate()
            } catch(ex : Exception) {

            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class BDBNode(var i : Int, val state : State = State()) {

        private var next : BDBNode? = null
        private var prev : BDBNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = BDBNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawBDBNode(i, state.scale, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            state.update {
                cb(i, it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : BDBNode {
            var curr : BDBNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class BinaryDroppingBalls(var i : Int) {

        private var curr : BDBNode = BDBNode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            curr.update {i, scl ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(i, scl)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : BinaryDroppingBallsView) {

        private val bdb : BinaryDroppingBalls = BinaryDroppingBalls(0)
        private var animator : Animator = Animator(view)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            bdb.draw(canvas, paint)
            animator.animate {
                bdb.update {i, scl ->
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            bdb.startUpdating {
                animator.stop()
            }
        }
    }
}