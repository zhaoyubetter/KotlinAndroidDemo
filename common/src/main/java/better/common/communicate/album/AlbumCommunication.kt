package better.common.communicate.album

import android.content.Context

/**
 * 相册模块 通信接口 定义
 */
interface IAlbumCommunication {

    fun startAlbum(context: Context, config: AlbumConfig)

    /**
     * 更新选中的图片
     * @param dst 要被替换掉的图片全路径
     * @param src 替换来源
     */
    fun updateSelectedImage(dst: String, src: String)
}

/**
 * 打开 Album的配置
 */
class AlbumConfig {
    var maxCount: Int = 9
        set(value) {
            if (value >= 0) field = value
        }

    lateinit var listener: AlbumListener
}

/**
 * 选择图片对外提供的回调
 */
interface AlbumListener {

    /**
     * 确认选择时
     * @param selected 所选图片路径
     */
    fun onSelected(selected: List<String>)

    /**
     * 选满时，超过maxCount的回调
     * @param selected 所选图片路径
     * @param newSelected 新选择的图片
     */
    fun onFull(selected: List<String>, newSelected: String)
}
