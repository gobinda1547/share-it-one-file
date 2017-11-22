package org.ju.cse.gobinda;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Receiver implements Runnable {

	private String fileWhereToSave;

	public Receiver() {

	}

	@Override
	public void run() {

		try {

			ShareIt.makeGoToMainPanelFromReceiverBtnEnabled(false);

			fileWhereToSave = "C:/Users/" + System.getProperty("user.name") + "/Documents/ShareIt/";

			ServerSocket serverSocket = null;
			Socket socket = null;

			for (int i = 1; i <= 100 && socket == null; i++) {
				try {
					serverSocket = new ServerSocket(ShareIt.getReceiverPortValue());
					serverSocket.setSoTimeout(1000);
					socket = serverSocket.accept();
				} catch (Exception e) {
					ShareIt.setValuesToWaitingForSenderProgressBar(i, String.valueOf(i) + "%");
					serverSocket.close();
				}
			}

			if (socket == null) {
				ShareIt.setValuesToWaitingForSenderProgressBar(0, "No Sender Found");
				ShareIt.makeGoToMainPanelFromReceiverBtnEnabled(true);
				return;
			}

			ShareIt.setValuesToWaitingForSenderProgressBar(0, "Connected!");
			ShareIt.setValuesToTotalFileReceivedProgressBar(0, String.valueOf(0) + "%");

			DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

			ShareIt.showThisPanelIntoReceiverPanel("receiverTransmitePanel");

			try {

				boolean thisIsFile = dataInputStream.readBoolean();

				if (thisIsFile) {

					String nowFileName = dataInputStream.readUTF();

					long nowFileSize = dataInputStream.readLong();

					File nowFile = new File(fileWhereToSave + nowFileName);
					nowFile.getParentFile().mkdirs();
					FileOutputStream fos = new FileOutputStream(nowFile);
					byte[] buffer = new byte[4096];

					int read = 0;
					long remaining = nowFileSize;

					double perCycleCopy = 100.00 / (nowFileSize / 4096);
					double incrementor = 0.0;
					int value = (int) incrementor;

					while ((read = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, remaining))) > 0) {

						value = Math.min(100, (int) incrementor);
						ShareIt.setValuesToTotalFileReceivedProgressBar(value, String.valueOf(value) + "%");

						remaining -= read;
						fos.write(buffer, 0, read);

						incrementor += perCycleCopy;
					}

					fos.close();
					ShareIt.setValuesToTotalFileReceivedProgressBar(100, "Completed");

				} else {
					String nowFileName = dataInputStream.readUTF();
					nowFileName = fileWhereToSave + nowFileName;
					new File(nowFileName).mkdirs();
					ShareIt.setValuesToTotalFileReceivedProgressBar(100, "Completed");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			socket.close();
			serverSocket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		ShareIt.setValuesToWaitingForSenderProgressBar(0, "DisConnected!");
		ShareIt.makeGoToMainPanelFromReceiverBtnEnabled(true);

	}

}
