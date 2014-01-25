package esgi.project.winmine;

public class Utility {
	public static final String PREF_KEY = "highScoreWinmine";
	public static final String KEY = "highScore";
	
	public static String timerToString (long timer) {		
		int seconds = (int) (timer / 1000);
		int minutes = seconds / 60;
		seconds = seconds % 60;
		int milliseconds = (int) (timer % 1000);
		
		return "" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds) + ":" + String.format("%03d", milliseconds);
	}
}
