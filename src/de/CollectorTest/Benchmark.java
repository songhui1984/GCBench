package de.CollectorTest;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.Range;
import org.jfree.data.category.DefaultCategoryDataset;

@SuppressWarnings("serial")
public class Benchmark extends JFrame{
	
	//	parameter
	static int overAllRounds = 1000;
	static int roundsToLive = 250; 
	static int arrLength = 250;
	static short objectSize = 1024;
	static short oldAmount = 25;
	
	static boolean isRunning = false;
	static JTextArea console = new JTextArea("", 5, 22);
	static DefaultCategoryDataset data = new DefaultCategoryDataset();
	static JButton startStopWorker;
	static JLabel statusText;
	static JPanel consolePanel;
	static JLabel loadingLabel;
	static JLabel pauseLabel;
	static JSlider overAllRoundsSlider  = null;
	static JSlider roundsToLiveSlider  = null;
	static JSlider arrLengthSlider  = null;
	static JSlider objectSizeSlider  = null;
	static JSlider oldAmountSlider  = null;
	Worker worker;
	
	public static void main(String[] args) {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			
		} catch (InstantiationException e) {
			
		} catch (IllegalAccessException e) {
			
		} catch (UnsupportedLookAndFeelException e) {
			
		}
		
		new Benchmark();
		
	}
	
	public Benchmark() {
		
		super("Garbage Collector Benchmark");
		
		//	create new worker
		worker  = new Worker();
		
		//	window
		this.setSize(new Dimension(680, 780));
		this.setResizable(false);
		
		//	panel for buttons
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		this.add(buttonPanel,BorderLayout.SOUTH);
		
		//	panel for console
		consolePanel = new JPanel();
		consolePanel.setPreferredSize(new Dimension(100, 120));
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
		
		//	console text
		console.setBackground(getBackground());
		console.setEditable(false);
		consolePanel.add(console);
		consolePanel.add(statusText);
		consolePanel.add(pauseLabel);
		
		//	population chart
		final JFreeChart chart = ChartFactory.createAreaChart("", "Runden zu leben", "Anzahl der Objekte", data);
		chart.setBackgroundPaint(getBackground());
		chart.removeLegend();
		CategoryAxis xAchse = chart.getCategoryPlot().getDomainAxis();
		xAchse.setTickLabelsVisible(false);
		ValueAxis yAchse = chart.getCategoryPlot().getRangeAxis();
		yAchse.setRange(new Range(0, 125), true, false);
		
		
		consolePanel.add(pauseLabel);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(650, 200));
		JPanel controlPanel = new JPanel();
		controlPanel.setPreferredSize(new Dimension(100, 100));
		controlPanel.setLayout(new FlowLayout());
		
		overAllRoundsSlider = new JSlider(JSlider.HORIZONTAL, 100, 10000, 1000);
		overAllRoundsSlider.setBorder(BorderFactory.createTitledBorder("Gesamte Rundenanzahl:    1000"));
		overAllRoundsSlider.setPreferredSize(new Dimension(600, 65));
		overAllRoundsSlider.setMajorTickSpacing(1100);
		overAllRoundsSlider.setMinorTickSpacing(110);
		overAllRoundsSlider.setPaintTicks(true);
		overAllRoundsSlider.setPaintLabels(true);
		overAllRoundsSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				overAllRounds = overAllRoundsSlider.getValue();
				overAllRoundsSlider.setBorder(BorderFactory.createTitledBorder("Gesamte Rundenanzahl:    " + overAllRoundsSlider.getValue()));
				ValueAxis yAchse = chart.getCategoryPlot().getRangeAxis();
				yAchse.setRange(new Range(0, arrLength*0.5), false, false);
				yAchse.setAutoRange(false);
			}
		});
		
		roundsToLiveSlider = new JSlider(JSlider.HORIZONTAL, 10, 500, 250);
		roundsToLiveSlider.setBorder(BorderFactory.createTitledBorder("Maximale Rundeanzahl zu leben pro Objekt:    250"));
		roundsToLiveSlider.setPreferredSize(new Dimension(600, 65));
		roundsToLiveSlider.setMajorTickSpacing(35);
		roundsToLiveSlider.setMinorTickSpacing(7);
		roundsToLiveSlider.setPaintTicks(true);
		roundsToLiveSlider.setPaintLabels(true);
		roundsToLiveSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				roundsToLive = roundsToLiveSlider.getValue();
				roundsToLiveSlider.setBorder(BorderFactory.createTitledBorder("Maximale Rundeanzahl zu leben pro Objekt:    " + roundsToLiveSlider.getValue()));
				ValueAxis yAchse = chart.getCategoryPlot().getRangeAxis();
				yAchse.setRange(new Range(0, arrLength*0.5), false, false);
				yAchse.setAutoRange(false);
			}
		});
		
		arrLengthSlider = new JSlider(JSlider.HORIZONTAL, 10, 1000, 250);
		arrLengthSlider.setBorder(BorderFactory.createTitledBorder("Anzahl der Objekte:    250"));
		arrLengthSlider.setPreferredSize(new Dimension(600, 65));
		arrLengthSlider.setMajorTickSpacing(110);
		arrLengthSlider.setMinorTickSpacing(11);
		arrLengthSlider.setPaintTicks(true);
		arrLengthSlider.setPaintLabels(true);
		arrLengthSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				arrLength = arrLengthSlider.getValue();
				arrLengthSlider.setBorder(BorderFactory.createTitledBorder("Anzahl der Objekte:    " + arrLengthSlider.getValue()));
				ValueAxis yAchse = chart.getCategoryPlot().getRangeAxis();
				yAchse.setRange(new Range(0, arrLength*0.5), false, false);
				yAchse.setAutoRange(false);
			}
		});
		
		objectSizeSlider = new JSlider(JSlider.HORIZONTAL, 128, 8192, 1024);
		objectSizeSlider.setBorder(BorderFactory.createTitledBorder("Maximale Größe eines Objektes (in Kilobyte):    1024"));
		objectSizeSlider.setPreferredSize(new Dimension(600, 75));
		objectSizeSlider.setMajorTickSpacing(1008);
		objectSizeSlider.setToolTipText("Setzt die maximale Größe welches ein Objekt sein kann.");
		objectSizeSlider.setMinorTickSpacing(63);
		objectSizeSlider.setPaintTicks(true);
		objectSizeSlider.setPaintLabels(true);
		objectSizeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				objectSize = (short) objectSizeSlider.getValue();
				objectSizeSlider.setBorder(BorderFactory.createTitledBorder("Maximale Größe eines Objektes (in Kilobyte):    " + objectSizeSlider.getValue()));
				ValueAxis yAchse = chart.getCategoryPlot().getRangeAxis();
				yAchse.setRange(new Range(0, arrLength*0.5), false, false);
				yAchse.setAutoRange(false);
			}
		});
		
		oldAmountSlider = new JSlider(JSlider.HORIZONTAL, 5, 50, 25);
		oldAmountSlider.setBorder(BorderFactory.createTitledBorder("Prozentsatz an langlebigen Objekten:    25%"));
		oldAmountSlider.setPreferredSize(new Dimension(600, 75));
		oldAmountSlider.setMajorTickSpacing(5);
		oldAmountSlider.setToolTipText("Setzt den Prozentsatz an langlebigen Objekten.");
		oldAmountSlider.setMinorTickSpacing(1);
		oldAmountSlider.setPaintTicks(true);
		oldAmountSlider.setPaintLabels(true);
		oldAmountSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				oldAmount = (short) (oldAmountSlider.getValue());
				oldAmountSlider.setBorder(BorderFactory.createTitledBorder("Prozentsatz an langlebigen Objekten:    " + oldAmountSlider.getValue() + "%"));
				ValueAxis yAchse = chart.getCategoryPlot().getRangeAxis();
				yAchse.setRange(new Range(0, arrLength*0.5), false, false);
				yAchse.setAutoRange(false);
			}
		});
		
		controlPanel.add(chartPanel);
		controlPanel.add(overAllRoundsSlider);
		controlPanel.add(roundsToLiveSlider);
		controlPanel.add(arrLengthSlider);
		controlPanel.add(objectSizeSlider);
		controlPanel.add(oldAmountSlider);
		this.add(controlPanel);
		
		//	startStop button
		startStopWorker = new JButton("Test starten");
		startStopWorker.setPreferredSize(new Dimension(170, 40));
		startStopWorker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setSlidersEditable(false);
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
			@SuppressWarnings({ "static-access" })
			public void actionPerformed(ActionEvent e) {
				setSlidersEditable(true);
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
	
	@SuppressWarnings("deprecation")
	public static void setSlidersEditable(boolean state) {
		overAllRoundsSlider.enable(state);
		overAllRoundsSlider.repaint();
		roundsToLiveSlider.enable(state);
		roundsToLiveSlider.repaint();
		arrLengthSlider.enable(state);
		arrLengthSlider.repaint();
		objectSizeSlider.enable(state);
		objectSizeSlider.repaint();
		oldAmountSlider.enable(state);
		oldAmountSlider.repaint();
	}
	
}