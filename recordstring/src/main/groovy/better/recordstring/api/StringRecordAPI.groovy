package better.recordstring.api

import better.recordstring.RecordConfiguration

/**
 * 接口
 */
interface StringRecordAPI {
    /**
     *
     * @param configuration
     * @param appBuildPath app 构建根目录
     */
    void create(RecordConfiguration configuration, String appBuildPath)
}