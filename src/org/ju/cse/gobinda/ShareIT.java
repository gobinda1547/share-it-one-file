package org.ju.cse.gobinda;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.CardLayout;

import javax.swing.JButton;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;

import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JProgressBar;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import java.awt.Color;

public class ShareIt extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int RECEIVER_PORT = 55555;

	private static JPanel contentPane;
	private static JPanel senderPanel;
	private static JPanel receiverPanel;

	private JTextField enterReceiverIpAddressTextField;
	private JTextField txtTotalFileSend;
	private JTextField txtTotalTransmittedFile;
	private JTextField txtTotalFileReceive;
	private JTextField txtTotalFileSize;

	private static JButton goToMainPanelFromSender;
	private static JButton goToMainPanelFromReceiver;

	private static CardLayout mainPanelCardLayout;
	private static CardLayout senderPanelCardLayout;
	private static CardLayout receiverPanelCardlayout;

	private JTextArea dragAndDropTextArea;

	private static JProgressBar waitingForReceiverProgressBar;
	private static JProgressBar totalFileTransmittedProgressBar;

	private static JProgressBar waitingForSenderProgressBar;
	private static JProgressBar totalFileReceivedProgressBar;

	private Sender sender;
	private Receiver receiver;

	/**
	 * Create the frame.
	 */
	public ShareIt() {

		sender = new Sender();
		receiver = new Receiver();

		DropTargetHandler dropTargetHandler = new DropTargetHandler();

		mainPanelCardLayout = new CardLayout();
		senderPanelCardLayout = new CardLayout();
		receiverPanelCardlayout = new CardLayout();

		setTitle("Share IT");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 282, 445);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(mainPanelCardLayout);

		JPanel optionPanel = new JPanel();
		contentPane.add(optionPanel, "optionPanel");
		optionPanel.setLayout(null);

		JButton showSenderPanelBtn = new JButton("Send");
		showSenderPanelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				showThisPanelIntoMainPanel("senderPanel");
				showThisPanelIntoSenderPanel("senderSelectFilePanel");
			}
		});
		showSenderPanelBtn.setFont(new Font("Tahoma", Font.PLAIN, 18));
		showSenderPanelBtn.setBounds(62, 128, 135, 43);
		optionPanel.add(showSenderPanelBtn);

		JButton showReceiverPanelBtn = new JButton("Receive");
		showReceiverPanelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				showThisPanelIntoMainPanel("receiverPanel");
				receiver = new Receiver();
				new Thread(receiver).start();
			}
		});
		showReceiverPanelBtn.setFont(new Font("Tahoma", Font.PLAIN, 18));
		showReceiverPanelBtn.setBounds(62, 182, 135, 43);
		optionPanel.add(showReceiverPanelBtn);

		JLabel lblGobindaPaul = new JLabel("Gobinda Paul [01923055489]");
		lblGobindaPaul.setForeground(new Color(0, 128, 128));
		lblGobindaPaul.setBounds(10, 381, 246, 14);
		optionPanel.add(lblGobindaPaul);

		senderPanel = new JPanel();
		contentPane.add(senderPanel, "senderPanel");
		senderPanel.setLayout(senderPanelCardLayout);

		JPanel senderSelectFilePanel = new JPanel();
		senderPanel.add(senderSelectFilePanel, "senderSelectFilePanel");
		senderSelectFilePanel.setLayout(null);

		JPanel dragAndDropPanel = new JPanel();
		dragAndDropPanel.setBounds(10, 43, 246, 320);
		senderSelectFilePanel.add(dragAndDropPanel);
		dragAndDropPanel.setLayout(new BorderLayout(0, 0));

		JScrollPane senderFileListScrollPane = new JScrollPane();
		dragAndDropPanel.add(senderFileListScrollPane, BorderLayout.CENTER);

		dragAndDropTextArea = new JTextArea();
		dragAndDropTextArea.setEditable(false);
		dragAndDropTextArea.setText("Drag and Drop File Here");
		senderFileListScrollPane.setViewportView(dragAndDropTextArea);

		dragAndDropTextArea.setDropTarget(dropTargetHandler);

		JButton sendSelectedFileBtn = new JButton("Send");
		sendSelectedFileBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				sender.setReceiverIpAddress(enterReceiverIpAddressTextField.getText().trim());
				boolean recevierIpAddresssValidnessCheck = sender.isIpAddressValid();
				if (recevierIpAddresssValidnessCheck == false) {
					JOptionPane.showMessageDialog(null, "ENTER A VALID IP ADDRESS!");
					return;
				}

				boolean selectedFileValidCheck = sender.isSelectedFileValid();
				if (selectedFileValidCheck == false) {
					JOptionPane.showMessageDialog(null, "FILE SELECTION ERROR!");
					return;
				}

				makeGoToMainPanelFromSenderBtnEnabled(false);
				showThisPanelIntoSenderPanel("senderTransmiteProgressPanel");
				new Thread(sender).start();

			}
		});
		sendSelectedFileBtn.setBounds(176, 374, 80, 23);
		senderSelectFilePanel.add(sendSelectedFileBtn);

		enterReceiverIpAddressTextField = new JTextField();
		enterReceiverIpAddressTextField.setText("Enter Receiver Ip Address");
		enterReceiverIpAddressTextField.setBounds(10, 11, 194, 23);
		senderSelectFilePanel.add(enterReceiverIpAddressTextField);
		enterReceiverIpAddressTextField.setColumns(10);

		JButton clearReceiverIpAddressButton = new JButton("XX");
		clearReceiverIpAddressButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				enterReceiverIpAddressTextField.setText("");
			}
		});
		clearReceiverIpAddressButton.setBounds(211, 11, 45, 23);
		senderSelectFilePanel.add(clearReceiverIpAddressButton);

		JButton goBackToOptionPanelFromSenderPanelBtn = new JButton("Clear");
		goBackToOptionPanelFromSenderPanelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				clearSenderInformation();
			}
		});
		goBackToOptionPanelFromSenderPanelBtn.setBounds(90, 374, 76, 23);
		senderSelectFilePanel.add(goBackToOptionPanelFromSenderPanelBtn);

		JButton clearDragAndDropFileListBtn = new JButton("Back");
		clearDragAndDropFileListBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearSenderInformation();
				showThisPanelIntoMainPanel("optionPanel");
			}
		});
		clearDragAndDropFileListBtn.setBounds(10, 374, 76, 23);
		senderSelectFilePanel.add(clearDragAndDropFileListBtn);

		JPanel senderTransmiteProgressPanel = new JPanel();
		senderPanel.add(senderTransmiteProgressPanel, "senderTransmiteProgressPanel");
		senderTransmiteProgressPanel.setLayout(null);

		txtTotalFileSend = new JTextField();
		txtTotalFileSend.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtTotalFileSend.setEditable(false);
		txtTotalFileSend.setText("Waiting For Receiver");
		txtTotalFileSend.setHorizontalAlignment(SwingConstants.CENTER);
		txtTotalFileSend.setColumns(10);
		txtTotalFileSend.setBounds(52, 65, 162, 34);
		senderTransmiteProgressPanel.add(txtTotalFileSend);

		waitingForReceiverProgressBar = new JProgressBar();
		waitingForReceiverProgressBar.setBounds(52, 104, 162, 26);
		waitingForReceiverProgressBar.setStringPainted(true);
		senderTransmiteProgressPanel.add(waitingForReceiverProgressBar);

		txtTotalTransmittedFile = new JTextField();
		txtTotalTransmittedFile.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtTotalTransmittedFile.setEditable(false);
		txtTotalTransmittedFile.setText("Total Transmitted");
		txtTotalTransmittedFile.setHorizontalAlignment(SwingConstants.CENTER);
		txtTotalTransmittedFile.setColumns(10);
		txtTotalTransmittedFile.setBounds(52, 164, 162, 35);
		senderTransmiteProgressPanel.add(txtTotalTransmittedFile);

		totalFileTransmittedProgressBar = new JProgressBar();
		totalFileTransmittedProgressBar.setBounds(52, 204, 162, 26);
		totalFileTransmittedProgressBar.setStringPainted(true);
		senderTransmiteProgressPanel.add(totalFileTransmittedProgressBar);

		goToMainPanelFromSender = new JButton("Go To Main Panel");
		goToMainPanelFromSender.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setValuesToWaitingForReceiverProgressBar(0, String.valueOf("0") + "%");
				setValuesToTotalFileTransmittedProgressBar(0, String.valueOf("0") + "%");
				clearSenderInformation();
				showThisPanelIntoMainPanel("optionPanel");
			}
		});
		goToMainPanelFromSender.setBounds(52, 262, 162, 23);
		senderTransmiteProgressPanel.add(goToMainPanelFromSender);

		receiverPanel = new JPanel();
		contentPane.add(receiverPanel, "receiverPanel");
		receiverPanel.setLayout(receiverPanelCardlayout);

		JPanel receiverTransmitePanel = new JPanel();
		receiverPanel.add(receiverTransmitePanel, "receiverTransmitePanel");
		receiverTransmitePanel.setLayout(null);

		txtTotalFileReceive = new JTextField();
		txtTotalFileReceive.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtTotalFileReceive.setText("Waiting For Sender");
		txtTotalFileReceive.setHorizontalAlignment(SwingConstants.CENTER);
		txtTotalFileReceive.setEditable(false);
		txtTotalFileReceive.setColumns(10);
		txtTotalFileReceive.setBounds(52, 71, 162, 29);
		receiverTransmitePanel.add(txtTotalFileReceive);

		waitingForSenderProgressBar = new JProgressBar();
		waitingForSenderProgressBar.setStringPainted(true);
		waitingForSenderProgressBar.setBounds(52, 105, 162, 29);
		receiverTransmitePanel.add(waitingForSenderProgressBar);

		txtTotalFileSize = new JTextField();
		txtTotalFileSize.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtTotalFileSize.setText("Total Received");
		txtTotalFileSize.setHorizontalAlignment(SwingConstants.CENTER);
		txtTotalFileSize.setEditable(false);
		txtTotalFileSize.setColumns(10);
		txtTotalFileSize.setBounds(52, 170, 162, 29);
		receiverTransmitePanel.add(txtTotalFileSize);

		totalFileReceivedProgressBar = new JProgressBar();
		totalFileReceivedProgressBar.setStringPainted(true);
		totalFileReceivedProgressBar.setBounds(52, 203, 162, 29);
		receiverTransmitePanel.add(totalFileReceivedProgressBar);

		goToMainPanelFromReceiver = new JButton("Go To Main Panel");
		goToMainPanelFromReceiver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setValuesToWaitingForSenderProgressBar(0, String.valueOf("0") + "%");
				setValuesToTotalFileReceivedProgressBar(0, String.valueOf("0") + "%");
				showThisPanelIntoMainPanel("optionPanel");
			}
		});
		goToMainPanelFromReceiver.setBounds(52, 267, 162, 23);
		receiverTransmitePanel.add(goToMainPanelFromReceiver);

		JButton showIpAddressBtn = new JButton("Show Your Ip Address");
		showIpAddressBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					JOptionPane.showMessageDialog(null, InetAddress.getLocalHost().getHostAddress().toString());
				} catch (HeadlessException e1) {
					JOptionPane.showMessageDialog(null, "CAN NOT ACCESS IP ADDRESS");
				} catch (UnknownHostException e1) {
					JOptionPane.showMessageDialog(null, "CAN NOT ACCESS IP ADDRESS");
				}
			}
		});
		showIpAddressBtn.setBounds(52, 293, 162, 23);
		receiverTransmitePanel.add(showIpAddressBtn);
	}

	public static void setValuesToTotalFileReceivedProgressBar(int percentage, String message) {
		totalFileReceivedProgressBar.setString(message);
		totalFileReceivedProgressBar.setValue(percentage);
	}

	public static void setValuesToWaitingForSenderProgressBar(int percentage, String message) {
		waitingForSenderProgressBar.setString(message);
		waitingForSenderProgressBar.setValue(percentage);
	}

	public static void setValuesToTotalFileTransmittedProgressBar(int percentage, String message) {
		totalFileTransmittedProgressBar.setString(message);
		totalFileTransmittedProgressBar.setValue(percentage);
	}

	public static void setValuesToWaitingForReceiverProgressBar(int percentage, String message) {
		waitingForReceiverProgressBar.setString(message);
		waitingForReceiverProgressBar.setValue(percentage);
	}

	public void clearSenderInformation() {
		dragAndDropTextArea.setText("Drag and Drop File Here");
		enterReceiverIpAddressTextField.setText("Enter Receiver Ip Address");
		sender.clearAllInformation();
	}

	public static void showThisPanelIntoMainPanel(String panelName) {
		mainPanelCardLayout.show(contentPane, panelName);
	}

	public static void showThisPanelIntoSenderPanel(String panelName) {
		senderPanelCardLayout.show(senderPanel, panelName);
	}

	public static void showThisPanelIntoReceiverPanel(String panelName) {
		receiverPanelCardlayout.show(receiverPanel, panelName);
	}

	public static void makeGoToMainPanelFromSenderBtnEnabled(boolean tof) {
		goToMainPanelFromSender.setEnabled(tof);
	}

	public static void makeGoToMainPanelFromReceiverBtnEnabled(boolean tof) {
		goToMainPanelFromReceiver.setEnabled(tof);
	}

	class DropTargetHandler extends DropTarget {

		private static final long serialVersionUID = 1L;

		public synchronized void drop(DropTargetDropEvent evt) {
			if (dragAndDropTextArea.getText().startsWith("Drag")) {
				dragAndDropTextArea.setText("");
			}

			try {
				evt.acceptDrop(DnDConstants.ACTION_COPY);

				@SuppressWarnings("unchecked")
				List<File> droppedFiles = (List<File>) evt.getTransferable()
						.getTransferData(DataFlavor.javaFileListFlavor);

				for (File file : droppedFiles) {
					dragAndDropTextArea.setText(file.getName() + "\n");
					sender.setSelectedFile(file);
					return;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}

	}

	public static int getReceiverPortValue() {
		return RECEIVER_PORT;
	}

	public static void initialize() {
		new ShareIt().setVisible(true);
	}
}
