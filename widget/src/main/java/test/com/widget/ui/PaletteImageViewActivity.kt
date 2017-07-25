package test.com.widget.ui

import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.widget_activity_palette_image_view.*

import test.com.widget.R

class PaletteImageViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.widget_activity_palette_image_view)
        palette.setBitmap(BitmapFactory.decodeResource(resources, R.mipmap.widget_zilong))
    }
}
