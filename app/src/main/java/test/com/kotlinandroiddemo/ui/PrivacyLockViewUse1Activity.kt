package test.com.kotlinandroiddemo.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

import test.com.kotlinandroiddemo.R

class PrivacyLockViewUse1Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_lock_view_use1)

        setSupportActionBar(toolbar)

        supportActionBar?.title = intent.getStringExtra("title")
        supportActionBar?.subtitle = intent.getStringExtra("desc")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

    }
}
