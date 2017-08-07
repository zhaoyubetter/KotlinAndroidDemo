package better.recordstring

import groovy.transform.Canonical;

/**
 * Created by cz on 2017/7/24.
 */
@Canonical
class RecordConfiguration {
    // 过滤字符串,配置在此处会被过滤不计
    List<String> filterString
    // 配置在此处,会被附加到统计中
    List<String> extrasString
    // 配置过来values的后缀名（如：en-US，默认不配置，则生成default）
    List<String> postfix
    // 生成的目标文件全路径，不配置，则使用默认
    String targetFileFullPath
    // 配置检测目录,如果用户动态设置了build目录
    String buildStringFile
}
