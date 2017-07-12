package test.com.me

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by zhaoyu1 on 2017/7/12.
 */
class MEFragment : Fragment() {

    private var toolbar: ActionBar? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.me__fragment_main, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = (activity as AppCompatActivity).supportActionBar
        toolbar?.setTitle(R.string.me__title)
        toolbar?.setSubtitle(R.string.me__subtitle)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        toolbar?.setTitle(R.string.me__title)
        toolbar?.setSubtitle(R.string.me__subtitle)
    }
}