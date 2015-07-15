/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repeater;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.RowFilter;

/**
 *
 * @author Admin
 */
public class Config {
	protected File configFile;
	protected Map<String,String> config = new HashMap<>();
	
	public void setConfigFile(String configFile) {
		try {
			this.configFile = new File(configFile);
			if(!this.configFile.exists())
				this.configFile.createNewFile();
		} catch (IOException ex) {
			Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void read() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(this.configFile));
			String line;
			try {
				while((line = reader.readLine()) != null) {
					String[] param = line.split(":");
					this.config.put(param[0], param[1]);
				}
			} catch (IOException ex) {
				Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (FileNotFoundException ex) {
			Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void write() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(this.configFile));
			for(Entry<String,String> entry : this.config.entrySet()) {
				writer.write(entry.getKey() + ":" + entry.getValue());
			}
			writer.close();
		} catch (IOException ex) {
			Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public String get(String key) {
		return this.config.get(key);
	}

	public void set(String key, String value) {
		this.config.put(key, value);
	}
}