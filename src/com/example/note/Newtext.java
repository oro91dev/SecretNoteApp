package com.example.note;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//ny tekst klasse
public class Newtext extends Activity implements OnClickListener {
	EditText eb1;
	EditText eb2;
	Button cutb;
	Button copyb;
	Button pasteb;
	Button saveb, backb;
	Editable s1, s2;

	Spannable str;
	Spannable str2;

	private Long mRowId;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		setContentView(R.layout.new_text);
		eb1 = (EditText) findViewById(R.id.title);
		eb1.setTextColor(Color.parseColor("#6D351A"));
		eb2 = (EditText) findViewById(R.id.insertdata);
		eb2.setTextColor(Color.parseColor("#000000"));
		mRowId = null;
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String title = extras.getString(NotesDbAdapter.KEY_TITLE);
			String body = extras.getString(NotesDbAdapter.KEY_BODY);
			mRowId = extras.getLong(NotesDbAdapter.KEY_ROWID);
			if (title != null) {
				eb1.setText(title);
			}
			if (body != null) {
				eb2.setText(body);
			}
			
		}
		cutb = (Button) findViewById(R.id.cut);
		cutb.setOnClickListener(this);
		copyb = (Button) findViewById(R.id.copy);
		copyb.setOnClickListener(this);
		pasteb = (Button) findViewById(R.id.paste);
		pasteb.setOnClickListener(this);
		saveb = (Button) findViewById(R.id.save);
		saveb.setOnClickListener(this);


	}

	public void onBackPressed() {
		moveTaskToBack(true);
		return;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            Intent intent = new Intent(this, Main.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	//metoder for hver knapp
	@Override
	public void onClick(View v) {
		
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.save:
			if (valider()) {
			Bundle bundle = new Bundle();
			
			bundle.putString(NotesDbAdapter.KEY_TITLE, eb1.getText()
					.toString());
			bundle.putString(NotesDbAdapter.KEY_BODY, eb2.getText()
					.toString());
			
			if (mRowId != null) {
				bundle.putLong(NotesDbAdapter.KEY_ROWID, mRowId);
			}
			Intent mIntent = new Intent();
			mIntent.putExtras(bundle);
			setResult(RESULT_OK, mIntent);
			finish();
			break;
			}
		case R.id.copy:
			if (eb2.getSelectionEnd() > eb2.getSelectionStart()) {
				s1 = (Editable) eb2.getText().subSequence(
						eb2.getSelectionStart(),
						eb2.getSelectionEnd());
			} else {
				s1 = (Editable) eb2.getText().subSequence(
						eb2.getSelectionEnd(),
						eb2.getSelectionStart());
			}
			break;
		case R.id.cut:
			if (eb2.getSelectionEnd() > eb2.getSelectionStart()) {
				s1 = (Editable) eb2.getText().subSequence(
						eb2.getSelectionStart(),
						eb2.getSelectionEnd());
			} else {
				s1 = (Editable) eb2.getText().subSequence(
						eb2.getSelectionEnd(),
						eb2.getSelectionStart());
			}
			eb2.getText().replace(
					Math.min(eb2.getSelectionStart(),
							eb2.getSelectionEnd()),
					Math.max(eb2.getSelectionStart(),
							eb2.getSelectionEnd()), "", 0, 0);
			break;
		case R.id.paste:
			eb2.getText().replace(
					Math.min(eb2.getSelectionStart(),
							eb2.getSelectionEnd()),
					Math.max(eb2.getSelectionStart(),
							eb2.getSelectionEnd()), s1, 0, s1.length());
			break;

		}
	}
	
	public boolean valider() {
		StringBuilder builder = new StringBuilder();
		
		if (eb1.getText().toString().matches("")) {
			builder.append(getString(R.string.failure1) + "\n");
		}

		if (eb2.getText().toString().matches("")) {
			builder.append(getString(R.string.failure2) + "\n");
		}

		if (builder.toString().equals("")) {
			return true;
		} else {
			Toast.makeText(this, builder.toString(), Toast.LENGTH_SHORT).show();
		}
		return false;
	}

	
}