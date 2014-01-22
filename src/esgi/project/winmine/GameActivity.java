package esgi.project.winmine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageView;

class GameState {
	public static final int START = 0;
	public static final int RESUME = 1;
	public static final int PAUSE = 2;
	public static final int END = 3;
}

public class GameActivity extends Activity {
	private Grid grid;
	private int gridHeight;
	private int gridWidth;
	private Cell[][] gridCells;
	private GridView gridView;
	private int gameState;
	@SuppressWarnings("unused")
	private int numberOfFlag;
	private int numberOfCellsDisplayed;
	private int numberOfDisplayedNeeded;
	
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
        String value = intent.getStringExtra("level");
        
        this.initGame(value);
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
    	gameState = GameState.START;
//    	setView();
	}
	
	public void setGridView() {
		gridView = (GridView) findViewById(R.id.gameGrid);
		gridView.setNumColumns(gridHeight);
		gridView.setAdapter(new ImageAdapter(this, gridHeight * gridWidth));

		gridView.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	if(gameState != GameState.END) {
		    		Cell cellClicked = getCellClicked(position);
		    		checkCell(cellClicked);
		    		checkGame();
	        	}
	        }
	    });
		
		gridView.setOnItemLongClickListener(new OnItemLongClickListener() {
	        public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
	        	if(gameState != GameState.END) {
		        	setFlag(position);
	        	}
	        	return true;
	        }
	    });
	}
	
	public Cell getCellClicked(int position) {
		int posX = 0;
		int posY = 0;
		
		if(position>0) {
			posX = position/gridHeight;
			posY = position%gridHeight;
		}
		
		return gridCells[posX][posY];
	}
	
	public void checkCell(Cell cell) {
		if(!cell.IsDisplayed()) {
			int value = cell.GetValue();
			displayCell(cell);
			
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
			int drawIndex;
			
			if(cell.IsBomb()) {
				drawIndex = R.drawable.cell_mine;
			} else {
				drawIndex = gridImages[value];
			}
			
			ImageView imageView = (ImageView)gridView.getChildAt(position);
			imageView.setImageResource(drawIndex);
			cell.SetDisplayed();
			numberOfCellsDisplayed++;
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
	}
	
	public void checkGame() {
		if(numberOfDisplayedNeeded == numberOfCellsDisplayed) {
			endGame(true);
		}
	}	
	
	public void endGame(boolean won) {
		gameState = GameState.END;
		
		if(won) {
			Log.v("END GAME", "SUCCESS");
		} else {
			Log.v("END GAME", "FAILURE");
		}
	}
//	public void setView () {
//		TableLayout tableLayout = (TableLayout) findViewById(R.id.gameLayout);
//		
//		gridHeight = grid.getHeight();
//		gridWidth = grid.getWidth();
//		gridCells = grid.getGrid();
//		
//		TableRow.LayoutParams params = new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f);
//
//		for(int i=0; i<gridWidth; i++) {
//			final TableRow row = new TableRow(this);
////			tr_head.setBackgroundColor(Color.GRAY);
//		  
//			for(int j=0; j<gridHeight; j++) {
//				final Button cell = new Button(this);
//				cell.setId(i*gridHeight+j);
//				cell.setText(Integer.toString(gridCells[i][j].GetValue()));
//				cell.setTextColor(Color.BLACK);
////				cell.setBackgroundColor(Color.BLUE);
//				cell.setPadding(2, 2, 2, 2);
//				cell.setGravity(Gravity.CENTER);
//				cell.setLayoutParams(params);
//				cell.setOnClickListener(CreateClickCellListener(cell));
//				
//				row.addView(cell);
//			}		
//			tableLayout.addView(row);
//		}
//	}
//	
//	public OnClickListener CreateClickCellListener(final Button cell) {
//		return new OnClickListener() {			
//			@Override
//			public void onClick(View v) {
//				checkCell(cell);
//			}
//		};
//	}
//	
//	public void checkCell (Button cell) {
//		int id = cell.getId();
//		int posX = 0;
//		int posY = 0;
//		
//		if(id>0) {
//			posX = id/gridHeight;
//			posY = id%gridHeight;
//		}
//		
//		Log.v("POS", posX + ":" + posY + " = " + gridCells[posX][posY].GetValue());
//	}
}


