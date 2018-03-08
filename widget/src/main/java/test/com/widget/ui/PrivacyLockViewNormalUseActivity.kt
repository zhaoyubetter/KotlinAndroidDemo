package test.com.widget.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.SeekBar
import kotlinx.android.synthetic.main.widget_activity_privacy_lock_view_use1.*
import org.jetbrains.anko.toast
import test.com.widget.R

class PrivacyLockViewNormalUseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.widget_activity_privacy_lock_view_use1)
        title = intent.getStringExtra("title")
        toolbar.title = title
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // 事件监听
        privacyView.setOnTextSubmitListener {
            toast(resources.getString(R.string.widget_user_input) + it.toString())
        }

        chk.setOnCheckedChangeListener { _, isChecked ->
            privacyView.setEncrypt(isChecked)
        }

        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                //最小为1,0会抛错,超出最大值10,也会抛错
                privacyView.setItemCount(1 + progress)
            }
        })

        seekbar2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                privacyView.setItemPadding(progress.toFloat())
            }
        })
    }
}
