package edu.sjsu.medscan.ocr;
import java.util.Vector;

import edu.sjsu.medscan.core.intf.ITextExtractor;

public class AbbyyCloudOCR implements ITextExtractor {

	private Client restClient;
	private Vector<String> args;
	
	public AbbyyCloudOCR(String imagePath){
		restClient = new Client();
		args = new Vector<String>();
		args.add(imagePath);
	}
	
	public String extract() {
		try {
          return performRecognition();
		}
		catch (Exception e){
			return "Error occured.";
		}
	}

	/**
	 * Parse command line and recognize one or more documents.
	 */
	private String performRecognition()
			throws Exception {
		String language = extractRecognitionLanguage();
		ProcessingSettings.OutputFormat outputFormat = ProcessingSettings.OutputFormat.txt;

		ProcessingSettings settings = new ProcessingSettings();
		settings.setLanguage(language);
		settings.setOutputFormat(outputFormat);

		Task task = null;
		if (args.size() == 1) {
			task = restClient.processImage(args.elementAt(0), settings);

		} else if (args.size() > 1) {

			for (int i = 0; i < args.size(); i++) {
				String taskId = null;
				if (task != null) {
					taskId = task.Id;
				}

				Task result = restClient.submitImage(args.elementAt(i),
						taskId);
				if (task == null) {
					task = result;
				}
			}
			task = restClient.processDocument(task.Id, settings);
		} 
		return waitAndDownloadResult(task);
	}


	/** 
	 * Wait until task processing finishes
	 */
	private Task waitForCompletion(Task task) throws Exception {
		while (task.isTaskActive()) {
			Thread.sleep(2000);
			task = restClient.getTaskStatus(task.Id);
		}
		return task;
	}
	
	/**
	 * Wait until task processing finishes and download result.
	 */
	private String waitAndDownloadResult(Task task)
			throws Exception {
		task = waitForCompletion(task);

		if (task.Status == Task.TaskStatus.Completed) {
		//	System.out.println("Downloading..");
			return restClient.downloadResult(task);
			//System.out.println("Ready");
		} else if (task.Status == Task.TaskStatus.NotEnoughCredits) {
		//	System.out.println("Not enough credits to process document. "
			//		+ "Please add more pages to your application's account.");
			return "Not enough Credits.";
		} else {
			return "Error occured.";
		}

	}

	private String extractParameterValue(String parameterName) {
		String prefix = "--" + parameterName + "=";
		for (int i = 0; i < args.size(); i++) {
			String arg = args.elementAt(i);
			if (arg.startsWith(prefix)) {
				String value = arg.substring(prefix.length());
				args.remove(i);
				return value;
			}
		}
		return null;
	}

	private String extractRecognitionLanguage() {
		String lang = extractParameterValue("lang");
		if (lang != null) {
			return lang;
		}
		return "English";
	}

}
