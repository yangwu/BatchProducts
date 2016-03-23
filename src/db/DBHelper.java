package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import constant.BatchConfig;

public class DBHelper {

	public static final String url = "jdbc:mysql://localhost:3306/wish?characterEncoding=utf-8";
	public static final String name = "com.mysql.jdbc.Driver";
	public static final String user = "root";
	public static final String password = "yangwu";

	public Connection conn = null;
	public PreparedStatement pst = null;

	public DBHelper() {
		try {
			Class.forName(name);// 指定连接类型
			conn = DriverManager.getConnection(url, user, password);// 获取连接
			// pst = conn.prepareStatement(sql);//准备执行语句
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean insertProduct(Map<String, String> product) {
		int count = 0;
		String insertSql = "";
		System.out.println("insertSql:" + insertSql);
		try {
			pst = conn.prepareStatement(insertSql);
			count = pst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (count > 0);
	}

	public boolean insertProductSource(Map<String, String> product) {
		int result = 0;
		String insertSourceSQL = "insert into productinfo(accountid, parent_sku,source_url) values (?,?,?)";
		try {
			pst = conn.prepareStatement(insertSourceSQL);
			pst.setInt(1, Integer.parseInt(product.get(BatchConfig.ACCOUNTID)));
			pst.setString(2, product.get(BatchConfig.PARENT_SKU));
			pst.setString(3, product.get(BatchConfig.SOURCEURL));
			result = pst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("insertProductsource failed " + e.getMessage());
		}

		return result > 0;
	}
}
