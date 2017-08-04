package better.recordstring

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by cz on 2017/7/24.
 */
class RecordStringPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        //获取配置
        project.extensions.create("record", RecordConfiguration.class)
        project.task("recordString") << {
            println "Start record string resource!"
            //配置信息
            println("FilterString:${project.record?.filterString}")
            println("ExtrasString:${project.record?.extrasString}")
            println("buildStringFile:${project.record.buildStringFile}")
            println("Postfix:${project.record?.postfix}")
            //检测并合并所有activity
            def stringItems, stringArray
            (stringItems, stringArray) = parserStringValuesFile(project)
            updateStringItems(project, stringItems, stringArray)
            println "Record string success!"
        }
        project.tasks.findByName('build').finalizedBy(project.tasks.findByName('recordString'))
    }

    /**
     * 读取新生成的values.xml 资源,获取strings/string-array 信息
     * @param project
     * @return
     */
    def parserStringValuesFile(project) {
        // build 文件路径
        def path = project.record.buildStringFile
        def valuesFile = path ? new File(path) : new File(project.buildDir, "/intermediates/res/merged/debug/values/values.xml")
        def root = new XmlParser().parse(valuesFile)
        def stringItems = [:]
        root.string.each {
            def translatableValue = it.attributes()["translatable"]
            //检测不需要国际化数组配置
            if (null == translatableValue || Boolean.valueOf(translatableValue)) {
                def item = it.attributes()["name"]
                stringItems << [(item): it.text()]
            }
        }
        println("string size:${stringItems.size()}")
        def arrayArrayItems = [:]
        root."string-array".each {
            def translatableValue = it.attributes()["translatable"]
            //检测不需要国际化数组配置
            if (null == translatableValue || Boolean.valueOf(translatableValue)) {
                def arrayItems = []
                it.value().each {
                    def value = it.text()
                    //解出引用嵌引用的
                    def matcher = value =~ /@string\/(.+)/
                    if (matcher) {
                        arrayItems << stringItems[matcher[0][1]]
                    } else {
                        arrayItems << value
                    }
                }
                arrayArrayItems << [(it.attributes()["name"]): (arrayItems)]
            }
        }
        println("array size:${arrayArrayItems.size()}")
        [stringItems, arrayArrayItems]
    }

    def updateStringItems(project, stringItems, stringArray) {
        //检测assets目录配置文件
        def assetsFile = new File(project.name, "/src/main/assets")
        if (!assetsFile.exists()) {
            assetsFile.mkdir();
        }
        def configFile = new File(assetsFile, "strings.xml")
        def oldItems = [:]
        def oldArrayItems = [:]
        if (configFile.exists()) {
            def root = new XmlParser().parse(configFile)
            //1:分析己配置string条目
            root.string.each {
                oldItems << [(it.attributes()["name"]): it.text()]
            }
            //2:分析己配置好的string-array
            root."string-array".each {
                def arrayItems = []
                it.value().each { arrayItems << it.text() }
                oldArrayItems << [(it.attributes()["name"]): arrayItems]
            }
        }
        //合并所有String
        def newStringItems = new HashMap()
        stringItems.each { newStringItems << [(it.key): null] }
        //1:取交集,过滤出所有nenItems不存在的,不存在的则为无效条目,可能为移除
        oldItems.keySet().retainAll(newStringItems.keySet())
        //2:再移出所有stringItems之中的oldString并集,这一步是解出所有未配置的条目
        newStringItems -= oldItems
        //3:合并
        newStringItems += oldItems
        //4:移除需要过滤的条目
        project.record?.filterString.each { newStringItems.remove(it) }
        //5:加入附加的条目
        project.record?.extrasString.each { newStringItems << [(it): null] }

        //合并所有StringArray
        def newStringArray = new HashMap(stringArray)
        //1:取交集,过滤出所有nenItems不存在的,不存在的则为无效条目,可能为移除
        oldArrayItems.keySet().retainAll(newStringArray.keySet())
        //2:再移出所有stringItems之中的oldString并集,这一步是解出所有未配置的条目
        newStringArray -= oldArrayItems
        //3:合并
        newStringArray += oldArrayItems
        //4:移除需要过滤的条目
        project.record?.filterString.each { newStringArray.remove(it) }
        //5:加入附加的条目
        project.record?.extrasString.each { newStringArray << [(it): null] }

        // 6.生成xml
        def fileWriter = new FileWriter(configFile)
        def xml = new groovy.xml.MarkupBuilder(fileWriter)
        xml.resources([ct: new Date().toLocaleString()]) {
            //字符串,cn为中文映射字符,用以对照翻译作用
            newStringItems?.each {
                string(it.value ?: "", [name: it.key, cn: stringItems[it.key]])
            }
            //数组
            newStringArray.each { array ->
                "string-array"(name: array.key) {
                    array.value.each {
                        item(it)
                    }
                }
            }
        }
    }

}
