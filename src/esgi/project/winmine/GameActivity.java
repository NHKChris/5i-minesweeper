package esgi.project.winmine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

class GameState {
	public static final int START = 0;
	public static final int RESUME = 1;
	public static final int PAUSE = 2;
	public static final int END = 3;
}

public class GameActivity extends Activity {	
	private String level;
	private String currentPrefKey;
	
	private Grid grid;
	private int gridHeight;
	private int gridWidth;
	private Cell[][] gridCells;
	private GridView gridView;
	private int gameState;
//	@SuppressWarnings("unused")
	private int numberOfFlag;
	private int numberOfCellsDisplayed;
	private int numberOfDisplayedNeeded;
	
	private boolean startTimer = false;
	private TextView timerValue;	
	private Handler customHandler = new Handler();

	private long startTime;
	private long timeInMilliseconds;
	private long timeSwapBuff;
	private long updatedTime;
	
	private TextView flagsValue;
	private TextView highScore;
	
	private PopupWindow popupPause;
	private PopupWindow popupEnd;
	
	private static Integer[] gridImages = {
		R.drawable.cell_0,
		R.drawable.cell_1,
		R.drawable.cell_2,
		R.drawable.cell_3,
		R.drawable.cell_4,
		R.drawable.cell_5,
		R.drawable.cell_6,
		R.drawable.cell_7,
		R.drawable.cell_8,
		R.drawable.cell_9,
		R.drawable.cell_mine,
		R.drawable.cell_flag,
		R.drawable.cell_hidden
	};
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);  
       
        Intent intent = getIntent();
        level = intent.getStringExtra("level");
        currentPrefKey = Utility.KEY + level;
        
        timerValue = (TextView) findViewById(R.id.gameTimer);
        flagsValue = (TextView) findViewById(R.id.gameFlag);     
        highScore = (TextView) findViewById(R.id.gameHighScore);
        
        this.initGame(level);
    }

	public void initGame(String value) {    	
    	grid = new Grid(Integer.parseInt(value));
    	grid.InitGrid();
    	gridHeight = grid.getHeight();
		gridWidth = grid.getWidth();
		gridCells = grid.getGrid();
		numberOfFlag = 0;
		numberOfCellsDisplayed = 0;
		numberOfDisplayedNeeded = gridHeight * gridWidth - grid.getNumberOfBombs();

    	setGridView();
    	
    	startTimer = false;
    	startTime = 0L;
    	timeInMilliseconds = 0L;
    	timeSwapBuff = 0L;
    	updatedTime = 0L;
        
        long highScoreLong = getScore();
        String highScoreString = (highScoreLong > 0) ? Utility.timerToString(highScoreLong) : "00:00:000";       

        highScore.setText("Best time : " + highScoreString);
        flagsValue.setText("Flags: " + numberOfFlag + "/" + grid.getNumberOfBombs());
    	
    	gameState = GameState.START;
	}
	
	public void setGridView() {
		gridView = (GridView) findViewById(R.id.gameGrid);
		gridView.setNumColumns(gridHeight);
		gridView.setAdapter(new ImageAdapter(this, gridHeight * gridWidth));
		LayoutParams params = gridView.getLayoutParams();
		params.width = gridWidth * ((level.equals("1")) ? 80 : 50);		
		gridView.setLayoutParams(params);
		
		gridView.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	if(gameState == GameState.START) {	        		
		    		Cell cellClicked = getCellClicked(position);
		    		checkCell(cellClicked);
		    		checkGame();
	        	}
	        }
	    });
		
		gridView.setOnItemLongClickListener(new OnItemLongClickListener() {
	        public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
	        	if(gameState == GameState.START) {
		        	setFlag(position);
	        	}
	        	return true;
	        }
	    });
	}
	
	public Cell getCellClicked(int position) {
		if(!startTimer) {
			startTimer = true;
			startTime = SystemClock.uptimeMillis();
			customHandler.postDelayed(updateTimerThread, 0);
		}
		
		int posX = 0;
		int posY = 0;
		
		if(position>0) {
			posX = position/gridHeight;
			posY = position%gridHeight;
		}
		
		return gridCells[posX][posY];
	}
	
	public void checkCell(Cell cell) {
		if(!cell.IsDisplayed() && !cell.IsFlag()) {
			int value = cell.GetValue();
			displayCell(cell);
			Log.v("Test", "Coucou5");
			
			if(value < 0) {
				endGame(false);
			} else if(value == 0) {
				checkNeighborCells(cell);
			}		
		}	  
	}
	
	public int getPosition(Cell cell) {
		int[] pos = cell.GetPos();
		return pos[0] * gridHeight + pos[1];
	}
	
	public void checkNeighborCells(Cell cell) {
		int modifierW = -1;
		int modifierH = -1;
		
		int posX = cell.GetX();
		int posY = cell.GetY();
		
		displayCell(cell);
		
		for (int i = 0; i < 3; i++) {		
			modifierH = -1;
			for(int j = 0; j < 3; j++) {
				
				int testW = posX + modifierW;
				int testH = posY + modifierH;
				
				if(testW > -1 && testW < gridWidth && testH > -1 && testH < gridHeight) {
					if(posX != testW || posY != testH) {
						Cell currentCell = gridCells[testW][testH];
						if(!currentCell.IsDisplayed() && !currentCell.IsBomb()) {
							displayCell(currentCell);

							if(currentCell.GetValue() == 0) {
								checkNeighborCells(currentCell);
							}
						}
					}					
				} 
				modifierH++;
			}
			modifierW++;
		}
 	}
	
	public void displayCell(Cell cell) {
		if(!cell.IsDisplayed()) {
			int position = getPosition(cell);
			int value = cell.GetValue();			
			int drawIndex = R.drawable.cell_mine;
			if(!cell.IsBomb()) {
				drawIndex = gridImages[value];
				cell.SetDisplayed();
				numberOfCellsDisplayed++;
			}
			ImageView imageView = (ImageView)gridView.getChildAt(position);	
			imageView.setImageResource(drawIndex);
		}
	}
	
	public void setFlag(int position) {
		Cell cell = getCellClicked(position);
		if(!cell.IsDisplayed()) {
			boolean isFlag = !cell.IsFlag();
			cell.SetFlag(isFlag);
			
			int draw = (isFlag) ? R.drawable.cell_flag : R.drawable.cell_hidden;
			numberOfFlag += (isFlag) ? 1 : -1;
			
			ImageView imageView = (ImageView)gridView.getChildAt(position);
			imageView.setImageResource(draw);
		}
		flagsValue.setText("Flags: " + numberOfFlag + "/" + grid.getNumberOfBombs());
	}
	
	private Runnable updateTimerThread = new Runnable() {
		public void run() {
			timeInMilliseconds = SystemClock.uptimeMillis() - startTime;			
			updatedTime = timeSwapBuff + timeInMilliseconds;
			timerValue.setText(Utility.timerToString(updatedTime));
			customHandler.postDelayed(this, 0);
		}
	};
	
	public void checkGame() {
		if(numberOfDisplayedNeeded == numberOfCellsDisplayed) {
			endGame(true);
		}
	}	
	
	public void pauseGame(View v) {
		if(gameState != GameState.PAUSE && gameState != GameState.END) {
	    	gameState = GameState.PAUSE;
	    	timeSwapBuff += timeInMilliseconds;
			customHandler.removeCallbacks(updateTimerThread);
			
			LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);  
		    View popupView = layoutInflater.inflate(R.layout.popup_game_pause, null);
		    
		    popupPause = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);		    
		    popupPause.showAtLocation(popupView, Gravity.CENTER, 0, 0);
		}
    }
	
	public void resumeGame(View v) {
		gameState = GameState.START;
		startTime = SystemClock.uptimeMillis();
		customHandler.postDelayed(updateTimerThread, 0);
		popupPause.dismiss();
	}
	
	public void restartGame(View v) {
		popupPause.dismiss();
		timerValue.setText("00:00:000");
		initGame(level);
	}
	
	public void quitGame(View v) {
		Intent intent = new Intent(GameActivity.this, GameMenuActivity.class);
		GameActivity.this.startActivity(intent);
	}
	
	public void saveScore() {
		long currentScore = getScore();
		
		if(currentScore == 0 || currentScore > updatedTime) {
			SharedPreferences prefs = this.getSharedPreferences(Utility.PREF_KEY, Context.MODE_PRIVATE);
			Editor editor = prefs.edit();
			editor.putLong(currentPrefKey, updatedTime);
			editor.commit();
		}		
	}
	
	public long getScore() {
		SharedPreferences prefs = this.getSharedPreferences(Utility.PREF_KEY, Context.MODE_PRIVATE);
		return prefs.getLong(currentPrefKey, 0);
	}
	
	public void endGame(boolean won) {
		gameState = GameState.END;
		timeSwapBuff += timeInMilliseconds;
		customHandler.removeCallbacks(updateTimerThread);
		
		LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);  
	    View popupView = layoutInflater.inflate(R.layout.popup_game_end, null);
	    
	    popupEnd = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);		    
	    popupEnd.showAtLocation(popupView, Gravity.CENTER, 0, 0);

		TextView endLabel1 = (TextView) popupView.findViewById(R.id.label1);
		TextView endLabel2 = (TextView) popupView.findViewById(R.id.label2);
		
		if(won) {	
			endLabel1.setText(R.string.label_win);
			endLabel2.setText(R.string.label_win2);
			endLabel1.setTextColor(Color.parseColor("#2980b9"));
			
			saveScore();
			
			TextView labelTimer = (TextView) popupView.findViewById(R.id.labelTimer);
			labelTimer.setText(Utility.timerToString(updatedTime));

		} else {
			endLabel1.setText(R.string.label_lose);
			endLabel2.setText(R.string.label_lose2);
			endLabel1.setTextColor(Color.parseColor("#e74c3c"));
		}
	    
	    Button btnRestart = (Button)popupView.findViewById(R.id.buttonRestart);
	    btnRestart.setOnClickListener(new Button.OnClickListener(){
		    public void onClick(View v) {
		    	popupEnd.dismiss();
		    	timerValue.setText("00:00:000");
		    	initGame(level);
		    }
		});
	    
	    Button btnQuit = (Button)popupView.findViewById(R.id.buttonQuit);
	    btnQuit.setOnClickListener(new Button.OnClickListener(){
		    public void onClick(View v) {
		    	Intent intent = new Intent(GameActivity.this, GameMenuActivity.class);
				GameActivity.this.startActivity(intent);
		    }
		});
	}
}


