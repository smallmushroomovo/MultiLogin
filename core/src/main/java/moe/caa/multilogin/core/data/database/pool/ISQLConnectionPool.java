/*
 * Copyleft (c) 2021 ksqeib,CaaMoe. All rights reserved.
 * @author  ksqeib <ksqeib@dalao.ink> <https://github.com/ksqeib445>
 * @author  CaaMoe <miaolio@qq.com> <https://github.com/CaaMoe>
 * @github  https://github.com/CaaMoe/MultiLogin
 *
 * moe.caa.multilogin.core.data.database.pool.ISQLConnectionPool
 *
 * Use of this source code is governed by the GPLv3 license that can be found via the following link.
 * https://github.com/CaaMoe/MultiLogin/blob/master/LICENSE
 */

package moe.caa.multilogin.core.data.database.pool;

import java.sql.Connection;
import java.sql.SQLException;

public interface ISQLConnectionPool {
    Connection getConnection() throws SQLException;

    void close();
}