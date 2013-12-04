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
import org.jfree.data.xy.DefaultIntervalXYDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.ui.RectangleInsets;

@SuppressWarnings("serial")
public class Benchmark extends JFrame{
	
	//	parameter
	static int overAllRounds = 50;
	static int roundsToLive = 250; 
	static int arrLength = 3000;
	
	static boolean isRunning = false;
	Worker worker;
	static JTextArea console = new JTextArea("", 5, 22);
	static DefaultCategoryDataset data = new DefaultCategoryDataset();
//	static DefaultIntervalXYDataset barData = new DefaultIntervalXYDataset();
	static JButton startStopWorker;
	static JLabel statusText;
	static JPanel consolePanel;
	static JLabel loadingLabel;
	static JLabel pauseLabel;
	
	public static void main(String[] args) {
		
		new Benchmark();
	}
	
	public Benchmark() {
		//	create new worker
		worker  = new Worker();
		
		//	window
		this.setSize(new Dimension(640, 900));
		
		//	panel for buttons
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		this.add(buttonPanel,BorderLayout.SOUTH);
		
		//	panel for console
		consolePanel = new JPanel();
		consolePanel.setPreferredSize(new Dimension(100, 100));
		consolePanel.setLayout(new FlowLayout());
		this.add(consolePanel,BorderLayout.NORTH);
		
		//	loading and pause label
		Image image = new ImageIcon(Benchmark.class.getResource("assets/loading.gif")).getImage().getScaledInstance(100, 133, 0);
		ImageIcon icon = new ImageIcon(image);
		loadingLabel = new JLabel(icon);
		loadingLabel.setPreferredSize(new Dimension(100, 100));
		image = new ImageIcon(Benchmark.class.getResource("assets/pause.gif")).getImage().getScaledInstance(100, 133, 0);
		icon = new ImageIcon(image);
		pauseLabel = new JLabel(icon);
		pauseLabel.setPreferredSize(new Dimension(100, 100));
		
		//	status text if is running
		statusText = new JLabel("Test nicht gestartet");
		statusText.setPreferredSize(new Dimension(150,20));
		
		console.setBackground(getBackground());
		console.setEditable(false);
		consolePanel.add(console);
		consolePanel.add(statusText);
		consolePanel.add(pauseLabel);
		
		JFreeChart chart = ChartFactory.createAreaChart("", "Rounds to live", "Amount of Objects", data);
		chart.setBackgroundImageAlpha(0);
		chart.setBackgroundPaint(getBackground());
		chart.setBorderVisible(false);
		chart.setBorderPaint(getBackground());
//		chart.setPadding(new RectangleInsets(100, 0, 100, 0));
		
		consolePanel.add(pauseLabel);
		ChartPanel panel = new ChartPanel(chart);
//		ChartPanel barPanel = new ChartPanel(barChart);
		this.add(panel);
//		this.add(barPanel);
		
		//	startStop button
		startStopWorker = new JButton("Test starten");
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
					worker = null;
					worker = new Worker();
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
				data.clear();
			}
		});
		buttonPanel.add(resetWorker);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
}