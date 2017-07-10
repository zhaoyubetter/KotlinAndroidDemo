package test.com.kotlinandroiddemo.ui

import android.app.Dialog
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import test.com.kotlinandroiddemo.R
import test.com.kotlinandroiddemo.widget.PrivacyLockView
import android.view.*
import android.widget.Toast


class PrivacyLockViewDialogUseActivity : AppCompatActivity() {

    var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_lock_view_dialog_use)

        dialog = Dialog(this)
        val d = windowManager.defaultDisplay
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window.setBackgroundDrawableResource(android.R.color.white)
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val content = inflater.inflate(R.layout.privacy_lock_view_dialog_input, null)
        val lockView = content.findViewById(R.id.lock_view) as PrivacyLockView

        content.findViewById(R.id.title).setOnClickListener { dialog!!.dismiss() }

        dialog!!.setContentView(content)
        val attributes = dialog!!.window.attributes
        attributes.width = d.width
        dialog!!.window.attributes = attributes
        dialog!!.window.setGravity(Gravity.BOTTOM)
        dialog!!.show()

        lockView.setOnTextSubmitListener(object : PrivacyLockView.OnTextSubmitListener {
            override fun onSubmit(editable: CharSequence) {
                if (editable.toString() == "000000") {
                    Toast.makeText(applicationContext, "密码正确", Toast.LENGTH_SHORT).show()
                    dialog!!.dismiss()
                } else {
                    Toast.makeText(applicationContext, "密码错误，密码为: 000000", Toast.LENGTH_SHORT).show()
                    lockView.clearText()
                }
            }
        })
    }
}
