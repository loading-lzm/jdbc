package com.connection;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionTest {

    //方式一
    public void testConnection1() throws Exception {
        try {
            //1.提供java.sql.Driver接口实现类的对象
            Driver driver = new com.mysql.jdbc.Driver();
            //2.提供url，指明具体操作的数据
            /**
             * url:路径
             * jdbc:mysql协议
             * localhost:默认mysql端口号
             * test:test数据库
             */
            String url = "jdbc:mysql://localhost:3306/test";
            //3.提供Properties的对象，指明用户名和密码
            Properties info = new Properties();
            info.setProperty("user", "root");
            info.setProperty("password", "123456");
            //4.调用driver的connect()，获取连接
            Connection conn = driver.connect(url, info);
            System.out.println(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //方式二
    //没有使用第三方的api,相对第一种方式有着更好的移植性
    public void testConnection2() throws Exception {
        try {
            //1.获取Driver实现类对象,使用反射方式
            Class clazz = Class.forName("com.mysql.jdbc.Driver");
            Driver driver = (Driver) clazz.newInstance();

            //2.提供要连接的数据库
            String url = "jdbc:mysql://localhost:3306/test";

            //3.提供用户名和密码
            Properties info = new Properties();
            info.setProperty("user", "root");
            info.setProperty("password", "123456");

            //4.获取连接
            Connection conn = driver.connect(url, info);
            System.out.println(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //方式三
    //使用DriverManager替换Driver
    public void testConnection3() throws Exception {
        try {
            //1.获取Driver实现类的对象
            Class clazz = Class.forName("com.mysql.jdbc.Driver");
            Driver driver = (Driver) clazz.newInstance();

            //2.提供另外三个连接的基本信息
            String url = "jdbc:mysql://localhost:3306/test";
            String user = "root";
            String password = "123456";

            //3.注册成功
            DriverManager.registerDriver(driver);

            //4.注册连接
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //方式四,可以知识加载驱动,不用显示注册驱动过了.
    public void testConnection4() throws Exception {
        try {
            //提供另外三个连接的基本信息
            String url = "jdbc:mysql://localhost:3306/test";
            String user = "root";
            String password = "123456";

            //加载Driver
            Class.forName("com.mysql.jdbc.Driver");
            //Driver driver = (Driver) clazz.newInstance();

            //3.注册驱动
            //DriverManager.registerDriver(driver);
            /*
            可以注释掉上述代码的原因，是因为在mysql的Driver类中声明有：
            static {
                try {
                    DriverManager.registerDriver(new Driver());
                } catch (SQLException var1) {
                    throw new RuntimeException("Can't register driver!");
                }
            }
            */

            //3.获取连接
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //方式五 最终方案
    //①实现了代码和数据的分离，如果需要修改配置信息，直接在配置文件中修改，不需要深入代码
    //②如果修改了 配置信息，省去重新编译的过程。
    public void testConnection5() throws Exception{
        //加载配置文件
        //运用类的加载器 构造输入流对象
        InputStream is = ConnectionTest.class.getClassLoader().getResourceAsStream("jdbc.properties");
        Properties pros = new Properties();
        pros.load(is);

        //读取配置信息
        String user = pros.getProperty("user");
        String password = pros.getProperty("password");
        String url = pros.getProperty("url");
        String driverClass = pros.getProperty("driverClass");

        //加载驱动
        Class.forName(driverClass);

        //获取连接
        Connection conn = DriverManager.getConnection(url,user,password);
        System.out.println();
    }

    public static void main(String[] args) throws Exception {
        ConnectionTest connectionTest = new ConnectionTest();
        connectionTest.testConnection1();
        connectionTest.testConnection2();
        connectionTest.testConnection3();
        connectionTest.testConnection4();
        connectionTest.testConnection5();
    }
}