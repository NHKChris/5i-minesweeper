package esgi.project.winmine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class GameScoreActivity extends Activity {
	
	private long score1;
	private long score2;
	private long score3;
	private String timerDefault;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_score);  
        
        timerDefault = "00:00:000";
        
        getScores();
        
        TextView highScore1 = (TextView) findViewById(R.id.highScore1);
        TextView highScore2 = (TextView) findViewById(R.id.highScore2);
        TextView highScore3 = (TextView) findViewById(R.id.highScore3);
        
        highScore1.setText((score1 > 0) ? Utility.timerToString(score1) : timerDefault);
        highScore2.setText((score2 > 0) ? Utility.timerToString(score2) : timerDefault);
        highScore3.setText((score3 > 0) ? Utility.timerToString(score3) : timerDefault);
    }
	
	public void getScores() {
		SharedPreferences prefs = this.getSharedPreferences(Utility.PREF_KEY, Context.MODE_PRIVATE);
		score1 = prefs.getLong(Utility.KEY + 1, 0);
		score2 = prefs.getLong(Utility.KEY + 2, 0);
		score3 = prefs.getLong(Utility.KEY + 3, 0);
	}
	
	public void back(View v){
		Intent intent = new Intent(GameScoreActivity.this, GameMenuActivity.class);
		GameScoreActivity.this.startActivity(intent);
	}
}
