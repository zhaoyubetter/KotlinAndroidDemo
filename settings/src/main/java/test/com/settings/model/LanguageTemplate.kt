package test.com.settings.model

import android.app.Activity

/**
 *
 * Created by zhaoyu1 on 2017/7/14.
 */


class LanguageTemplate {
    companion object {
        val items = mutableListOf<LanguageModel>()

        /**
         * 添加一项
         */
        fun item(closure: LanguageModel.() -> Unit) {
            items.add(LanguageModel().apply(closure))
        }

        init {
            item {
                langName = "中文（简体）"
                langValue = "zh"
                codeCountry = "zh"
                codeLanguage = "CN"
            }

            item {
                langName = "中文-香港（繁體）"
                langValue = "rHK"
                codeCountry = "zh"
                codeLanguage = "TW"
            }

            item {
                langName = "中文-台灣（繁體）"
                langValue = "rTW"
                codeCountry = "zh"
                codeLanguage = "TW"
            }

            item {
                langName = "English"
                langValue = "en"
                codeCountry = "en"
                codeLanguage = "US"
            }
        }
    }
}