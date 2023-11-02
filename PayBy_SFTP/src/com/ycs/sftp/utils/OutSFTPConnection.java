package com.ycs.sftp.utils;

import java.io.File;
import java.util.ResourceBundle;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import java.util.Scanner;
import com.didisoft.pgp.PGPLib;
import com.sshtools.j2ssh.SftpClient;
import com.sshtools.j2ssh.SshClient;
import com.sshtools.j2ssh.authentication.PublicKeyAuthenticationClient;
import com.sshtools.j2ssh.transport.publickey.SshPrivateKey;
import com.sshtools.j2ssh.transport.publickey.SshPrivateKeyFile;


public class OutSFTPConnection {

	private static Logger LOG = Logger.getLogger(OutSFTPConnection.class);
	private static String userName;
	private static String host;
	private static String sftpPrivateconnectionKey;
	private static int port;
	private static String localDir;
	private static String backupPath;
	private static String remoteDir;
	private static String encStatus;
	private static String imgDir;
	private static String sftpPublicKey;
	static {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("sftp");
		userName = resourceBundle.getString("sftp.username").trim();
		host = resourceBundle.getString("sftp.host").trim();
		port = Integer.parseInt(resourceBundle.getString("sftp.port").trim());
		sftpPrivateconnectionKey = resourceBundle.getString("sftp.privatekey.path").trim();
		localDir = resourceBundle.getString("sftp.localDir.path").trim();
		backupPath = resourceBundle.getString("sftp.backupPath.path").trim();
		remoteDir = resourceBundle.getString("sftp.remoteDir.path").trim();
		encStatus= resourceBundle.getString("sftp.encfile.on/off").trim();
		imgDir= resourceBundle.getString("sftp.imageDir.path").trim();
		sftpPublicKey=resourceBundle.getString("sftp.publickey.path").trim();
	}

	PGPLib pgpLib = new PGPLib();
	public boolean validateAndProcessEmbossing(String fileName, String path, String fileType)
	{ 
		System.out.println("Inside validation of embossing file name");
		boolean validateFlag=false;

		path = path+fileName+"/";
		File fl=new File(path);
		if(fl.exists())
		{

			System.out.println(" Emboss File Present in system--");

			validateFlag=true;
		}
		else
		{
			System.out.println("Emboss FILE DOES NOT Exist--");
			validateFlag=false;
		}
		return validateFlag;
	}



	public void putOnServer(String fileName, String localPath, String fileType) {
		LOG.debug("***  PAYBY SFTP INCOMING FILE PROCESS START SUSSCESSFULLY ***");
		String encExtension =".asc";
		SshClient srcSsh = null;
		SftpClient srcSftp = null;
		String encForFile="OFF";
		int authResult = -1;

		try {
			File localfile = new File(localPath + fileName);
			LOG.debug("Looking for files :" + localPath + fileName);
			if(fileType.contains("emboss"))
			{
				encForFile="ON";
			}

			if (localfile.isFile()) {
				LOG.debug("Looking for files on sftp...");
				LOG.debug("****** SFTP Process Started ******");
				LOG.debug("Connecting with password less connection type..");
				srcSsh = new SshClient();
				srcSsh.connect(host, port);
				PublicKeyAuthenticationClient auth = new PublicKeyAuthenticationClient();
				SshPrivateKeyFile sshPrivKeyFile = SshPrivateKeyFile.parse(new File(sftpPrivateconnectionKey));
				SshPrivateKey sshPrivKey = sshPrivKeyFile.toPrivateKey("");
				auth.setKey(sshPrivKey);
				auth.setUsername(userName);
				authResult = srcSsh.authenticate(auth);
			}
			LOG.debug("Connection Response Value - " + authResult);
			if (authResult == 4) {
				LOG.debug("Connection to Source Established sucessfully");
				srcSftp = srcSsh.openSftpClient();
				if (localfile.isFile()) {
					File srcFile = new File(String.valueOf(localPath) + "/" + fileName);

					if (!fileName.contains(".asc")) {
						OutSFTPConnection.LOG.debug("Inside the process for sftp file processing ");
						boolean setArmor = true;
						String filePath = String.valueOf(localPath) + "/" + fileName;
						String encFilePath = String.valueOf(localPath) + "/" + fileName + ".asc";
						if (encStatus.equalsIgnoreCase("ON")&&encForFile.equalsIgnoreCase("ON")) {
							LOG.debug("Inside Encryption block");
							pgpLib.encryptFile(filePath,sftpPublicKey, encFilePath, setArmor, true);
							LOG.debug("Before deleting original file from local path");
							FileUtils.forceDelete(srcFile);
							LOG.debug("After deleting file from local path");

						} else {
							encExtension ="";
						}
						srcSftp.put(String.valueOf(localPath) + "/" + fileName + encExtension,
								String.valueOf(remoteDir) + fileName + encExtension);
						LOG.debug("encrypted file move to file server-" + fileName + encExtension);
						LOG.debug("Before moving to backup folder");
						File srcFileName = new File(String.valueOf(localPath) + "/" + fileName + encExtension);
						LOG.debug("srcFileName is -" + srcFileName);
						File backupFile = new File(
								String.valueOf(String.valueOf(backupPath)) + "/" + fileName + encExtension);
						LOG.debug("backupFile is - " + backupFile);
						FileUtils.moveFile(srcFileName, backupFile);
						LOG.debug("After copying file to backup path");
					}
				}
			}

		} catch (Exception e) {
			LOG.debug("exception while connect to sftp/put the file old process-" + e.getMessage());

		} finally {
			try {
				if (srcSftp.isClosed() && srcSftp != null) {
					srcSftp.quit();
				}
				if (srcSsh.isConnected() && srcSsh != null) {
					srcSsh.disconnect();
				}
				LOG.debug("connection disconnect sucsessfully..!");

			} catch (Exception e) {
				LOG.debug("exception get while disconnect" + e.getMessage());
			}

		}
	}
	public static void main(String args[])
	{
		String imgDirName=null;
		OutSFTPConnection sftp=new OutSFTPConnection();
		boolean status;
		int option;
		Scanner scanner=new Scanner(System.in);	

		System.out.println("PLEASE SELECT THE BELOW OPTION.....? ");
		System.out.println("===============================================================");
		System.out.println("0. PRESS TO EXIT");
		System.out.println("1. ENTER EMBOSSING FILE NAME TO MOVE IN SFTP");
		System.out.println("2. ENTER IMAGE DIRECTORY NAME TO MOVE IN SFTP");
		System.out.println("===============================================================");
		option = scanner.nextInt();
		switch(option)
		{
		case 0:
			System.out.println("------------ THANK YOU ------------");
			System.exit(0);
			break;
		case 1:
			System.out.println("Enter Embossing File Name");
			String fileName=scanner.next();
			status=sftp.validateAndProcessEmbossing(fileName,localDir,"emboss");
			if(status==false)
			{

				System.out.println("DEAR TEAM PLEASE ENTER CORRECT EMBOSSING FILE NAME...");
				System.out.println("\n\n");

			}else
			{

				System.out.println("WE ARE IN PROCESSING.....");
				sftp.putOnServer(fileName,localDir,"emboss");

			}
			break;

		case 2:
			System.out.println("Please enter image directory name ");
			imgDirName=scanner.next();
			status=sftp.validateAndProcessEmbossing(imgDirName,imgDir,".zip");
			if(status==false)
			{

				System.out.println("PLEASE ENTER CORRECT IMAGE DIRECTORY NAME...");
				System.out.println("\n\n");

			}else
			{

				System.out.println("WE ARE IN PROCESSING.....");
				sftp.putOnServer(imgDirName,imgDir,".zip");
			}
			break;

		default:
			System.out.println("Dear Team please enter correct option !");
		}
	}
}