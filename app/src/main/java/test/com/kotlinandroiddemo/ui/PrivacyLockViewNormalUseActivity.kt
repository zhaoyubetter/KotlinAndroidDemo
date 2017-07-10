package test.com.kotlinandroiddemo.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_privacy_lock_view_use1.*
import test.com.kotlinandroiddemo.widget.PrivacyLockView.OnTextSubmitListener
//import org.jetbrains.anko.*

import test.com.kotlinandroiddemo.R

class PrivacyLockViewNormalUseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_lock_view_use1)

        setSupportActionBar(toolbar)

        supportActionBar?.title = intent.getStringExtra("title")
        supportActionBar?.subtitle = intent.getStringExtra("desc")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // 事件监听
        privacyView.setOnTextSubmitListener(object : OnTextSubmitListener {
            override fun onSubmit(editable: CharSequence) {
                Toast.makeText(applicationContext, "输入完毕：" + editable.toString(), Toast.LENGTH_SHORT).show()
            }
        })

        chk.setOnCheckedChangeListener { _, isChecked ->
            privacyView.mEncrypt = isChecked
        }

        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                privacyView.mItemCount = progress
            }
        })

        seekbar2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                privacyView.mItemPadding = progress
            }
        })
    }
}
