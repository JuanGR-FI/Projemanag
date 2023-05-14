package com.example.projemanag.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.projemanag.R
import com.example.projemanag.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        setupActionBar()

        val navView = findViewById<NavigationView>(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)

    }

    private fun setupActionBar(){
        val toolbarMainActivity = findViewById<Toolbar>(R.id.toolbar_main_activity)
        setSupportActionBar(toolbarMainActivity)
        toolbarMainActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        toolbarMainActivity.setNavigationOnClickListener {
            toggleDrawer()
        }

    }

    private fun toggleDrawer(){
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)

        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)

        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            doubleBackToExit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)

        when(item.itemId){
            R.id.nav_my_profile -> {
                Toast.makeText(this@MainActivity, "My Profile", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()

            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}