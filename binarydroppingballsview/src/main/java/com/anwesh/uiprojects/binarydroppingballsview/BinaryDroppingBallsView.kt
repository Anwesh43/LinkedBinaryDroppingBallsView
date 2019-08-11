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

