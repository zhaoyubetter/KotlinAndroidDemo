package test.com.widget.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import test.com.widget.R
import test.com.widget.widget.PrivacyLockView


class PrivacyLockViewDialogUseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.widget_activity_privacy_lock_view_dialog_use)

        title = intent.getStringExtra("title")
        toolbar.title = title
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        val view = layoutInflater.inflate(R.layout.widget_privacy_lock_view_dialog_input, null)
        val alertDialog = AlertDialog.Builder(this).
                setTitle(R.string.widget_input_code).
                setMessage(R.string.widget_privacy_message).
                setView(view).create()
        val lockView = view.find<PrivacyLockView>(R.id.lock_view)
        alertDialog.show()

        lockView.setOnTextSubmitListener {
            if (it.toString() == "000000") {
                toast(R.string.widget_password_correct)
                alertDialog.dismiss()
            } else {
                toast(getString(R.string.widget_password_error, "000000"))
                lockView.clearText()
            }
        }
    }
}
