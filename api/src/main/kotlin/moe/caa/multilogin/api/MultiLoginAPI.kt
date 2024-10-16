package moe.caa.multilogin.api

import moe.caa.multilogin.api.eventbus.EventBus
import moe.caa.multilogin.api.manager.*

interface MultiLoginAPI {
    /**
     * 档案管理 API
     */
    val profileManager: ProfileManager

    /**
     * service管理 API
     */
    val serviceManager: ServiceManager

    /**
     * user管理 API
     */
    val userManager: UserManager

    /**
     * 角色信息提供者
     */
    val loginSourceDataManager: LoginSourceDataManager

    /**
     * 事件车
     */
    val eventBus: EventBus
}