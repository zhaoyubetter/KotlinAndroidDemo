package com.better.module.home

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

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
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            toolbar?.setTitle(R.string.home__subtitle)
            toolbar?.setSubtitle(R.string.home__subtitle)
        }
    }
}