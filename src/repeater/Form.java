/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repeater;

//import de.ksquared.system.keyboard.GlobalKeyListener;
//import de.ksquared.system.keyboard.KeyAdapter;
//import de.ksquared.system.keyboard.KeyEvent;
import java.awt.event.ActionEvent;
import java.io.File;
import java.lang.reflect.Method;
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

	public void show() {
		this.currentDir = System.getProperty("user.dir") + File.separatorChar;

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
	}

	protected void start(ActionEvent e) {
		this.btnStart.setEnabled(false);
		this.recording();
	}

	protected void stop(ActionEvent e) {
		
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
	
	public void keyPress(Integer keyCode) {
		if(keyCode == Form.SHIFT && !Form.isShiftPressed) {
			Form.isShiftPressed = true;
		} else if(keyCode != Form.SHIFT) {
			if(Form.isShiftPressed)
				System.out.println("BIG " + keyCode);
			else
				System.out.println("SMALL " + keyCode);
		}
		
	}
	
	public void keyRelease(Integer keyCode) {
		if(keyCode == Form.SHIFT)
			Form.isShiftPressed = false;
	}

	public void recording() {
		GlobalKeyListener keyListener = new GlobalKeyListener();
		Method press, release;
		try {
			press = this.getClass().getMethod("keyPress", Integer.class);
			release = this.getClass().getMethod("keyRelease", Integer.class);
			keyListener.addListener(press,release,this);
		} catch (NoSuchMethodException ex) {
			Logger.getLogger(Form.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SecurityException ex) {
			Logger.getLogger(Form.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		GlobalMouseListener mouseListener = new GlobalMouseListener();
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
