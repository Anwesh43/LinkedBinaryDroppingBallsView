package com.anwesh.uiprojects.linkedbinarydroppingballsview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.binarydroppingballsview.BinaryDroppingBallsView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BinaryDroppingBallsView.create(this)
    }
}
