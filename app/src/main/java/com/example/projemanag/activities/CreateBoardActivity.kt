package com.example.projemanag.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.projemanag.R

class CreateBoardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_board)
        setupActionBar()
    }

    private fun setupActionBar(){
        val toolbarCreateBoard = findViewById<Toolbar>(R.id.toolbar_create_board_activity)
        setSupportActionBar(toolbarCreateBoard)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.create_board_title)
        }

        toolbarCreateBoard.setNavigationOnClickListener {
            onBackPressed()
        }

    }

}