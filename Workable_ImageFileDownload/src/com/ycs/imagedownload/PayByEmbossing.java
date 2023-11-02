package com.ycs.imagedownload;

import java.util.ResourceBundle;
import java.util.Scanner;
import org.apache.log4j.Logger;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PayByEmbossing {
	private static Logger log = Logger.getLogger(PayByEmbossing.class);

	Scanner scanner = new Scanner(System.in);
	static String selectquery = null;
	static String pendingQuery = null;
	static String completedQuery = null;
	static String totalRecordQuery = null;
	static String updateQuery = null;
	static String folderPath = null;
	static String embossPath=null;
	static String fileStartName = null;
	static String fileNameExt = null;
	static String selectcurrentdate = null;
	static String validateEmbossingQuery=null;
	static File dir=null;


	static {
		ResourceBundle bundle=ResourceBundle.getBundle("common");
		selectquery = bundle.getString("qr.select.query");
		completedQuery = bundle.getString("qr.completedRecord.query");
		updateQuery = bundle.getString("qr.updateRecord.query");
		folderPath = bundle.getString("folder.path");
		fileNameExt = bundle.getString("flle.name.ext");
		totalRecordQuery = bundle.getString("qr.totalRecordQuery");
		selectcurrentdate = bundle.getString("qr.currentdate");
		validateEmbossingQuery=bundle.getString("qr.validateEmbossing.query");
		embossPath=bundle.getString("embossFiles.path");
	}

	public static void main(String[] args) {
		PayByEmbossing dataExtract = new PayByEmbossing();
		dataExtract.embossingDataGenrater();
	}


	public void embossingDataGenrater() {
		boolean status;
		int option;

		while(true)
		{
			System.out.println("PLEASE SELECT THE BELOW OPTION.....? ");
			System.out.println("===============================================================");
			System.out.println("0. PRESS TO EXIT");
			System.out.println("1. ENTER EMBOSSING FILE NAME ");
			System.out.println("===============================================================");
			option = scanner.nextInt();

			switch(option)
			{
			case 0:
				System.out.println("------------THANK YOU------------");
				System.exit(0);
			case 1:
				System.out.println("Enter Embossing File Name\n");
				String embossingName=scanner.next();
				status=this.validateAndProcessEmbossing(embossingName.trim());
				if(status==false)
				{
					
					System.out.println("DEAR TEAM PLEASE ENTER CORRECT EMBOSSING FILE NAME...");
					System.out.println("\n\n");

				}else
				{

					System.out.println("WE ARE IN PROCESSING.....");
					ImageDownloader.downloadImg(embossingName);
					break;
				}
			default:
				System.out.println("Dear Team please Enter Correct Option !");

			}
		}}

	public void result(int total, int completed)
	{  
		
		System.out.println("--------------------------Dear Team------------------------------\n");
		System.out.println("Total Record Received :- "+total);
		System.out.println("Total completed Received :- "+completed);
		
	}



	private boolean validateAndProcessEmbossing(String embosFile)
	{ 

		log.debug("[PayByEmbossing][validateAndProcessEmbossing] Inside validateAndProcessEmbossing method");

		boolean tableFlag=true;
		PreparedStatement preparedStatement = null;
		Connection connection = null;
		ResultSet resultSet = null;
		String embossingCount= null;
		boolean validateFlag=false;
		embossPath = embossPath+embosFile;
		log.debug("Searching for emboss file in "+embossPath+" this path");
		File fl=new File(embossPath);
		if(fl.exists())
		{

			log.debug("[PayByEmbossing][validateAndProcessEmbossing] Emboss File Present in system--"+embosFile);
			validateFlag=true;

		}
		else
		{

			log.debug("[PayByEmbossing][validateAndProcessEmbossing] Emboss FILE DOES NOT Exist--"+embosFile);
			System.out.println("[PayByEmbossing][validateAndProcessEmbossing] Emboss FILE DOES NOT Exist--"+embosFile);
			validateFlag=false;
		}

		try {

			connection = DBUtils.getConnection();
			if(connection != null) {
				log.debug("[PayByEmbossing][validateAndProcessEmbossing]Connection Success -- " + connection);
				preparedStatement = connection.prepareStatement(validateEmbossingQuery);
				preparedStatement.setString(1, embosFile);
				resultSet=preparedStatement.executeQuery();
				while(resultSet.next())
				{
					embossingCount=resultSet.getString(1);
					log.debug("[PayByEmbossing][validateAndProcessEmbossing]Counting of EmbossFile "+embossingCount+"Embossing Name " +embosFile);
				}
				if("0".equalsIgnoreCase(embossingCount))
				{
					System.out.println("[PayByEmbossing][validateAndProcessEmbossing]Embossing File not found in table..."+embosFile);

					log.debug("[PayByEmbossing][validateAndProcessEmbossing]Embossing File not found in table..."+embosFile);
					tableFlag=false;
				}
				else
				{
					System.out.println("[PayByEmbossing][validateAndProcessEmbossing]Present In Table ..."+embosFile);
					tableFlag=true;

				}

			}

		}
		catch (Exception e) {
			log.debug("[PayByEmbossing][validateAndProcessEmbossing]faccing Exception while Connection "+ e);

		}finally {
			DBUtils.closePrepareStatement(preparedStatement);;
			DBUtils.closeConnection(connection);
			DBUtils.closeResultset(resultSet);
		}
		if(tableFlag==true && validateFlag==true)
		{
			return true;
		}else
		{
			return false;
		}	 
	}
}

