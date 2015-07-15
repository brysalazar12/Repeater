/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repeater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Admin
 */
public class Recorder {
	protected ArrayList<Map<String,String>> records;
	protected String recordName;

	public Recorder() {
		this.records = new ArrayList<>();
	}

	public void set(String key,String value) {
		Map<String,String> map = new HashMap<>();
		map.put(key, value);
		this.records.add(map);
	}
	
	public void save() {

	}
}
