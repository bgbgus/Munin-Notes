package montes.agusti.muninnotes;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import montes.agusti.muninnotes.content.MuninContent;
import montes.agusti.muninnotes.content.MuninContent.Note;
import montes.agusti.muninnotes.crypto.AESCrypto;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.password, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		ProgressDialog dialog;
		Button btnEnter;
		EditText txtPassword;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_password,
					container, false);
			return rootView;
		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);

			txtPassword = (EditText) getActivity().findViewById(
					R.id.txtPassword);

			btnEnter = (Button) getActivity().findViewById(R.id.btnEnter);
			btnEnter.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					dialog = new ProgressDialog(getActivity());
					dialog.setTitle(R.string.pleaseWait);
					dialog.setMessage(getString(R.string.checkPass));
					dialog.setCancelable(false);
					dialog.setIndeterminate(true);
					dialog.show();


					// TODO desencriptar el fitxer .aes

					try {
						AESCrypto.setPass(txtPassword.getText().toString());
						
						File aes = new File(Utility.ENCRYPTED_FILE);
						if (aes.isFile() && aes.canRead())
							AESCrypto.decode(aes, Utility.JSON_FILE);
						

						new Reader().execute(Utility.JSON_FILE);
					} catch (Exception e) {
						e.printStackTrace();
						dialog.cancel();
						Toast.makeText(getActivity(), R.string.incorrectPass,
								Toast.LENGTH_LONG).show();
						getActivity().finish();
					}
					


				}
			});
		}

		class Reader extends AsyncTask<String, Void, Boolean> {

			@Override
			protected Boolean doInBackground(String... params) {
				boolean ret = false;

				JsonParser parser = new JsonParser();
				JsonArray array = null;

				MuninContent.cleanMemory();

				try {
					BufferedReader fin = new BufferedReader(
							new InputStreamReader(getActivity().openFileInput(
									params[0])));
					
					String texto = fin.readLine();

					Toast.makeText(getActivity(), texto,
							Toast.LENGTH_LONG).show();
					if (texto == null) {
						JsonObject note = new JsonObject();
						note.addProperty("title", "readme");
						note.addProperty("body",
								"This is Munin Notes' read-me file.");

						Note nota = new Note(note.get("title").getAsString(),
								note.get("body").getAsString());

						MuninContent.addNote(nota);

						OutputStreamWriter fout = new OutputStreamWriter(
								getActivity().openFileOutput(params[0],
										Context.MODE_PRIVATE));

						fout.write(MuninContent.NOTES_JSON.toString());
						fout.close();
					} else {

						array = (JsonArray) parser.parse(texto);
						JsonObject object;

						for (int i = 0; i < array.size(); i++) {
							object = (JsonObject) array.get(i);
							MuninContent.addNote(new MuninContent.Note(object
									.get("title").getAsString(), object.get(
									"body").getAsString()));
						}
					}

					fin.close();
					ret = true;
				} catch (Exception e) {
					e.printStackTrace();
				}

				return ret;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				dialog.cancel();
				if (result) {
					Toast.makeText(getActivity(), R.string.correctPass,
							Toast.LENGTH_SHORT).show();
					Intent backNoSave = new Intent(getActivity(),
							NoteListActivity.class);
					startActivity(backNoSave);
					getActivity().finish();
				} else {
					Toast.makeText(getActivity(), R.string.errorReadingData,
							Toast.LENGTH_LONG).show();
					getActivity().finish();
				}
			}
		}

	}

}
