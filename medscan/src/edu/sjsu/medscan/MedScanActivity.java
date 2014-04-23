package edu.sjsu.medscan;

import java.io.File;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.CalendarContract.Events;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.sjsu.medscan.core.intf.ITextExtractor;
import edu.sjsu.medscan.core.model.Data;
import edu.sjsu.medscan.ocr.AbbyyCloudOCR;

public class MedScanActivity extends ListActivity {

	public static final String OCR_RESULT = "result";
	public static final int MEDIA_TYPE_IMAGE = 1;

	private Uri fileUri;
	private ProgressDialog progessDialog;
	private List<Data> medicines;
	private MedicineAdapter adapter;
	private Runnable viewMedicines;
	private ListView listView;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		medicines = new ArrayList<Data>();

		listView = getListView();
		adapter = new MedicineAdapter(this, R.layout.medicine_item, medicines);

		//listView.setAdapter(adapter);

		setListAdapter(adapter);
		populateList();
	}

	private void populateList(){
		//adapter.clear();
		viewMedicines = new Runnable() {
			public void run() {
				getData();	
			}
		};
		Thread thread = new Thread(null, viewMedicines, "View Medicines");
		thread.start();

		this.progessDialog = ProgressDialog.show(this, "Please wait...", "Retrieving medicine list...");
	}

	private Runnable result = new Runnable() {
		public void run() {
			int size = medicines.size();
			if (medicines != null && size > 0){
				for (int i=0; i<size;i++){
					adapter.add(medicines.get(i));
				}
			}
			progessDialog.dismiss();
			adapter.notifyDataSetChanged();
		}
	};

	protected void getData() {
		medicines = Store.getData();
		runOnUiThread(result);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId){
		case R.id.capture_image: showFileChooser();
		return true;
		default:
			return super.onOptionsItemSelected(item); 
		}
	}

	private class MedicineAdapter extends ArrayAdapter<Data> {

		private List<Data> items;
		private Context context;

		private void setDataList(List<Data> items){
			this.items = items;
		}

		public MedicineAdapter(Context context, int resource, List<Data> dataList) {
			super(context, resource, dataList);
			this.items = dataList;
			this.context = context;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			LayoutInflater linf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View row = linf.inflate(R.layout.medicine_item, parent, false);
			TextView tt = (TextView) row.findViewById(R.id.toptext);
			TextView bt = (TextView) row.findViewById(R.id.bottomtext);

			//if (position <= items.size() - 1) {
			Data d = items.get(position);
			if (d != null){
				tt.setText(d.getMedicine().getMedicine());
				bt.setText(d.getPatient2Medicine().getFrequencyOfIntake()
						+ " " + d.getPatient2Medicine().getQuantityPerIntake()
						+ " by " + d.getPatient2Medicine().getMode());
			}
			//}
			return row;
		}

	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)){
			File externalStorage = Environment.getExternalStoragePublicDirectory(
					Environment.DIRECTORY_PICTURES);

			if (!externalStorage.exists()){
				if (!externalStorage.mkdir())
					return null;
			}

			// Create a media file name
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
			File mediaFile = null;
			if (type == MEDIA_TYPE_IMAGE){
				mediaFile = new File(externalStorage.getPath() + File.separator +
						"Medscan_"+ timeStamp + ".jpg");
				Log.d("Media file", mediaFile.getName());
			} 
			return mediaFile;
		}
		return null;		
	}

	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type){
		return Uri.fromFile(getOutputMediaFile(type));
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		/*//Log.d("Request code", data.getDataString());
		Log.d("Result", resultCode+"");

		if (requestCode == 100) {
			if (resultCode == RESULT_OK) {
				// Image captured and saved to fileUri specified in the Intent
				Log.d("Result", data.getDataString());
				Toast.makeText(this, "Image saved to:\n" +
						data.getData(), Toast.LENGTH_LONG).show();
			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the image capture

			} else {
				// Image capture failed, advise user
			}
		}*/
		switch (requestCode) {
		case FILE_SELECT_CODE:
			if (resultCode == RESULT_OK) {
				// Get the Uri of the selected file 
				Uri uri = data.getData();
				Log.d("File Uri: ", uri.toString());
				// Get the path
				String path = "/";
				try {
					path = getPath(this, uri);
					Log.d("File Path: ", path);
					//Toast.makeText(this, path, Toast.LENGTH_LONG).show();
					
					if (path != null) {
						progessDialog = ProgressDialog.show(this, "Please wait...", "Processing...");
						new PerformOCRTask().execute(path); // call cloud service
					}
					else
						Toast.makeText(this, "Could not process the selected file.", Toast.LENGTH_LONG).show();
				} catch (URISyntaxException e) {
					Toast.makeText(this, "Could not process the selected file.", Toast.LENGTH_LONG).show();
				}
			}
			break;
		case OCR_CODE:
			adapter.clear();
			getData();
			adapter.setDataList(medicines);
			listView.invalidateViews();    		
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}


	private void extract() {
		this.progessDialog = ProgressDialog.show(this, "Please wait...", "Processing...");
		File pic = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES);

		String[] fileNames = pic.list();
		String img = pic.getAbsolutePath() + File.separator + fileNames[0];
		Log.d("Extract method - File path:", img);
		new PerformOCRTask().execute(img);
	}

	private void openCapture() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
		Log.d("File Uri", fileUri.getPath());
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		Log.d("Start Activity", "activity camera start...");
		startActivityForResult(intent, 100);
	}

	private static final int FILE_SELECT_CODE = 0;
	private static final int OCR_CODE = 100;

	private void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
		intent.setType("*/*"); 
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		try {
			startActivityForResult(
					Intent.createChooser(intent, "Select a File to Upload"),
					FILE_SELECT_CODE);
		} catch (android.content.ActivityNotFoundException ex) {
			// Potentially direct the user to the Market with a Dialog
			Toast.makeText(this, "Please install a File Manager.", 
					Toast.LENGTH_SHORT).show();
		}
	}

	public static String getPath(Context context, Uri uri) throws URISyntaxException {
		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = { "_data" };
			Cursor cursor = null;

			try {
				cursor = context.getContentResolver().query(uri, projection, null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					return cursor.getString(column_index);
				}
			} catch (Exception e) {
				// Eat it
			}
		}
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	} 

	private class PerformOCRTask extends AsyncTask<String, Void, String> {

		protected void onPostExecute(String result) {
			Intent newPatient = new Intent(MedScanActivity.this, NewPatientActivity.class);
			newPatient.putExtra(OCR_RESULT, result);
			// startActivity(newPatient);
			startActivityForResult(newPatient, OCR_CODE);
		}

		protected String doInBackground(String... params) {
			ITextExtractor extractor = new AbbyyCloudOCR(params[0]);
			String result = extractor.extract();
			progessDialog.dismiss();
			return result;
		}
	}
}


