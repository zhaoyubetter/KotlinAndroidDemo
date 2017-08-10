package test.com.widget.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import test.com.widget.R

class NestedHorizonalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.widget__activity_nested_horizontal)

        val list = mutableListOf<String>()
//        (0..100).forEach { list.add("item $it") }
    }
}
