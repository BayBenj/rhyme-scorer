package data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import phonetics.Phoneticizer;
import main.*;
import java.io.*;
import java.util.*;

public abstract class DataLoader {

	public static Dataset loadDataset(String name, String baseUrl, int sizeLimit) throws IOException, JSONException {
		Dataset result = new Dataset();
		int i = 0;
		for (String word : Phoneticizer.syllableDict.keySet()) {
			if (i > sizeLimit) break;
			word = word.replaceAll("[^\\w]","");
			word = word.toLowerCase();
			System.out.print(i + " ");
			List<JSONObject> o = HttpInterface.get(baseUrl + word);
			Set<String> rhymes = new HashSet<>();
			for (JSONObject object : o) {
				String tempWord = new String(object.getString("word"));
				rhymes.add(tempWord);
			}
			result.put(word,rhymes);
			if (i % 50000 == 0 && i != 0) {
				serializeRhymes(name, result, i);
				System.out.println("SERIALIZED FIRST " + i + " " + name + " RHYMES");
			}
			i++;
		}
		return result;
	}

	public static Dataset deserializeRhymes(String name, int size) {
		System.out.print("Deserializing " + name + " rhymes...");
		Dataset result = null;
		try {
			FileInputStream fileIn = new FileInputStream(Main.rootPath + "data/rhyme_data/" + name + "-" + size + ".ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			result = (Dataset) in.readObject();
			in.close();
			fileIn.close();
			System.out.println("done!");
		}
		catch(IOException i) {
			i.printStackTrace();
		}
		catch(ClassNotFoundException c) {
			System.out.println(name + " rhyme class not found");
			c.printStackTrace();
		}
		return result;
	}

	private static void serializeRhymes(String name, Dataset rhymes, int size) {
		System.out.print("Serializing " + name + " rhymes...");
		try {
			FileOutputStream fileOut = new FileOutputStream(Main.rootPath + "data/rhyme_data/" + name + "-" + size + ".ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(rhymes);
			out.close();
			fileOut.close();
			System.out.println("Serialized " + name + " rhymes saved in data/rhyme_data/" + name + "-" + size + ".ser");
		}
		catch(IOException i) {
			i.printStackTrace();
		}
	}

	public static List<JSONObject> parseJson(String jsonData) throws JSONException {
		final List<JSONObject> result = new ArrayList<>();
		final JSONArray array = new JSONArray(jsonData);
		final int n = array.length();
		for (int i = 0; i < n; ++i) {
			final JSONObject entry = array.getJSONObject(i);
			result.add(entry);
		}
		return result;
	}

}
