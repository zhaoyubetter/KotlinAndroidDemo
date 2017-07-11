package better.common.communicate

import better.common.communicate.router.IRouterCommunication
import java.lang.RuntimeException

/**
 * 方便引用到IRouterCommunication对象，便于module之间的调用
 * Created by zhaoyu1 on 2017/7/11.
 */
class CommonModule private constructor(val r: IRouterCommunication) : BaseModule(r) {

    companion object {
        private var instance: CommonModule? = null

        fun getIns(r: IRouterCommunication): CommonModule {
            if (instance == null) {
                instance = CommonModule(r)
            }
            return instance!!
        }

        fun getIns(): CommonModule {
            if (instance == null) {
                throw RuntimeException("You must first call getIns(IRouterCommunication)")
            }

            return instance!!
        }
    }

    override fun getTag(): String = "common_module"

    override fun getCommunication(): Any? = null

    override fun onInit() {

    }
}