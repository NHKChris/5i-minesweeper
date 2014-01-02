package esgi.project.winmine;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	private Context currentContext;
	private int numberOfCells;
	
	
	public ImageAdapter (Context c, int nbCells) {
		currentContext = c;
		numberOfCells = nbCells;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return numberOfCells;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		ImageView imageView;
		if(v == null) {
			imageView = new ImageView(currentContext);
			imageView.setLayoutParams(new GridView.LayoutParams(50, 50));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setPadding(0, 0, 0, 0);
		} else {
			imageView = (ImageView) v;
		}
		
		imageView.setImageResource(R.drawable.cell_hidden);
		
		return imageView;
	}
}
