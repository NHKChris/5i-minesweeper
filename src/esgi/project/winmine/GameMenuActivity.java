package esgi.project.winmine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class GameMenuActivity extends Activity {
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void startGame(View v) {
    	Intent intent = new Intent(GameMenuActivity.this, ChooseLevelActivity.class);
    	GameMenuActivity.this.startActivity(intent);
    }
    
    public void showScores(View v) {
    	Intent intent = new Intent(GameMenuActivity.this, GameScoreActivity.class);
    	GameMenuActivity.this.startActivity(intent);
    }
    
    public void quitGame(View v) {
    	Intent intent = new Intent();
    	intent.setAction(Intent.ACTION_MAIN);
    	intent.addCategory(Intent.CATEGORY_HOME);
    	this.startActivity(intent);
    }
}
