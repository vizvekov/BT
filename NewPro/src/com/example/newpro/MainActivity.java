package com.example.newpro;


import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;

public class MainActivity extends Activity {

	final String LOG_TAG = "myLogs";
	  private LinearLayout layout;
	  private String result;
	  int TaskColor;
	  OnClickListener radioListener;
	  DBHelper dbHelper = new DBHelper(this);;
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
		  init_main_layout();
	  }
	  
	  private void init_main_layout() {
		  setContentView(R.layout.activity_main);
		  layout = (LinearLayout) findViewById(R.id.tesks);
		  TextView loger = (TextView) findViewById(R.id.textView1);
		  SQLiteDatabase db = dbHelper.getWritableDatabase();
		  Log.d(LOG_TAG, "--- Rows in mytable: ---");
		  Cursor c = db.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);
		  if (c.moveToFirst()) {
		        // определяем номера столбцов по имени в выборке
		        int idColIndex = c.getColumnIndex(DBHelper.ID);
		        int taskColIndex = c.getColumnIndex(DBHelper.TASK);
		        int dateColIndex = c.getColumnIndex(DBHelper.DATE);
		        int timeColIndex = c.getColumnIndex(DBHelper.TIME);
		        int colorConIndex = c.getColumnIndex(DBHelper.COLOR);
		        do {
		          // получаем значения по номерам столбцов и пишем все в лог
		          Log.d(LOG_TAG,
		        		  DBHelper.ID + " = " + c.getInt(idColIndex) + 
		              ", " + DBHelper.TASK + " = " + c.getString(taskColIndex) + 
		              ", " + DBHelper.DATE + " = " + c.getString(dateColIndex) +
		              ", " + DBHelper.TIME + " = " + c.getString(timeColIndex) +
		              ", " + DBHelper.COLOR + " = " + c.getString(colorConIndex));
		          additem_to_main("Task: " +  c.getString(taskColIndex) + " data: " + c.getString(dateColIndex) + " in time: " + c.getString(timeColIndex), c.getInt(colorConIndex));
		        } while (c.moveToNext());
		      } else{
		    	loger.setText("0 rows");
		        Log.d(LOG_TAG, "0 rows");
		      }
		      c.close();
		  
		      

		  Button startButton = (Button) findViewById(R.id.startbutton);
		  startButton.setOnClickListener(new View.OnClickListener() {
			  public void onClick(View view) {
				  try {
					  showdilog();
				  } catch (NumberFormatException e) {
				  }
			  }
		  });


	  }
	  private void switch_layout (){
		  setContentView(R.layout.dilog);
		  TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker1);
		  timePicker.setIs24HourView(true);
		  
		  radioListener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					RadioButton rb = (RadioButton)v;
					switch (rb.getId()) {
					case R.id.radioRed: TaskColor = Color.parseColor("#ff0000");
					    break;
					case R.id.radioGreen: TaskColor = Color.parseColor("#0000ff");
					    break;
					case R.id.radioBlue: TaskColor = Color.parseColor("#00ff00");
					default:
						break;
					}
				}
			};

			RadioButton rb_red = (RadioButton)findViewById(R.id.radioRed);
			rb_red.setOnClickListener(radioListener);

			RadioButton rb_green = (RadioButton)findViewById(R.id.radioGreen);
			rb_green.setOnClickListener(radioListener);

			RadioButton rb_blue = (RadioButton)findViewById(R.id.radioBlue);
			rb_blue.setOnClickListener(radioListener);
		  
			Button dtButton = (Button) findViewById(R.id.dts);
			dtButton.setOnClickListener(new View.OnClickListener() {
		    	  public void onClick(View view) {
		    		  try {
		    			  DatePicker dat = (DatePicker) findViewById(R.id.datePicker1);
		    			  String datime_tmp = "" + dat.getYear() + "-" + dat.getMonth() + "-" + dat.getDayOfMonth();
		    			  TimePicker tim = (TimePicker) findViewById(R.id.timePicker1);
		    			  ViewGroup v = (ViewGroup) tim.getChildAt(0);
		    			  ViewGroup numberPicker1 = (ViewGroup) v.getChildAt(0);
		    			  ViewGroup numberPicker2 = (ViewGroup) v.getChildAt(1);
		    			  String hours = ((EditText) numberPicker1.getChildAt(1)).getText().toString();
		    			  String mins = ((EditText) numberPicker2.getChildAt(1)).getText().toString();
		    			  Log.d(LOG_TAG, "--- Insert in mytable: ---");
		    			  ContentValues cv = new ContentValues();
		    			  SQLiteDatabase db = dbHelper.getWritableDatabase();
		    			  cv.put(DBHelper.TASK, result);
		    			  cv.put(DBHelper.DATE, datime_tmp);
		    			  cv.put(DBHelper.TIME, "" + hours + ":" + mins);
		    			  cv.put(DBHelper.COLOR, TaskColor);
		    			  long rowID = db.insert(DBHelper.TABLE_NAME, null, cv);
		    			  Log.d(LOG_TAG, "row inserted, ID = " + rowID);
		    			  init_main_layout();		    			  
		    		  } catch (NumberFormatException e){
		    			  // method ignores invalid (non-integer) input and waits
		    			  // for something it can use
		    		  }
		    	  }
		    });
	  }
	  
	  private void additem_to_main (String text, int color){
		  final TextView txt = new TextView(this);
	      txt.setText("" + text); 
	      txt.setBackgroundColor(color);
	      layout.addView(txt);
	  }
	  
	  private void showdilog (){
		  AlertDialog.Builder alert = new AlertDialog.Builder(this);

		  alert.setTitle("Заголовок");
		  alert.setMessage("Сообщение");
		  final EditText input = new EditText(this);
		  alert.setView(input);
		  alert.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		     result = input.getText().toString();
		     switch_layout();
		    }
		  });

		  alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int whichButton) {
		      // Если отменили.
		    }
		  });

		  alert.show();
	  }

	    class DBHelper extends SQLiteOpenHelper {
	    	private static final int DB_VERSION = 2;
	    	public static final String ID = "id";
	    	public static final String TASK = "task";
	    	public static final String DATE = "date";
	    	public static final String TIME = "time";
	    	public static final String COLOR = "colors";
	    	public static final String TABLE_NAME = "users";
	        final String CREATE_TABLE = "CREATE TABLE "
	        				+ TABLE_NAME + " (" + ID +  " integer primary key autoincrement, "
	        				+ TASK + " TEXT, " 
	        				+ DATE + " TEXT, "
	        				+ TIME + " TEXT, "
	        				+ COLOR + "INT "
	        				+ ")";
	        final static String DB_NAME = "mySuperDB.db";
	        Context mContext;
	        
	        public DBHelper(Context context){
	        	super(context, DB_NAME, null, DB_VERSION);
	        	mContext = context;
	        }
	        
	         @Override
	         public void onCreate(SQLiteDatabase db) {
	        	 db.execSQL(CREATE_TABLE);
	         }
	        
	         @Override
	         public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	         //проверяете какая версия сейчас и делаете апдейт
	        	 db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	        	 onCreate(db);
	         }
	    }
}

