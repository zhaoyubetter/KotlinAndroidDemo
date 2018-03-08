package com.better.module.home.anim

import android.os.Bundle
import better.common.base.ToolbarActivity
import com.better.module.home.R

/**
 * scale anim
 * Created by zhaoyu on 2018/3/8.
 * 参考：http://blog.csdn.net/harvic880925/article/details/39996643
 */
class TweenAnimActivity : ToolbarActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_anim_activity_scale_anim)
    }
}