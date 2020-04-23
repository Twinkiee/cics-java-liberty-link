package com.ibm.cics.server.examples.wlp.link;

import com.ibm.cics.server.CCSIDErrorException;
import com.ibm.cics.server.Channel;
import com.ibm.cics.server.ChannelErrorException;
import com.ibm.cics.server.CodePageErrorException;
import com.ibm.cics.server.Container;
import com.ibm.cics.server.ContainerErrorException;
import com.ibm.cics.server.InvalidRequestException;
import com.ibm.cics.server.Task;
import com.ibm.cics.server.invocation.CICSProgram;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.Resource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* Licensed Materials - Property of IBM                               */
/*                                                                    */
/* SAMPLE                                                             */
/*                                                                    */
/* (c) Copyright IBM Corp. 2016 All Rights Reserved                   */       
/*                                                                    */
/* US Government Users Restricted Rights - Use, duplication or        */
/* disclosure restricted by GSA ADP Schedule Contract with IBM Corp   */
/*                                                                    */
/**
 * Sample code to be invoked by EXEC CICS LINK.
 */
public class HelloLiberty {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	private DataSource dataSource;

	/**
	 * Prints a greeting message to STDOUT. When invoked with a text container
	 * called NAME on the current channel, that name is used. Otherwise, the
	 * user ID of the CICS task is used.
	 */
	@CICSProgram("HELLOWLP")  // This method can be invoked by EXEC CICS LINK PROGRAM(HELLOWLP)
	public void printMessage() {

		logger.info("Executing Java CICS Program [ HELLOWLP ]");

		StringBuilder sb = new StringBuilder();
		sb.append("Hello ");
		try {
			Channel currentChannel = Task.getTask().getCurrentChannel();
			if (currentChannel != null && currentChannel.getContainer("NAME") != null) {
				Container nameContainer = currentChannel.getContainer("NAME");
				sb.append(nameContainer.getString());
			} else {
				sb.append(Task.getTask().getUSERID());
			}
			sb.append(" from Liberty server ");
			sb.append(System.getProperty("com.ibm.cics.jvmserver.wlp.server.name"));

			if (dataSource == null) {
				logger.debug("Datasource not available. Initializing using context lookup");

				InitialContext ctx = new InitialContext();

				dataSource = (DataSource)ctx.lookup("jdbc/defaultCICSDataSource");

				logger.info("Datasource [ {} ]", dataSource.getClass());
			}

			String zal01Type = doSelect();

			sb.append("; ZAL_01_TYPE [ ").append(zal01Type).append(" ]");
			logger.info("Java CICS Program [ HELLOWLP ] completed; [ {} ]", sb);

		} catch (InvalidRequestException | ContainerErrorException | ChannelErrorException | CCSIDErrorException | CodePageErrorException | SQLException | NamingException e) {

			logger.error("Error while performing Java CICS Program [ HELLOWLP ]", e);
			Task.getTask().abend("OHNO");
		}
	}

	private String doSelect() throws SQLException {
		Connection connection = dataSource.getConnection();
		final String sql = "SELECT "
				+ "	ZAL_01_TYPE "
				+ "FROM "
				+ "	STS3.TCMZAL01 "
				+ "WHERE ZAL_01_ELEM = ?";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setString(1, "PYDUMMY9");

		ResultSet rs = stmt.executeQuery();
		final boolean hasNext = rs.next();

		if (!hasNext) {
			logger.info("No record found");

			throw new SQLException("No record found");
		} else {
			return rs.getString(1);
		}
	}
}
