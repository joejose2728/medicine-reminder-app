package edu.sjsu.medscan;

import java.util.ArrayList;
import java.util.List;

import edu.sjsu.medscan.core.model.Data;

public class Store {

	private static final List<Data> dataStore = new ArrayList<Data>();
	
	public static void addData(Data data){
		dataStore.add(data);
	}
	
	public static List<Data> getData(){
		 return dataStore;		
	}
}
