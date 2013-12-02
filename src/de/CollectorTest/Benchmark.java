package de.CollectorTest;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.crypto.spec.GCMParameterSpec;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class Benchmark extends JFrame{
	
	//	parameter
	static int overAllRounds = 100000;
	static int roundsToLive = 200; 
	static int arrLength = 1000;
	
	boolean isRunning = false;
	Worker worker;
	static JTextArea console = new JTextArea("", 5, 22);
	
	public static void main(String[] args) {
		new Benchmark();
	}
	
	public Benchmark() {
		//	create new worker
		worker  = new Worker();
		
		//	window
		this.setSize(new Dimension(640, 480));
		
		//	panel for buttons
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		this.add(buttonPanel,BorderLayout.SOUTH);
		
		//	panel for consol
		final JPanel consolePanel = new JPanel();
		consolePanel.setPreferredSize(new Dimension(100,100));
		consolePanel.setLayout(new FlowLayout());
		this.add(consolePanel,BorderLayout.NORTH);
		
		//	loading and pause label
		ImageIcon icon = new ImageIcon(Benchmark.class.getResource("assets/loading.gif"));
		final JLabel loadingLabel = new JLabel(icon);
		icon = new ImageIcon(Benchmark.class.getResource("assets/pause.gif"));
		final JLabel pauseLabel = new JLabel(icon);
		
		//	status text if is running
		final JLabel statusText = new JLabel("Test nicht gestartet");
		statusText.setPreferredSize(new Dimension(150,20));
		
		console.setBackground(getBackground());
		consolePanel.add(console);
		consolePanel.add(pauseLabel);
		
		consolePanel.add(statusText);
		
		//	startStop button
		final JButton startStopWorker = new JButton("Test starten");
		startStopWorker.setPreferredSize(new Dimension(150, 40));
		startStopWorker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (worker.isAlive()) {
					if(!isRunning) {
						startStopWorker.setText("Pausiere Test");
						worker.continueWorker();
						statusText.setText("Test wird fortgesetzt ...");
					} else if (isRunning) {
						startStopWorker.setText("Test fortsetzen");
						worker.pauseWorker();
						statusText.setText("Test ist pausiert");
					}
				} else {
					startStopWorker.setText("Pausiere Test");
					worker.start();
					statusText.setText("Test l�uft ...");
				}
				isRunning = !isRunning;
				if (isRunning) {
					consolePanel.add(loadingLabel);
					consolePanel.remove(pauseLabel);
				} else {
					consolePanel.add(pauseLabel);
					consolePanel.remove(loadingLabel);
				}
				consolePanel.repaint();
			}
		});
		buttonPanel.add(startStopWorker);
		
		//	reset button
		JButton resetWorker = new JButton("Test zur�ckstellen");
		resetWorker.setPreferredSize(new Dimension(150, 40));
		resetWorker.addActionListener(new ActionListener() {
			@SuppressWarnings("static-access")
			public void actionPerformed(ActionEvent e) {
				if (worker.isAlive()) {
					worker.shutDownWorker();
					worker.continueWorker();
					try {
						worker.sleep(500);
					} catch (InterruptedException e1) {
						System.out.println("could not sleep for shutting down old worker");
					}
				}
				worker = new Worker();
				isRunning = false;
				startStopWorker.setText("Test starten");
				statusText.setText("Test zur�ckgestellt");
			}
		});
		buttonPanel.add(resetWorker);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
}