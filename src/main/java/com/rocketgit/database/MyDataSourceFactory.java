package com.rocketgit.database;
import java.util.ResourceBundle;

import javax.sql.DataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.rocketgit.Commons;

public class MyDataSourceFactory {
	
	private static ResourceBundle rb;
	
	private static DataSource getMySQLDataSource() {
		MysqlDataSource mysqlDS = null;
		// Introducir la informacion de la base de datos desde el resources
		try {
			rb = ResourceBundle.getBundle("db");
			mysqlDS = new MysqlDataSource();
			mysqlDS.setUrl(rb.getString("MYSQL_DB_URL"));
			mysqlDS.setDatabaseName(rb.getString("MYSQL_DB_NAME"));
			mysqlDS.setUser(rb.getString("MYSQL_DB_USERNAME"));
			mysqlDS.setPassword(rb.getString("MYSQL_DB_PWD"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mysqlDS;
	}
	
	
	public static DataSource getDataSource(String dbType) {
		// seleccionar tipo de driver
		if(dbType != null) {
			if(dbType.equals(Commons.MYSQL)) return getMySQLDataSource();
		}
		
		return null;
	}

}
