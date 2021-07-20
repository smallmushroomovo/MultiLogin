/*
 * Copyleft (c) 2021 ksqeib,CaaMoe. All rights reserved.
 * @author  ksqeib <ksqeib@dalao.ink> <https://github.com/ksqeib445>
 * @author  CaaMoe <miaolio@qq.com> <https://github.com/CaaMoe>
 * @github  https://github.com/CaaMoe/MultiLogin
 *
 * moe.caa.multilogin.core.auth.AuthCore
 *
 * Use of this source code is governed by the GPLv3 license that can be found via the following link.
 * https://github.com/CaaMoe/MultiLogin/blob/master/LICENSE
 */

package moe.caa.multilogin.core.auth;

import moe.caa.multilogin.core.language.LanguageKeys;
import moe.caa.multilogin.core.logger.LoggerLevel;
import moe.caa.multilogin.core.main.MultiCore;
import moe.caa.multilogin.core.yggdrasil.YggdrasilService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.FutureTask;

public class AuthCore {

    public final MultiCore core;

    public AuthCore(MultiCore core) {
        this.core = core;
    }

    public <T> Object yggAuth(String name, String serverId) throws SQLException {
        return yggAuth(name, serverId, null);
    }

    public <T> AuthResult<T> yggAuth(String name, String serverId, String ip) throws SQLException {
        core.getLogger().log(LoggerLevel.DEBUG, LanguageKeys.DEBUG_LOGIN_START.getMessage(core, name, serverId, ip));

        List<List<YggdrasilService>> order = core.getVerifier().getVeriOrder(name);
        boolean down = false;
        boolean haveService = false;
//        异常 必须保留
        Throwable throwable = null;

        for (List<YggdrasilService> entries : order) {
            if (entries.size() != 0) haveService = true;
            AuthResult<T> result = authWithTasks(entries, name, serverId, ip);
            if (result != null && result.isSuccess()) {
                core.getLogger().log(LoggerLevel.DEBUG, LanguageKeys.DEBUG_LOGIN_END_ALLOW.getMessage(core, name, result.service.getName(), result.service.getPath()));
                return result;
            }
            if (result != null && result.err == AuthFailedEnum.SERVER_DOWN) {
                down = true;
            }
            if (result.throwable != null) throwable = result.throwable;
        }
        AuthFailedEnum failedEnum = down ? AuthFailedEnum.SERVER_DOWN : haveService ? AuthFailedEnum.VALIDATION_FAILED : AuthFailedEnum.NO_SERVICE;
        core.getLogger().log(LoggerLevel.DEBUG, LanguageKeys.DEBUG_LOGIN_END_DISALLOW.getMessage(core, name, failedEnum.name()));
        return new AuthResult<>(failedEnum, throwable);
    }

    private <T> AuthResult<T> authWithTasks(List<YggdrasilService> services, String name, String serverId, String ip) {
        AuthResult<T> getResult = null;
        List<FutureTask<AuthResult<T>>> tasks = new ArrayList<>();
        long endTime = System.currentTimeMillis() + core.servicesTimeOut;
        for (YggdrasilService entry : services) {
            if (entry == null) continue;
            FutureTask<AuthResult<T>> task = new FutureTask<>(new AuthTask<>(entry, name, serverId, ip, core));
            core.plugin.getSchedule().runTaskAsync(task);
            tasks.add(task);
        }

        while (tasks.size() != 0 && endTime > System.currentTimeMillis()) {
            Iterator<FutureTask<AuthResult<T>>> itr = tasks.iterator();
            while (itr.hasNext()) {
                FutureTask<AuthResult<T>> task = itr.next();
                if (!task.isDone()) continue;
                try {
                    getResult = task.get();
                } catch (Exception ignored) {
                } finally {
                    itr.remove();
                }
            }
            if (getResult != null && getResult.isSuccess()) break;
        }
        for (FutureTask<AuthResult<T>> future : tasks) {
            future.cancel(true);
        }
        return getResult;
    }
}
