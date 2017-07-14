package test.com.settings.model

/**
 * Created by zhaoyu1 on 2017/7/14.
 */
data class LanguageModel(var langName: String?, var langValue: String?, var codeCountry: String?, var codeLanguage: String?) {
    constructor() : this(null, null, null, null)
}