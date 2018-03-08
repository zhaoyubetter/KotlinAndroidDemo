package com.better.module.home

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.home_layout_fragment_home.*
import lib.basenet.okhttp.OkHttpRequest
import lib.basenet.request.AbsRequestCallBack
import lib.basenet.response.Response
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.toast

/**
 * home fragment
 * Created by zhaoyu1 on 2017/7/11.
 */
class HomeFragment : Fragment() {

    private var toolbar: ActionBar? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.home_layout_fragment_home, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = (activity as AppCompatActivity).supportActionBar
        toolbar?.setTitle(R.string.home__title)
        toolbar?.setSubtitle(R.string.home__subtitle)

        btn_test.onClick {
            OkHttpRequest.Builder().url("http://www.baidu.com").callback(object : AbsRequestCallBack<String>() {
                override fun onSuccess(response: Response<String>) {
                    toast(response.message)
                }

                override fun onFailure(e: Throwable) {
                    toast(e.toString())
                }
            }).build().request()
        }

        animal.onClick {

        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            toolbar?.setTitle(R.string.home__title)
            toolbar?.setSubtitle(R.string.home__subtitle)
        }
    }
}