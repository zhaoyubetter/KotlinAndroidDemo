package test.com.widget.ui

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.SeekBar
import better.common.base.ToolbarActivity
import kotlinx.android.synthetic.main.widget_activity_palette_image_view.*

import test.com.widget.R
import test.com.widget.widget.PaletteImageView
import test.com.widget.widget.PaletteImageView.OnParseColorListener
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import org.jetbrains.anko.sdk25.coroutines.onClick


class PaletteImageViewActivity : ToolbarActivity(), SeekBar.OnSeekBarChangeListener {

    private var index = 0

    companion object {
        val imageSrc = listOf<Int>(R.mipmap.widget_ganning, R.mipmap.widget_caocao, R.mipmap.widget_burenshi)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.widget_activity_palette_image_view)
//        palette.setBitmap(BitmapFactory.decodeResource(resources, R.mipmap.widget_zilong))
        title = "阴影ImageView"

        seekbar_image_radius.setOnSeekBarChangeListener(this)
        seekbar_shadow_radius.setOnSeekBarChangeListener(this)
        seekbar_x_offset.setOnSeekBarChangeListener(this)
        seekbar_y_offset.setOnSeekBarChangeListener(this)

        palette.setListener(object : OnParseColorListener {
            override fun onComplete(paletteImageView: PaletteImageView) {
                val vibrantColor = paletteImageView.getDarkMutedColor()
                vibrantColor?.let {
                    val colorFilter = PorterDuffColorFilter(it[2], PorterDuff.Mode.SRC_IN)
                    seekbar_image_radius.progressDrawable.colorFilter = colorFilter
                    seekbar_image_radius.thumb.colorFilter = colorFilter

                    seekbar_shadow_radius.progressDrawable.colorFilter = colorFilter
                    seekbar_shadow_radius.thumb.colorFilter = colorFilter

                    seekbar_x_offset.progressDrawable.colorFilter = colorFilter
                    seekbar_x_offset.thumb.colorFilter = colorFilter

                    seekbar_y_offset.progressDrawable.colorFilter = colorFilter
                    seekbar_y_offset.thumb.colorFilter = colorFilter
                }
            }

            override fun onFail() {
            }
        })

        btn_next.onClick {
            if (index == imageSrc.size) {
                index = 0
            }
            palette.setBitmap(BitmapFactory.decodeResource(resources, imageSrc.get(index)))
            index++
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        seekBar?.apply {
            when (seekBar.id) {
                R.id.seekbar_image_radius -> palette.setPaletteRadius(progress.toFloat())
                R.id.seekbar_shadow_radius -> palette.setPaletteShadowRadius(progress.toFloat())
                R.id.seekbar_x_offset -> palette.setPaletteOffsetX(progress.toFloat())
                R.id.seekbar_y_offset -> palette.setPaletteOffsetY(progress.toFloat())
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }
}
