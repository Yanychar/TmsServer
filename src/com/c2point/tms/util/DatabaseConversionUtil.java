package com.c2point.tms.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handles database integrity during updates
 */
public class DatabaseConversionUtil {
	private static Logger logger = LogManager.getLogger( DatabaseConversionUtil.class.getName());

	/**
	 * @param schemaFileLocation
	 *            Example "/your/tms/config/tms_init_schema.sql"
	 * @param dbURL
	 *            example "jdbc:postgresql:testtms"
	 * @param username
	 *            example "testtms"
	 * @param password
	 *            example "testtms"
	 */
	public static void loadSchema(String schemaFileLocation, String dbURL, String username, String password) {
		try {
			Class.forName("org.postgresql.Driver"); // Or any other driver
		} catch (Exception x) {
			x.printStackTrace();
		}
		Connection con = null;
		try {
			con = DriverManager.getConnection(dbURL, username, password);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		StringBuffer schema = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new FileReader(schemaFileLocation));
			String line = null;

			while ((line = br.readLine()) != null) {
				if (line.length() > 0 && !line.startsWith("--")) {
					schema.append(line);
				}
			}
			br.close();
			con.setAutoCommit(true);
			Statement s = con.createStatement();
			String[] statements = schema.toString().split(";");
			for (String statement : statements) {
				s.executeUpdate(statement);
			}
			// con2.commit();
			s.close();
//			con.close();
		} catch (FileNotFoundException e ) {
			logger.error( e );
			throw new RuntimeException( e );
		} catch (IOException e) {
			logger.error( e );
			throw new RuntimeException(e);
		} catch (SQLException e) {
			logger.error( e );
			throw new RuntimeException(e);
		} finally {
			try {
				if (!con.isClosed()) {
					con.close();
				}
			} catch (SQLException e) {
				// NOP
			}
		}
	}
}
