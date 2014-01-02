package esgi.project.winmine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ChooseLevelActivity extends Activity {
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    
        setContentView(R.layout.activity_choose_level);
    }
	
	public void launchGame(View v) {
		String level = (String)v.getTag();
		Intent intent = new Intent(ChooseLevelActivity.this, GameActivity.class);
		intent.putExtra("level", level);
		ChooseLevelActivity.this.startActivity(intent);
	}
	
	public void back (View v) {
		
	}
}
