package esgi.project.winmine;

import java.util.ArrayList;
import java.util.List;

public class Grid {
	public int height;
	public int width;
	public int numberOfBombs;	
	public Cell[][] grid;
	public List<Cell> bombCoords;
	
	public Grid() {
		this.height = 5;
		this.width = 5;
		this.numberOfBombs = 10;
		this.grid = new Cell[width][height];
		this.bombCoords = new ArrayList<Cell>();
	}
	
	public Grid(int w, int h, int b) {
		this.height = h;
		this.width = w;
		this.numberOfBombs = b;
		this.grid = new Cell[w][h];
		this.bombCoords = new ArrayList<Cell>();
	}
	
	public void InitGrid() {
		int bomb = this.numberOfBombs;
		
		for (int i=0; i<this.width; i++) {
			for(int j=0; j<this.height; j++) {
				this.grid[i][j] = new Cell(i, j, false, 0);
			}			
		}
		
		do {
			int randW = (int)(Math.random() * this.width);
			int randH = (int)(Math.random() * this.height);
			
			if(this.grid[randW][randH].GetValue() == 0) {
				this.grid[randW][randH].SetAsBomb();
				this.bombCoords.add(this.grid[randW][randH]);
				bomb--;
			}
		} while(bomb > 0); 
		
//		this.DisplayGrid();
		this.SetClues();
	}
	
	public void SetClues() {		
		for(Cell c : this.bombCoords) {
			int modifierW = -1;
			int modifierH = -1;
			
			int posX = c.GetX();
			int posY = c.GetY();
			
			for (int i = 0; i < 3; i++) {		
				modifierH = -1;
				for(int j = 0; j < 3; j++) {
					if((posX + modifierW) > -1 && (posX + modifierW) < this.width && (posY + modifierH) > -1 && (posY + modifierH) < this.height && !this.grid[posX + modifierW][posY + modifierH].IsBomb()) {
						this.grid[posX + modifierW][posY + modifierH].IncrementValue();
					} 
					modifierH++;
				}
				modifierW++;
			}			
		}		
		this.DisplayGrid();
	}
	
	public void DisplayGrid() {
		for(int i=0; i<this.width; i++) {
			for(int j=0; j<this.height; j++) {
				System.out.print(this.grid[i][j].GetValue() + " ");
			}
			System.out.println();
		}
	}
}
