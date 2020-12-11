package org.apache.dolphinscheduler.dao.datasource;

import org.apache.dolphinscheduler.common.Constants;
import org.apache.dolphinscheduler.common.enums.DbType;
import org.apache.dolphinscheduler.common.utils.StringUtils;

/**
 * @Author: GDZ
 * @Date: 2020/6/12 8:37
 */
public class OdpsDataSource extends BaseDataSource {


    @Override
    public String driverClassSelector() {
        return Constants.COM_ODPS_JDBC_DRIVER;
    }

    @Override
    public DbType dbTypeSelector() {
        return DbType.ODPS;
    }


    @Override
    public String getDatabase() {
        String project=database;
        return project;
    }

    @Override
    public String getJdbcUrl() {
        // "jdbc:odps:https://service.odps.aliyun.com/api?project=jdbc_test&charset=UTF-8"
        //jdbc:odps:http://service.cn-hangzhou.maxcompute.aliyun.com/api?project=odps_create_table
        String jdbcUrl = getAddress();
        jdbcUrl += "?project=" + getDatabase();
        if (StringUtils.isNotEmpty(getOther())) {
            jdbcUrl += "&" + getOther();
        }

        return jdbcUrl;
    }


    @Override
    public String getUser() {
        String accessId=user;
        return accessId;
    }

    @Override
    public String getPassword() {
        String accessKey=password;
        return accessKey;
    }
}
