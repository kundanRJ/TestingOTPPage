package com.ycs.imagedownload;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


import org.apache.log4j.Logger;


public class ImageDownloader {
	private static Logger log = Logger.getLogger(ImageDownloader.class);
	static String selectquery = null;
	static String pendingQuery = null;
	static String completedQuery = null;
	static String totalRecordQuery = null;
	static String updateQuery = null;
	static String folderPath = null;
	static String fileStartName = null;
	static String fileNameExt = null;
	static String selectcurrentdate = null;
	static String dataByEmboss = null;
	static File dir = null;
	static {
		ResourceBundle bundle = ResourceBundle.getBundle("common");
		selectquery = bundle.getString("qr.select.query");
		completedQuery = bundle.getString("qr.completedRecord.query");
		updateQuery = bundle.getString("qr.updateRecord.query");
		folderPath = bundle.getString("folder.path");
		fileStartName = bundle.getString("file.start.name");
		fileNameExt = bundle.getString("flle.name.ext");
		totalRecordQuery = bundle.getString("qr.totalRecordQuery");
		selectcurrentdate = bundle.getString("qr.currentdate");
		dataByEmboss = bundle.getString("qr.selectDataByEmbos.query");

	}

	public static void downloadImg(String embossFile) {

		log.debug(" Updated on 18-OCT-23");
		PreparedStatement preparedStatement = null;
		Connection conection = null;
		ResultSet resultSet = null;
		String packRefNo = null;
		String cardDespCourier = null;
		Statement stmt = null;
		ResultSet rs_time = null;
		try {
			conection = DBUtils.getConnection();
			if (conection != null) {
				log.debug("Connection Success -- " + conection);
				stmt = conection.createStatement();
				rs_time = stmt.executeQuery(selectcurrentdate);
				String current_time = null;
				if (rs_time.next()) {
					current_time = rs_time.getString(1);
					log.debug(" current_date --" + current_time);
				}
				folderPath = folderPath+embossFile+ "/";
				log.debug(" Updated Image folder Path--" + folderPath);
				dir = new File(folderPath);
				if (!dir.exists()) {
					if (dir.mkdir()) {
					
					log.debug("Folder Created Successfully || getAbsolutepath --" + dir.getAbsolutePath());
					} else {
						log.debug(" Folder Creation Failed--");
					}
				} else {
					log.debug(" Folder Already Exisist--");
				}
				preparedStatement = conection.prepareStatement(dataByEmboss);
				log.debug("QR SELECT Query -- " + dataByEmboss);
				preparedStatement.setString(1, embossFile);
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					packRefNo = resultSet.getString("PACKREFNO");
					cardDespCourier = resultSet.getString("SOLD_BY_USERID");
					log.debug("packRefNo is - " + packRefNo);
					log.debug("Image URL is - " + cardDespCourier);
					downloadImage(cardDespCourier, folderPath, packRefNo, conection);
				}
				log.debug("NO QR URL found in card_master_wk for Download "
						+ (resultSet.isAfterLast()));
				log.debug("NO QR URL found in card_master_wk for Download ");
				if (resultSet.isAfterLast()) {
					boolean status = checkAndUpdate(conection, embossFile);
					if (status) {
						log.debug(" COMPLETED.... ");
					} else {
						log.debug(" Pending due to some error.... ");
						log.debug(" Do you want to continue .. ");
					}
				} else {
					log.debug("NO QR URL found in card_master_wk for Download ");
				}
			}
		} catch (SQLException e) {
			log.error(" SQLERROR in downloadImg" + e);
		} catch (Exception ex) {
			log.error(" ERROR in downloadImg" + ex);
		} finally {
			DBUtils.closePrepareStatement(preparedStatement);
			DBUtils.closeResultset(resultSet);
			DBUtils.closeConnection(conection);
		}
	}

	public static boolean checkAndUpdate(Connection conection, String fileName) throws SQLException {
		log.debug("Now going to check for Count of dowloaded Images AND file .."+fileName.trim());
		PreparedStatement completedPreStm = null;
		PreparedStatement totalPreStm=null;
		PayByEmbossing emps=new PayByEmbossing();
		ResultSet rs = null;
		ResultSet rss = null;
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		boolean finalStatus = false;
		
		try {
			if (conection != null) {
				log.debug("Connection Success -- " + conection);
				log.debug("File Name  -- " + fileName);
				completedPreStm = conection.prepareStatement(completedQuery);
				log.debug("Execute Query for completed records-- "+ completedQuery);
				completedPreStm.setString(1, fileName);
				rs = completedPreStm.executeQuery();
			    rs.next();
			    conection.commit();
			    log.debug(" after commit-- " );
				int completed_count = rs.getInt(1);
				
				log.debug(" Completed success Count-- " + completed_count);
			
				totalPreStm =conection.prepareStatement(totalRecordQuery);
				totalPreStm.setString(1,fileName);
				log.debug(" Total count Query for embossing file-- " + totalRecordQuery);
				rss = totalPreStm.executeQuery();
				rss.next();
				conection.commit();
				int total_count = rss.getInt(1);
				
				log.debug(" Total count for embossing file-- " + total_count);
				emps.result(total_count, completed_count);
				
				if (completed_count == total_count) {
					log.debug(" Count Match Successfully -- ");
					finalStatus = true;
					log.debug("================================================================");
					log.debug("--------------------------Dear Team------------------------------");
					log.debug("               Total Record Received :- " + total_count);
					log.debug("            Total completed Received :- " + completed_count);
					log.debug("================================================================");
					
					

					try {
						folderPath = dir.getAbsolutePath();
						String sourceDir = folderPath;
						String zipFileName = folderPath +".zip";
						File sourceDirectory = new File(sourceDir);
						fos = new FileOutputStream(zipFileName);
						zos = new ZipOutputStream(fos);
						addDirectoryToZip(sourceDirectory, sourceDirectory.getName(), zos);
						
                       
				   } catch (IOException e) {
						e.printStackTrace();
					} finally {
						if (zos != null) {
							zos.close();
						}
						if (fos != null) {
							fos.close();
						}
					}
				} else {
					log.debug(" Count NOT Matched --");
					finalStatus = false;
				}
			}
			
		         
		}
		
		catch (Exception e) {
			log.error(" Exception --" + e);
		} finally {
             DBUtils.closePrepareStatement(completedPreStm);
             DBUtils.closePrepareStatement(totalPreStm);
             DBUtils.closeResultset(rss);
             DBUtils.closeResultset(rs);
			
		}

		return finalStatus;
	}

	private static void addDirectoryToZip(File directory, String parent, ZipOutputStream zos) throws IOException {
		File[] files = directory.listFiles();

		for (File file : files) {
			if (file.isDirectory()) {
				addDirectoryToZip(file, parent + "/" + file.getName(), zos);
				continue;
			}

			FileInputStream fis = new FileInputStream(file);
			ZipEntry zipEntry = new ZipEntry(parent + "/" + file.getName());
			zos.putNextEntry(zipEntry);

			byte[] buffer = new byte[1024];
			int length;
			while ((length = fis.read(buffer)) > 0) {
				zos.write(buffer, 0, length);
			}

			fis.close();
		}
	}

	public static void downloadImage(String imageUrl, String folderPath, String proxyVal, Connection conn)
			throws IOException, SQLException {
		String flag = "";
		try {

			log.debug("Inside Download Image  :-");
			URL url = new URL(imageUrl);
			InputStream in = url.openStream();
			OutputStream out = new FileOutputStream(
					folderPath.concat(proxyVal.concat(fileStartName).concat(fileNameExt)));
			byte[] buffer = new byte[2048];
			int bytesRead = 0;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
			log.debug(" Download Image Name :-"+ folderPath.concat(proxyVal.concat(fileStartName).concat(fileNameExt)));
			flag = "C";
			updateNotification(flag, proxyVal, conn);
			in.close();
			out.close();
		} catch (Exception e) {
			log.error("while error in downloadImg image :-" + e);
			flag = "F";
			updateNotification(flag, proxyVal, conn);
		}

	}

	public static void updateNotification(String flag, String proxy, Connection con) {
		log.debug("Now going to updating flag for dowloaded Image ..");
		PreparedStatement ps = null;

		try {
			if (con != null) {
				log.debug("Connection Success -- " + con);
			}
			log.debug("Update Query -- update card_master_wk set GRP_ID=? where PACKREFNO=?");
			log.debug(" flag --" + flag);

			ps = con.prepareStatement(updateQuery); 
			if ("F".equals(flag)) {
				log.debug("[Fetch flag F -- ");
				ps.setString(1, flag);
				ps.setString(2, proxy);
				int count = ps.executeUpdate();
				con.commit();
				log.debug(count+" updateQuery Count for proxy -- "+proxy);
			} else {
				log.debug("Fetch flag C -- ");
				log.debug(" updateQuery -- " + updateQuery);
				ps.setString(1, flag);
				ps.setString(2, proxy);
				int count = ps.executeUpdate();
				con.commit();
				log.debug( count +" Records Updated for proxy -- "+ proxy);
			}

		} catch (SQLException s) {
			log.error("while error in notification update-" + s);
		} catch (Exception e) {
			log.error("while error in notification update-" + e);
		} finally {
			DBUtils.closePrepareStatement(ps);


		}
	}


}
