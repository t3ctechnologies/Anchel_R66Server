package com.t3c.anchel;

import java.io.File;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;

import org.springframework.web.context.ContextLoaderListener;

import com.t3c.anchel.openr66.protocol.exception.OpenR66ProtocolPacketException;
import com.t3c.anchel.openr66.server.R66Server;

public class AnchelR66Server extends ContextLoaderListener {

	public void contextInitialized(ServletContextEvent arg0) {

		try {
			System.out.println("Anchel R66Server DB is initiating...");
			new R66ServerDBInitializer().initdb();
			System.out.println("Anchel R66Server is starting...");
			File configFile = new File(this.getClass().getClassLoader().getResource("config-serverA.xml").getFile());
			String path = null;
			if (configFile.exists()) {
				path = configFile.toString();
			}
			String[] waarpconfig = { path };
			System.out.println("Anchel R66 server databse is initiating");
			R66Server.initR66Server(waarpconfig);
		} catch (OpenR66ProtocolPacketException e) {
			e.printStackTrace();
			System.out.println("Anchel R66 server is not started " + e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Anchel R66 database connection is not closed " + e.getMessage());
		}
	}

	public void contextDestroyed(ServletContextEvent arg0) {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();
			if (driver.getClass().getClassLoader() == cl) {
				try {
					System.out.println("Deregistering JDBC driver " + driver);
					DriverManager.deregisterDriver(driver);
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("Error deregistering JDBC driver " + driver);
				}
			} else {
				System.out.println(
						"Not deregistering JDBC driver {} as it does not belong to this webapp's ClassLoader" + driver);
			}
		}
		System.out.println("Anchel R66 server is terminated");
	}
}
