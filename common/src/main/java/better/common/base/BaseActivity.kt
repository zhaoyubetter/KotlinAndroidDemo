package better.common.base

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    /**
     * 事件接受方法
     */
    open fun onReceiveEvent(originalIntent: Intent? = null, eventKey: String? = null, eventData: Bundle? = null) {

    }
}
