/*package com.ycs.imagedownload;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Shedular {
	public static void main(String[] args) {
		new Shedular().start();
	}

	public void start() {
		Timer timer = new Timer();
		Date date = new Date();
		timer.schedule(new ImageDownloadPoll(), date, 5000);
	}

	class ImageDownloadPoll extends TimerTask {
		ImageDownloader imageDownloader = null;

		@Override
		public void run() {
			//imageDownloader = new ImageDownloader();
			//imageDownloader.downloadImg();
		}

	}
}
*/