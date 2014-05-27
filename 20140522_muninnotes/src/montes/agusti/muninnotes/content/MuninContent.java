package montes.agusti.muninnotes.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class MuninContent {
	
	public static List<Note> NOTES = new ArrayList<Note>();
	public static JsonArray NOTES_JSON = new JsonArray();
	
	public static Map<String, Note> NOTES_MAP = new HashMap<String, Note>();
	
	public static void addNote(Note note) {
		NOTES.add(note);
		NOTES_MAP.put(note.title, note);
		
		JsonObject json = new JsonObject();
		json.addProperty("title", note.title);
		json.addProperty("body", note.body);
		NOTES_JSON.add(json);
	}
	
	public static void removeNote(Note note) {
		NOTES.remove(NOTES_MAP.get(note.title));
		remakeJson();
		NOTES_MAP.remove(note.title);
	}
	
	public static void replaceNoteBody(String title, String body, String titleOld) {
		Note note = NOTES_MAP.get(titleOld);
		
		note.body = body;
		note.title = title;
	}
	
	private static void remakeJson() {
		NOTES_JSON = new JsonArray();
		JsonObject json;
		
		for (int i = 0; i < NOTES.size(); i++) {
			json = new JsonObject();
			json.addProperty("title", NOTES.get(i).title);
			json.addProperty("body", NOTES.get(i).body);
			NOTES_JSON.add(json);
		}
		
	}
	
	public static void cleanMemory() {
		NOTES = new ArrayList<Note>();
		NOTES_JSON = new JsonArray();
		NOTES_MAP =  new HashMap<String, Note>();
	}
	
	

	public static class Note {
		public String title;
		public String body;

		public Note(String title, String body) {
			this.body = body;
			this.title = title;
		}

		@Override
		public String toString() {
			return title;
		}

	}
}
