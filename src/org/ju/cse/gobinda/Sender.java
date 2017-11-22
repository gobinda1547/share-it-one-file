package org.ju.cse.gobinda;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;

import java.net.Socket;

public class Sender implements Runnable {

	private File selectedFile;
	private String receiverIpAddress;

	public Sender() {

	}

	@Override
	public void run() {

		try {
			ShareIt.makeGoToMainPanelFromSenderBtnEnabled(false);

			Socket socket = null;

			boolean socketIsConnected = false;
			for (int totalConnectingStep = 0; totalConnectingStep <= 3 && !socketIsConnected; totalConnectingStep++) {
				try {
					socket = new Socket(receiverIpAddress, ShareIt.getReceiverPortValue());
					socketIsConnected = true;
				} catch (Exception e) {
					ShareIt.setValuesToWaitingForReceiverProgressBar(totalConnectingStep,
							String.valueOf(totalConnectingStep) + "%");

				}
			}

			if (socketIsConnected == false) {
				ShareIt.setValuesToWaitingForReceiverProgressBar(0, "No Receiver Found");
				ShareIt.makeGoToMainPanelFromSenderBtnEnabled(true);
				return;
			}

			ShareIt.setValuesToWaitingForReceiverProgressBar(0, "Connected!");
			ShareIt.setValuesToTotalFileTransmittedProgressBar(0, String.valueOf(0) + "%");

			DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

			boolean thisIsFile = selectedFile.isFile();
			dataOutputStream.writeBoolean(thisIsFile);
			dataOutputStream.flush();

			dataOutputStream.writeUTF(selectedFile.getName());
			dataOutputStream.flush();

			if (thisIsFile) {

				long nowFileSize = selectedFile.length();

				dataOutputStream.writeLong(nowFileSize);
				dataOutputStream.flush();

				FileInputStream fis = new FileInputStream(selectedFile);
				byte[] buffer = new byte[4096];

				int read = 0;
				long remaining = nowFileSize;

				double perCycleCopy = 100.00 / (nowFileSize / 4096);
				double incrementor = 0.0;
				int value = (int) incrementor;

				while ((read = fis.read(buffer, 0, (int) Math.min(buffer.length, remaining))) > 0) {
					value = Math.min(100, (int) incrementor);
					ShareIt.setValuesToTotalFileTransmittedProgressBar(value, String.valueOf(value));
					dataOutputStream.write(buffer, 0, read);
					dataOutputStream.flush();
					remaining -= read;
					incrementor += perCycleCopy;
				}
				fis.close();
				ShareIt.setValuesToTotalFileTransmittedProgressBar(100, "Completed");
			} else {
				// folder name already send
				ShareIt.setValuesToTotalFileTransmittedProgressBar(100, "Completed");
			}

			socket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		ShareIt.setValuesToWaitingForReceiverProgressBar(0, "DisConnected!");
		ShareIt.makeGoToMainPanelFromSenderBtnEnabled(true);

	}

	public void setSelectedFile(File selectedFile) {
		this.selectedFile = selectedFile;
	}

	public void setReceiverIpAddress(String receiverIpAddress) {
		this.receiverIpAddress = receiverIpAddress;
	}

	public boolean isSelectedFileValid() {

		if (selectedFile == null) {
			return false;
		}

		if (selectedFile.getName().length() == 0) {
			return false;
		}
		return true;
	}

	public boolean isIpAddressValid() {

		if (receiverIpAddress == null) {
			return false;
		}

		for (int i = 0; i < receiverIpAddress.length(); i++) {
			char ch = receiverIpAddress.charAt(i);
			if (ch != '.' && (ch < '0' && '9' < ch)) {
				return false;
			}
		}

		String[] partsOfReceiverIpAddress = receiverIpAddress.split("\\.");
		if (partsOfReceiverIpAddress.length != 4) {
			return false;
		}

		for (int i = 0; i < 4; i++) {
			if (partsOfReceiverIpAddress[i].length() > 3) {
				return false;
			}
		}

		if (receiverIpAddress.indexOf("..") != -1) {
			return false;
		}

		if (receiverIpAddress.startsWith(".")) {
			return false;
		}

		if (receiverIpAddress.endsWith(".")) {
			return false;
		}

		return true;

	}

	public void clearAllInformation() {
		receiverIpAddress = null;
		selectedFile = null;
	}

}