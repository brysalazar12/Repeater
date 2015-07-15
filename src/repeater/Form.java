/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repeater;

//import de.ksquared.system.keyboard.GlobalKeyListener;
//import de.ksquared.system.keyboard.KeyAdapter;
//import de.ksquared.system.keyboard.KeyEvent;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;

/**
 *
 * @author Admin
 */
public class Form {
	protected JButton btnStart;
	protected JButton btnStop;
	protected JButton btnPlay;
	protected JComboBox recordList;
	protected static boolean isRecording = false;
	protected Thread thread;
	protected String currentDir;
	protected final String ext = "rec";
	protected static final int SHIFT = 160;
	protected static final int CTRL = 162;
	protected static final int ALT = 164;
	protected static boolean isShiftPressed = false;
	protected static boolean isCtrlPressed = false;
	protected static boolean isAltPressed = false;
	protected ArrayList<String> records;
	protected long startTime;
	protected long lapsedTime;

	public void show() {
		this.currentDir = System.getProperty("user.dir") + File.separatorChar;
		this.records = new ArrayList<>();

		JFrame frame = new JFrame("Repeater");
		frame.setLayout(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(536, 53);
		frame.setResizable(false);
		
		this.btnStart = new JButton("Start Recording");
		this.btnStart.setBounds(0, 0, 140, 25);
		this.btnStart.addActionListener((ActionEvent e) -> {
			this.start(e);
		});

		this.btnStop = new JButton("Stop Recording");
		this.btnStop.setBounds(140, 0, 140, 25);
		this.btnStop.addActionListener((ActionEvent e) -> {
			this.stop(e);
		});

		this.btnPlay = new JButton("Play");
		this.btnPlay.setBounds(280, 0, 100, 25);
		this.btnPlay.addActionListener((ActionEvent e) -> {
			this.play(e);
		});

		this.recordList = new JComboBox();
		this.recordList.addItem("List of records");
		this.readAllRecords();
		this.recordList.setBounds(380, 0, 150, 25);

		frame.add(this.btnStart);
		frame.add(this.btnStop);
		frame.add(this.btnPlay);
		frame.add(this.recordList);
		frame.setVisible(true);
	}

	

	protected void play(ActionEvent e) {
		System.out.println(this.recordList.getSelectedItem());
		try {
			Robot robot = new Robot();
			File file = new File(this.currentDir + "testing" + "." + this.ext);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			String[] rec;
			String[] points;
			while((line = reader.readLine()) != null) {
				rec = line.split(":");
				if(rec[0].equals("delay")) {
					robot.delay(Integer.parseInt(rec[1]));
				} else if(rec[0].equals("mouse")) {
					points = rec[1].split(",");
					robot.mouseMove(Integer.parseInt(points[0]), Integer.parseInt(points[1]));
					robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
					robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
				} else if(rec[0].equals("key")) {
					if(rec[1].equals("13")) {
						robot.keyPress(KeyEvent.VK_ENTER);
						robot.keyRelease(KeyEvent.VK_ENTER);
					} else {
						if(rec[1].startsWith("Big")) {
							rec[1] = rec[1].substring(4);
							robot.keyPress(KeyEvent.VK_SHIFT);
							robot.keyPress(Integer.parseInt(rec[1]));
							robot.keyRelease(Integer.parseInt(rec[1]));
							robot.keyRelease(KeyEvent.VK_SHIFT);
						} else {
							robot.keyPress(Integer.parseInt(rec[1]));
							robot.keyRelease(Integer.parseInt(rec[1]));
						}
					}

				}
			}
		} catch (AWTException ex) {
			Logger.getLogger(Form.class.getName()).log(Level.SEVERE, null, ex);
		} catch (FileNotFoundException ex) {
			Logger.getLogger(Form.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(Form.class.getName()).log(Level.SEVERE, null, ex);
		}
		
	}

	protected void start(ActionEvent e) {
		this.btnStart.setEnabled(false);
		this.startTime = System.currentTimeMillis();
		this.recording();
	}

	protected void stop(ActionEvent e) {
		this.recordDelay();
		this.saveRec();
	}

	protected void saveRec() {
		try {
			File file = new File(this.currentDir + "testing" + "." + this.ext);
			file.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			for (String record : this.records) {
				writer.write(record);
				writer.newLine();
			}
			writer.close();
		} catch (IOException ex) {
			Logger.getLogger(Form.class.getName()).log(Level.SEVERE, null, ex);
		}
		
	}

	protected void readAllRecords() {
		File folder = new File(this.currentDir);
		File[] files = folder.listFiles();
		for(File file : files) {
			if(file.isFile()) {
				String name = file.getName();
				int index = name.lastIndexOf(".");
				if(index > 0) {
					if(name.substring(index + 1).equals(this.ext)) {
						this.recordList.addItem(name.substring(0, index));
					}
				}
			}
		}
	}
	
	public void recordDelay() {
		this.lapsedTime = System.currentTimeMillis() - this.startTime;
		this.records.add("delay:" + String.valueOf(this.lapsedTime));
		this.startTime = System.currentTimeMillis();
	}

	public void recordKey(Integer key) {
		this.records.add("key:" + key.toString());
	}
	public void recordShiftKey(Integer key) {
		this.records.add("key:Big " + key.toString());
	}

	public void recordMouse(Integer x, Integer y) {
		this.records.add("mouse:" + x.toString() + "," + y.toString());
	}
	
	public void keyPress(Integer keyCode) {
		this.recordDelay();
		if(keyCode == Form.SHIFT && !Form.isShiftPressed) {
			Form.isShiftPressed = true;
		} else if(keyCode != Form.SHIFT) {
			if(Form.isShiftPressed) {
				this.recordShiftKey(keyCode);
			} else {
				this.recordKey(keyCode);
			}
			
		}
		
	}
	
	public void keyRelease(Integer keyCode) {
		if(keyCode == Form.SHIFT)
			Form.isShiftPressed = false;
	}

	public void leftMouseClick(Integer x, Integer y) {
		this.recordDelay();
//		System.out.println("x:" + x + " y:" + y);
		this.recordMouse(x, y);
	}

	public void recording() {
		GlobalKeyListener keyListener = new GlobalKeyListener();
		GlobalMouseListener mouseListener = new GlobalMouseListener();
		Method press, release, mouseClick;
		try {
			press = this.getClass().getMethod("keyPress", Integer.class);
			release = this.getClass().getMethod("keyRelease", Integer.class);
			mouseClick = this.getClass().getMethod("leftMouseClick", Integer.class,Integer.class);
			keyListener.addListener(press,release,this);
			mouseListener.addListener(mouseClick, this);
		} catch (NoSuchMethodException ex) {
			Logger.getLogger(Form.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SecurityException ex) {
			Logger.getLogger(Form.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		keyListener.start();
		mouseListener.start();
		
//		new GlobalKeyListener().addKeyListener(new KeyAdapter() {
//			@Override public void keyPressed(KeyEvent event) { 
////				System.out.println(event);
//				if(event.getVirtualKeyCode() == Form.SHIFT && !Form.isShiftPressed) {
//					Form.isShiftPressed = true;
//				} else if(event.getVirtualKeyCode() != Form.SHIFT) {
//					if(Form.isShiftPressed)
//						System.out.println("BIG "+(char)event.getVirtualKeyCode() + " " + event.getVirtualKeyCode());
//					else
//						System.out.println("SMALL "+(char)event.getVirtualKeyCode() + " " + event.getVirtualKeyCode());
//				}
//			}
//
//			@Override public void keyReleased(KeyEvent event) {
//				if(event.getVirtualKeyCode() == Form.SHIFT)
//					Form.isShiftPressed = false;
//				
////				System.out.println((char)event.getVirtualKeyCode() + " " + event.getVirtualKeyCode());
//
////				if(event.getVirtualKeyCode()==KeyEvent.VK_ADD
////				&& event.isCtrlPressed())
////					System.out.println("CTRL+ADD was just released (CTRL is still pressed)");
//			}
//		});
	}	
}
