package com.ycs.imagedownload;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import org.apache.log4j.Logger;


public class DBUtils {
	private static Logger log = Logger.getLogger(DBUtils.class);

	public static Connection getConnection() {
		ResourceBundle bundle = ResourceBundle.getBundle("common");

		Connection connection = null;

		try {
			Class.forName("oracle.jdbc.OracleDriver");

			connection = DriverManager.getConnection((bundle.getString("URL")), bundle.getString("USERNAME"),
					bundle.getString("PASSWORD"));

			log.debug("[DBUtils][getConnection] connection is -" + connection);

		} catch (SQLException se) {
			log.debug("[DBUtils][getConnection] DB Connection is problem! Exception occurred while getting connection "
					+ se);
		} catch (Exception e) {
			log.debug("[DBUtils][getConnection] DB Connection is problem! Exception occurred while getting connection "
					+ e);
		}
		return connection;
	}

	public static void closeConnection(Connection con) {
		try {
			if (con != null) {
				con.close();

			}
		} catch (SQLException e) {
			log.debug("[closeConnection] DB Connecton Closed Exception " + e.getMessage() + " actual Error. " + e);

		}
	}

	public static void closeStatement(Statement st) {
		try {
			if (st != null) {
				st.close();

			}
		} catch (SQLException e) {
			log.debug("[closeStatement] DB Statement Closed Exception " + e.getMessage() + " actual Error. " + e);

		}
	}

	public static void closeResultset(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();

			}
		} catch (SQLException e) {
			log.debug("[closeResultset] Result Set closed Exception " + e.getMessage() + " actualError. " + e);

		}
	}

	public static void closePrepareStatement(PreparedStatement ps) {
		try {
			if (ps != null) {
				ps.close();

			}
		} catch (Exception e) {
			log.debug("[closePrepareStatement] DB prepare Statement Closed Exception " + e.getMessage()
					+ " actual Error " + e);

		}
	}

	public static void rollback(Connection con) {
		if (con != null) {
			try {
				con.rollback();
				log.debug("[rollback] Rollback Successful");
			} catch (Exception e) {
				log.debug("[closePrepareStatement] DB prepare Statement Rollback Exception " + e.getMessage()
						+ " actual Error " + e);

			}
		}
	}
}