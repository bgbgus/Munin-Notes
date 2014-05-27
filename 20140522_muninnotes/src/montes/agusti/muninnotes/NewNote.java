package montes.agusti.muninnotes;

import java.io.OutputStreamWriter;

import montes.agusti.muninnotes.content.MuninContent;
import montes.agusti.muninnotes.content.MuninContent.Note;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;

public class NewNote extends ActionBarActivity {
	// TODO confirmar antes de borrar la nota.
	AlertDialog confirm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_note);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new NewNoteFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.new_note_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.backNoSave) {

			Intent backNoSave = new Intent(this, NoteListActivity.class);
			startActivity(backNoSave);
			finish();

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class NewNoteFragment extends Fragment {
		Button btnSave;
		EditText txtTitle;
		EditText txtBody;
		ProgressDialog dialog;

		public NewNoteFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_new_note,
					container, false);
			return rootView;
		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);

			txtTitle = (EditText) getActivity().findViewById(R.id.txtTitle);
			txtBody = (EditText) getActivity().findViewById(R.id.txtBody);

			btnSave = (Button) getActivity().findViewById(R.id.btnSave);
			btnSave.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					dialog = new ProgressDialog(getActivity());
					dialog.setTitle(R.string.pleaseWait);
					dialog.setMessage(getString(R.string.createFile));
					dialog.setCancelable(false);
					dialog.setIndeterminate(true);
					dialog.show();

					new SaveFile().execute(Utility.JSON_FILE);

				}
			});

		}

		class SaveFile extends AsyncTask<String, Void, JsonObject> {

			@Override
			protected JsonObject doInBackground(String... params) {
				JsonObject note = new JsonObject();
				note.addProperty("title", txtTitle.getText().toString());
				note.addProperty("body", txtBody.getText().toString());

				Note nota = new Note(note.get("title").getAsString(), note.get(
						"body").getAsString());

				MuninContent.addNote(nota);

				try {
					OutputStreamWriter fout = new OutputStreamWriter(
							getActivity().openFileOutput(params[0],
									Context.MODE_PRIVATE));

					fout.write(MuninContent.NOTES_JSON.toString());
					fout.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				return note;
			}

			@Override
			protected void onPostExecute(JsonObject result) {
				super.onPostExecute(result);

				Toast.makeText(
						getActivity(),
						getString(R.string.noteCreated) + " "
								+ result.get("title").getAsString(),
						Toast.LENGTH_SHORT).show();

				Intent toList = new Intent(getActivity(),
						NoteListActivity.class);

				dialog.cancel();
				startActivity(toList);
				getActivity().finish();
			}

		}
	}

}
