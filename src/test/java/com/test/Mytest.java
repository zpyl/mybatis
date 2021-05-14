package com.test;

import com.zpyl.mapper.UserMapper;
import com.zpyl.pojo.User;
import org.apache.ibatis.executor.*;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.*;
import org.apache.ibatis.session.defaults.DefaultSqlSession;
import org.apache.ibatis.transaction.jdbc.JdbcTransaction;
import org.hsqldb.Row;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mytest {

  Configuration configuration;
  JdbcTransaction transaction;

  @Before
  public void init() throws Exception {
    InputStream stream =  Resources.getResourceAsStream("SqlConfig.xml");
    SqlSessionFactory build = new SqlSessionFactoryBuilder().build(stream);

    configuration = build.getConfiguration();
    Connection connection = DriverManager.getConnection("jdbc:mysql:///mybatis", "root", "123456");
    transaction = new JdbcTransaction(connection);

  }

  /**
   * 简单执行器
   */
  @Test
  public void simpleExecutor() throws SQLException {
    SimpleExecutor simpleExecutor = new SimpleExecutor(configuration, transaction);
    MappedStatement ms=configuration.getMappedStatement("com.zpyl.mapper.UserMapper.findById");
    //sql声明映射 参数 行范围 结果处理器 动态sql语句
    List<Object> list = simpleExecutor.doQuery(ms, 41, RowBounds.DEFAULT, SimpleExecutor.NO_RESULT_HANDLER, ms.getBoundSql(41));
    List<Object> list1 = simpleExecutor.doQuery(ms, 41, RowBounds.DEFAULT, SimpleExecutor.NO_RESULT_HANDLER, ms.getBoundSql(41));
    System.out.println(list.get(0));
  }

  /**
   *可重用执行器
   */
   @Test
   public void reuseExecutor() throws SQLException {
     ReuseExecutor reuseExecutor = new ReuseExecutor(configuration, transaction);
     MappedStatement ms=configuration.getMappedStatement("com.zpyl.mapper.UserMapper.findById");
     //sql声明映射 参数 行范围 结果处理器 动态sql语句
     List<Object> list = reuseExecutor.doQuery(ms, 41, RowBounds.DEFAULT, SimpleExecutor.NO_RESULT_HANDLER, ms.getBoundSql(41));
     List<Object> list1 = reuseExecutor.doQuery(ms, 41, RowBounds.DEFAULT, SimpleExecutor.NO_RESULT_HANDLER, ms.getBoundSql(41));
     System.out.println(list.get(0));
   }


  /**
   * 批处理执行器
   * 只针对于查询
   */
  @Test
  public void batchExecutor() throws SQLException {
    BatchExecutor batchExecutor = new BatchExecutor(configuration, transaction);
    MappedStatement ms=configuration.getMappedStatement("com.zpyl.mapper.UserMapper.setName");
    Map<Object, Object> map = new HashMap<>();
    map.put("arg0","42");
    map.put("arg1","拉拉队");
    batchExecutor.doUpdate(ms,map);
    map.put("arg0","42");
    map.put("arg1","车夫");
    batchExecutor.doUpdate(ms,map);

    User user=new User();
    user.setId(41);
    user.setUsername("嘻嘻");
    user.setBirthday(new Date());
    user.setSex('女');
    user.setAddress("安徽阜阳");
    ms=configuration.getMappedStatement("com.zpyl.mapper.UserMapper.update");
    batchExecutor.doUpdate(ms,user);
    user.setUsername("呵呵");
    batchExecutor.doUpdate(ms,user);
    //刷新
    batchExecutor.doFlushStatements(false);
  }

  /**
   * 一级缓存执行器
   * baseExecutor
   */
  @Test
  public void baseExecutor() throws SQLException {
    BaseExecutor baseExecutor = new BatchExecutor(configuration, transaction);
    MappedStatement ms=configuration.getMappedStatement("com.zpyl.mapper.UserMapper.findById");
    List<Object> list = baseExecutor.query(ms,41, RowBounds.DEFAULT,Executor.NO_RESULT_HANDLER);
    List<Object> list1 = baseExecutor.query(ms, 41, RowBounds.DEFAULT, SimpleExecutor.NO_RESULT_HANDLER);
    System.out.println(list.get(0));
  }

  /**
   * 二级缓存执行器
   * CachingExecutor
   */
  @Test
  public void cacheExecutor() throws SQLException {
    CachingExecutor cachingExecutor = new CachingExecutor((BaseExecutor)new ReuseExecutor(configuration,transaction));
    MappedStatement ms=configuration.getMappedStatement("com.zpyl.mapper.UserMapper.findById");
    List<Object> list = cachingExecutor.query(ms,41, RowBounds.DEFAULT,Executor.NO_RESULT_HANDLER);
    List<Object> list1 = cachingExecutor.query(ms, 41, RowBounds.DEFAULT, SimpleExecutor.NO_RESULT_HANDLER);
    System.out.println(list.get(0));
  }

  @Test
  public void test() throws IOException {
    //读取配置文件
    InputStream resource = Resources.getResourceAsStream("SqlConfig.xml");
    //创建 SqlSessionFactory 的构建者对象
    SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
    //使用构建者创建工厂对象 SqlSessionFactory
    SqlSessionFactory sessionFactory = sqlSessionFactoryBuilder.build(resource);
    //使用 SqlSessionFactory 生产 SqlSession 对象
    SqlSession session = sessionFactory.openSession();
    //使用 SqlSession 创建 dao 接口的代理对象
    UserMapper mapper = session.getMapper(UserMapper.class);
    //使用代理对象执行查询所有方法
    mapper.findAll().forEach(user -> {
      System.out.println(user);
    });
    //释放资源
    session.close();
    resource.close();
  }

}
