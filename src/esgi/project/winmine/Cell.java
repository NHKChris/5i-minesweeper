package esgi.project.winmine;

public class Cell {
	private int posX;
	private int posY;
	private boolean isDisplayed;
	private boolean isBomb;
	private int value;
	private boolean isFlag;
	
	public Cell(int _posX, int _posY, boolean _isBomb, int _value) {
		this.posX = _posX;
		this.posY = _posY;
		this.isBomb = _isBomb;
		this.isDisplayed = false;
		this.value = _value;
	}
	
	public void SetPosX(int _index) {
		this.posX = _index;
	}
	
	public void SetPosY(int _index) {
		this.posY = _index;
	}	
	
	public void SetValue(int _value) {
		this.value = _value;
	}
	
	public void SetAsBomb() {
		this.isBomb = true;
		this.value = -1;
	}
	
	public void SetFlag(boolean isFLag) {
		this.isFlag = isFLag;
	}
	
	public void SetDisplayed() {
		this.isDisplayed = true;
	}
	
	public void IncrementValue() {
		this.value++;
	}
	
	public int[] GetPos () {
		return new int[] {this.posX, this.posY};
	}
	
	public int GetX() {
		return this.posX;
	}
	
	public int GetY() {
		return this.posY;
	}
	
	public int GetValue() {
		return this.value;
	}

	public boolean IsBomb () {
		return this.isBomb;
	}
	
	public boolean IsDisplayed() {
		return this.isDisplayed;
	}
	
	public boolean IsFlag() {
		return this.isFlag;
	}
}
