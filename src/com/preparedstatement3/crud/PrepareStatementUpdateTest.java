package com.preparedstatement3.crud;

import com.atguigu3.bean.Customer;
import com.atguigu3.util.JDBCUtils;
import javafx.scene.chart.ScatterChart;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;


public class PrepareStatementUpdateTest {

    @Test
    public void TestCommonUpdate() {
        String sql = "update customers set name = ? where id = ?";
        //String sql = "delete from customers where id = ?";
        //update(sql, "12");
        update(sql, "周靖凯", 4);
    }

    @Test
    public void testQueryForCustomers() {
        String sql = "select id,name,birth,email from customers where id = ?";
        Customer customer = queryForCustomer(sql, 13);
        System.out.println(customer);
    }

    @Test
    //改写数据库中的数据
    public void testUpdate() {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            //1.获取数据库连接
            conn = JDBCUtils.getConnection();
            //2.预编译sql语句,返回PrepareStatement实例
            String sql = "update customers set name = ? where id = ?";
            ps = conn.prepareStatement(sql);
            //3.填充占位符
            ps.setObject(1, "周靖凯");
            ps.setObject(2, 18);
            //4.执行
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps);
        }
    }

    //通用的增、删、改操作（体现一：增、删、改 ； 体现二：针对于不同的表）
    public void update(String sql, Object... args) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            //1.获取数据库的连接
            conn = JDBCUtils.getConnection();
            //2.获取PreparedStatement的实例 (或：预编译sql语句)
            ps = conn.prepareStatement(sql);
            //3.填充占位符
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            ps.execute();
            //4.执行sql语句
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //5.关闭资源
            JDBCUtils.closeResource(conn, ps);
        }
    }

    /**
     * 针对于customers表的查询操作
     */
    public Customer queryForCustomer(String sql, Object... args) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            //获取连接
            conn = JDBCUtils.getConnection();
            //预编译SQL语句
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                //注意:setObject方法从1开始
                ps.setObject(i + 1, args[i]);
            }
            //返回结果集
            rs = ps.executeQuery();
            //获取结果集的元数据 String name = "Tom"; 可以理解为Tom的两个元数据
            ResultSetMetaData rsmd = rs.getMetaData();
            //通过ResultSetMetaDate获取结果集中的列数
            int columnCount = rsmd.getColumnCount();
            if (rs.next()) {
                Customer cust = new Customer();
                //处理一行数据中的每一个列
                for (int i = 0; i < columnCount; i++) {
                    //获取列值
                    Object columValue = rs.getObject(i + 1);
                    //获取每个列的列名,使用类的属性名充当
                    String columName = rsmd.getColumnLabel(i + 1);
                    //给cust指定的某个属性赋值为 columValue 使用反射
                    Field field = Customer.class.getDeclaredField(columName);
                    field.setAccessible(true);
                    field.set(cust, columValue);
                }
                return cust;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps, rs);
        }
        return null;
    }

    /**
     * 针对不同表查询:返回一个对象
     *
     * @param clazz
     * @param sql
     * @param args
     * @param <T>
     * @return
     */
    // 通用的针对于不同表的查询:返回一个对象 (version 1.0)
    public <T> T getInstance(Class<T> clazz, String sql, Object... args) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            // 1.获取数据库连接
            conn = JDBCUtils.getConnection();
            // 2.预编译sql语句，得到PreparedStatement对象
            ps = conn.prepareStatement(sql);
            // 3.填充占位符
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            // 4.执行executeQuery(),得到结果集：ResultSet
            rs = ps.executeQuery();
            // 5.得到结果集的元数据：ResultSetMetaData
            ResultSetMetaData rsmd = rs.getMetaData();
            // 6.1通过ResultSetMetaData得到columnCount,columnLabel；通过ResultSet得到列值
            int columnCount = rsmd.getColumnCount();
            if (rs.next()) {
                T t = clazz.newInstance();
                for (int i = 0; i < columnCount; i++) {// 遍历每一个列
                    // 获取列值
                    Object columnVal = rs.getObject(i + 1);
                    // 获取列的别名:列的别名，使用类的属性名充当
                    String columnLabel = rsmd.getColumnLabel(i + 1);
                    // 6.2使用反射，给对象的相应属性赋值
                    Field field = clazz.getDeclaredField(columnLabel);
                    field.setAccessible(true);
                    field.set(t, columnVal);
                }
                return t;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 7.关闭资源
            JDBCUtils.closeResource(conn, ps, rs);
        }
        return null;
    }
}


