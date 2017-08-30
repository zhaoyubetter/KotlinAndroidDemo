package test.com.widget.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.widget__activity_nestd_scrolling_test.*
import org.jetbrains.anko.sdk25.coroutines.onClick

import test.com.widget.R

class NestdScrollingTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.widget__activity_nestd_scrolling_test)
        btn.onClick {
//            it?.scrollBy(0, -100)     // 在自己的显示区域中动
            it?.translationX = 100f     // 在父容器中動
        }
    }
}
