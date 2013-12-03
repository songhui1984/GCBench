package de.CollectorTest;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.DefaultPieDataset;

@SuppressWarnings("serial")
public class Benchmark extends JFrame{
	
	//	parameter
	static int overAllRounds = 1000;
	static int roundsToLive = 250; 
	static int arrLength = 100;
	
	boolean isRunning = false;
	Worker worker;
	static JTextArea console = new JTextArea("", 5, 22);
	static DefaultCategoryDataset data = new DefaultCategoryDataset();
	
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
		
		//	panel for console
		final JPanel consolePanel = new JPanel();
		consolePanel.setPreferredSize(new Dimension(100, 100));
		consolePanel.setLayout(new FlowLayout());
		this.add(consolePanel,BorderLayout.NORTH);
		
		//	loading and pause label
		Image image = new ImageIcon(Benchmark.class.getResource("assets/loading.gif")).getImage().getScaledInstance(100, 133, 0);
		ImageIcon icon = new ImageIcon(image);
		final JLabel loadingLabel = new JLabel(icon);
		loadingLabel.setPreferredSize(new Dimension(100, 100));
		image = new ImageIcon(Benchmark.class.getResource("assets/pause.gif")).getImage().getScaledInstance(100, 133, 0);
		icon = new ImageIcon(image);
		final JLabel pauseLabel = new JLabel(icon);
		pauseLabel.setPreferredSize(new Dimension(100, 100));
		
		//	status text if is running
		final JLabel statusText = new JLabel("Test nicht gestartet");
		statusText.setPreferredSize(new Dimension(150,20));
		
		console.setBackground(getBackground());
		console.setEditable(false);
		consolePanel.add(console);
		consolePanel.add(statusText);
		consolePanel.add(pauseLabel);
		
//		TODO SHIAAT data.addValue(value, rowKey, columnKey);
		
//		final double[][] data2 = new double[][] {
//	            {1.0, 4.0, 3.0, 5.0, 5.0, 7.0, 7.0, 8.0}
//	    };
//
//		data = (DefaultCategoryDataset) DatasetUtilities.createCategoryDataset("", "", data2);
		
		
		JFreeChart chart = ChartFactory.createAreaChart("", "Rounds to live", "Amount of Objects", data);
		chart.setBackgroundImageAlpha(0);
		chart.setBackgroundPaint(getBackground());
		
		
//		data.addValue(2.0, "", "");
		
		consolePanel.add(pauseLabel);
		ChartPanel panel = new ChartPanel(chart);
		this.add(panel);
		data.addValue(2.0, "", "2");
		
		//	startStop button
		final JButton startStopWorker = new JButton("Test starten");
		startStopWorker.setPreferredSize(new Dimension(170, 40));
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
					statusText.setText("Test läuft ...");
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
		JButton resetWorker = new JButton("Test zurückstellen");
		resetWorker.setPreferredSize(new Dimension(170, 40));
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
				statusText.setText("Test zurückgestellt");
				console.setText("");
				consolePanel.add(pauseLabel);
				consolePanel.remove(loadingLabel);
				consolePanel.repaint();
			}
		});
		buttonPanel.add(resetWorker);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
}