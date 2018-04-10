package com.github.springcloud.commons.ojdbc;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.springcloud.commons.util.MapUtil;

public class OracleUtil {
	private static Logger logger = LoggerFactory.getLogger(OracleUtil.class);
	public static String ojdbcConfigPath = "config/ojdbc_cfg.properties";

	public static Connection getConnection(Properties prop) {
		Connection conn = null;
		try {
			Class.forName(MapUtil.getStringValueOfProperties(prop, "driverClassName"));
			String url = MapUtil.getStringValueOfProperties(prop, "url");
			String username = MapUtil.getStringValueOfProperties(prop, "db.username");
			String password = MapUtil.getStringValueOfProperties(prop, "db.password");
			conn = DriverManager.getConnection(url, username, password);
		} catch (ClassNotFoundException e) {
			logger.info("加载数据库驱动失败", e);
			e.printStackTrace();
		} catch (SQLException e) {
			logger.info("获取数据库连接失败", e);
			e.printStackTrace();
		}
		return conn;
	}

	public static void release(Connection con, Statement statement, ResultSet rs) {
		if (null != rs) {
			try {
				rs.close();
			} catch (SQLException e) {
				logger.info("关闭ResultSet失败", e);
				e.printStackTrace();
			} finally {
				rs = null;
			}
		}
		if (null != statement) {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.info("关闭Statement对象失败", e);
				e.printStackTrace();
			} finally {
				statement = null;
			}
		}
		if (null != con) {
			try {
				con.close();
			} catch (SQLException e) {
				logger.info("关闭数据库连接失败", e);
				e.printStackTrace();
			} finally {
				con = null;
			}
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		Properties ojdbcConfig = MapUtil.loadFileToProperties(ojdbcConfigPath);
		Connection connection = OracleUtil.getConnection(ojdbcConfig);
		Statement stm = null;
		ResultSet rs = null;
		try {
			stm = connection.createStatement();
			String sql = "SELECT * FROM TB_USER";
			rs = stm.executeQuery(sql);
			while (rs.next()) {
				String name = rs.getString("NAME");
				logger.info("NAME=" + name + "--------------------");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			OracleUtil.release(connection, stm, rs);
		}
	}

}
