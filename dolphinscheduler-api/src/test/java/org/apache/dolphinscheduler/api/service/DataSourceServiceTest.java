/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dolphinscheduler.api.service;

import com.alibaba.fastjson.JSON;
import org.apache.dolphinscheduler.api.enums.Status;
import org.apache.dolphinscheduler.api.utils.Result;
import org.apache.dolphinscheduler.common.Constants;
import org.apache.dolphinscheduler.common.enums.DbConnectType;
import org.apache.dolphinscheduler.common.enums.DbType;
import org.apache.dolphinscheduler.common.enums.UserType;
import org.apache.dolphinscheduler.dao.datasource.BaseDataSource;
import org.apache.dolphinscheduler.dao.datasource.OdpsDataSource;
import org.apache.dolphinscheduler.dao.entity.DataSource;
import org.apache.dolphinscheduler.dao.entity.User;
import org.apache.dolphinscheduler.dao.mapper.DataSourceMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"sun.security.*", "javax.net.*"})
public class DataSourceServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceServiceTest.class);

    @InjectMocks
    private DataSourceService dataSourceService;
    @Mock
    private DataSourceMapper dataSourceMapper;

    @Test
    public void queryDataSourceListTest(){

        User loginUser = new User();
        loginUser.setUserType(UserType.GENERAL_USER);
        Map<String, Object> map = dataSourceService.queryDataSourceList(loginUser, DbType.MYSQL.ordinal());
        Assert.assertEquals(Status.SUCCESS, map.get(Constants.STATUS));
    }

    @Test
    public void verifyDataSourceNameTest(){
        User loginUser = new User();
        loginUser.setUserType(UserType.GENERAL_USER);
        String dataSourceName = "dataSource1";
        PowerMockito.when(dataSourceMapper.queryDataSourceByName(dataSourceName)).thenReturn(getDataSourceList());
        Result result = dataSourceService.verifyDataSourceName(loginUser, dataSourceName);
        Assert.assertEquals(Status.DATASOURCE_EXIST.getMsg(),result.getMsg());
    }

    @Test
    public void queryDataSourceTest(){
        PowerMockito.when(dataSourceMapper.selectById(Mockito.anyInt())).thenReturn(null);
        Map<String, Object> result = dataSourceService.queryDataSource(Mockito.anyInt());
        Assert.assertEquals(((Status)result.get(Constants.STATUS)).getCode(),Status.RESOURCE_NOT_EXIST.getCode());

        PowerMockito.when(dataSourceMapper.selectById(Mockito.anyInt())).thenReturn(getOracleDataSource());
        result = dataSourceService.queryDataSource(Mockito.anyInt());
        Assert.assertEquals(((Status)result.get(Constants.STATUS)).getCode(),Status.SUCCESS.getCode());
    }


    private List<DataSource> getDataSourceList(){

        List<DataSource> dataSources =  new ArrayList<>();
        dataSources.add(getOracleDataSource());
        return dataSources;
    }


    public DataSource getOracleDataSource(){
        DataSource dataSource = new DataSource();
        dataSource.setName("test");
        dataSource.setNote("Note");
        dataSource.setType(DbType.ORACLE);
        dataSource.setConnectionParams("{\"connectType\":\"ORACLE_SID\",\"address\":\"jdbc:oracle:thin:@192.168.xx.xx:49161\",\"database\":\"XE\",\"jdbcUrl\":\"jdbc:oracle:thin:@192.168.xx.xx:49161/XE\",\"user\":\"system\",\"password\":\"oracle\"}");

        return dataSource;
    }

    @Test
    public void getOdpsConn() throws SQLException {
        DataSourceService dataSourceService = new DataSourceService();
        String name="odps_tets";
        String note="";
        DbType dbType=DbType.ODPS;
        DbConnectType connectType = DbConnectType.ORACLE_SID;
        User loginUser=new User();


        String parameter = dataSourceService.buildParameter(
                name, "2", dbType, "http://service.cn-hangzhou.maxcompute.aliyun.com"
                , "/api", "odps_create_table", "1", "LTAI4FyFwumzxEoN3TK95hWc",
                "KvpnnEX80DY6TGtDajfsWkg46cO2Mb",connectType , "");
        BaseDataSource dataSource= JSON.parseObject(parameter, OdpsDataSource.class);
        Connection connection = DriverManager.getConnection(dataSource.getJdbcUrl(), dataSource.getUser(), dataSource.getPassword());


    }
}