package com.example.note;

import java.io.File;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class Main extends ListActivity {

	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	private static final int PHOTO_TAKEN_REQUEST = 4;
	private static final int BROWSE_GALLERY_REQUEST = 5;
	private Uri image;

	private static final int INSERT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;

	private NotesDbAdapter mDbHelper;
	private Cursor mNotesCursor;
	SimpleCursorAdapter notes;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mDbHelper = new NotesDbAdapter(this);
		mDbHelper.open();
		fillData();
		registerForContextMenu(getListView());
		addLockButtonListener();

	}

	// når man navigerer seg tilbake
	public void onBackPressed() {
		moveTaskToBack(true);
		this.finish();
		return;
	}

	// fyller alle radene med data
	private void fillData() {
		mNotesCursor = mDbHelper.fetchAllNotes();

		notes = new SimpleCursorAdapter(this, R.layout.notes_row, mNotesCursor,
				new String[] { NotesDbAdapter.KEY_TITLE },
				new int[] { R.id.text1 }, 0);

		setListAdapter(notes);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, INSERT_ID, 0, "New Note");
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case INSERT_ID:
			createNote();
			return true;
		case R.id.menu_passpoints_reset:
			resetPasspoints(null);
			return true;
		case R.id.menu_replace_image:
			replaceImage();

			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private void replaceImage() {

		// Offer a choice of methods to replace the image in a dialog;
		// the user can either take a photo or browse the gallery.
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View v = getLayoutInflater().inflate(R.layout.replace_image, null);
		builder.setTitle(R.string.replace_lock_image);
		builder.setView(v);

		final AlertDialog dlg = builder.create();
		dlg.show();

		Button takePhoto = (Button) dlg.findViewById(R.id.take_photo);
		Button browseGallery = (Button) dlg.findViewById(R.id.browse_gallery);

		takePhoto.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				takePhoto();
			}
		});

		browseGallery.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Browse the gallery.
				browseGallery();
			}
		});
	}

	// browser galleriet appen på telefonen

	private void browseGallery() {
		Intent i = new Intent(Intent.ACTION_PICK,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, BROWSE_GALLERY_REQUEST);
	}

	// tar et nytt bilde som blir lagt til i galariet
	private void takePhoto() {
		File picturesDirectory = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File imageFile = new File(picturesDirectory, "passpoints_image");

		Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		image = Uri.fromFile(imageFile);
		i.putExtra(MediaStore.EXTRA_OUTPUT, image);
		startActivityForResult(i, PHOTO_TAKEN_REQUEST);
	}

	// resetter passordet
	private void resetPasspoints(Uri image) {
		Intent i = new Intent(this, Image.class);
		i.putExtra(Image.RESET_PASSPOINTS, true);

		if (image != null) {
			i.putExtra(Image.RESET_IMAGE, image.getPath());
		}

		startActivity(i);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, "Delete");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			mDbHelper.deleteNote(info.id);
			fillData();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	// lagger ny notat
	private void createNote() {
		Intent i = new Intent(this, Newtext.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Cursor c = mNotesCursor;
		c.moveToPosition(position);
		Intent i = new Intent(this, Newtext.class);
		i.putExtra(NotesDbAdapter.KEY_ROWID, id);
		i.putExtra(NotesDbAdapter.KEY_TITLE,
				c.getString(c.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
		i.putExtra(NotesDbAdapter.KEY_BODY,
				c.getString(c.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
		startActivityForResult(i, ACTIVITY_EDIT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		Bundle extras = intent.getExtras();
		switch (requestCode) {
		case ACTIVITY_CREATE:
			String title = extras.getString(NotesDbAdapter.KEY_TITLE);
			String body = extras.getString(NotesDbAdapter.KEY_BODY);
			mDbHelper.createNote(title, body);
			fillData();
			break;
		case ACTIVITY_EDIT:
			Long rowId = extras.getLong(NotesDbAdapter.KEY_ROWID);
			if (rowId != null) {
				String editTitle = extras.getString(NotesDbAdapter.KEY_TITLE);
				String editBody = extras.getString(NotesDbAdapter.KEY_BODY);
				mDbHelper.updateNote(rowId, editTitle, editBody);
			}
			fillData();
			break;
		}

		if (requestCode == BROWSE_GALLERY_REQUEST) {
			String[] columns = { MediaStore.Images.Media.DATA };

			Uri imageUri = intent.getData();

			Cursor cursor = getContentResolver().query(imageUri, columns, null,
					null, null);

			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(columns[0]);
			String imagePath = cursor.getString(columnIndex);

			cursor.close();

			image = Uri.parse(imagePath);
		}

		if (image == null) {
			Toast.makeText(this, R.string.change, Toast.LENGTH_LONG).show();
			return;
		}

		resetPasspoints(image);
	}

	// lock button
	private void addLockButtonListener() {
		Button lockBtn = (Button) findViewById(R.id.lock);

		lockBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(Main.this, Image.class));
			}
		});
	}



}
