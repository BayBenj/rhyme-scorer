package data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import phonetics.Phoneticizer;
import main.*;
import utils.Pair;

import java.io.*;
import java.util.*;

public abstract class DataLoader {

	public static ScoreDataset loadScoredDataset(String name, String baseUrl, int sizeLimit) throws IOException, JSONException {
		ScoreDataset result = new ScoreDataset();
		int i = 0;
		for (String word : Phoneticizer.syllableDict.keySet()) {
			if (i > sizeLimit) break;
			word = word.replaceAll("[^\\w]","");
			word = word.toLowerCase();
			System.out.print(i + " ");
			List<JSONObject> o = HttpInterface.get(baseUrl + word);
			if (o == null) continue;
			Set<Pair<String, Integer>> rhymes = new HashSet<>();
			for (JSONObject object : o) {
				String tempWord = new String(object.getString("word"));
				Integer tempScore = new Integer(object.getInt("score"));
				rhymes.add(new Pair(tempWord, tempScore));
			}
			result.put(word,rhymes);
			if (i == 10000 || i == 5000) {
				serializeRhymes(name, result, i);
				System.out.println("SERIALIZED FIRST " + i + " " + name + " RHYMES");
			}
			i++;
		}
		return result;
	}

	public static SimpleDataset loadDataset(String name, String baseUrl, int sizeLimit) throws IOException, JSONException {
		SimpleDataset result = new SimpleDataset();
		int i = 0;
		for (String word : Phoneticizer.syllableDict.keySet()) {
			if (i > sizeLimit) break;
			word = word.replaceAll("[^\\w]","");
			word = word.toLowerCase();
			System.out.print(i + " ");
			List<JSONObject> o = HttpInterface.get(baseUrl + word);
			if (o == null) continue;
			Set<String> rhymes = new HashSet<>();
			for (JSONObject object : o) {
				String tempWord = new String(object.getString("word"));
				rhymes.add(tempWord);
			}
			result.put(word,rhymes);
			if ((i % 100000 == 0 && i != 0) || i == 10000) {
				serializeRhymes(name, result, i);
				System.out.println("SERIALIZED FIRST " + i + " " + name + " RHYMES");
			}
			i++;
		}
		return result;
	}

	public static SimpleDataset deserializeRhymes(String name, int size) {
		System.out.print("Deserializing " + name + " rhymes...");
		SimpleDataset result = null;
		try {
			FileInputStream fileIn = new FileInputStream(Main.rootPath + "data/rhyme_data/" + name + "-" + size + ".ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			result = (SimpleDataset) in.readObject();
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

	public static ScoreDataset deserializeScoredRhymes(String name, int size) {
		System.out.print("Deserializing " + name + " rhymes...");
		ScoreDataset result = null;
		try {
			FileInputStream fileIn = new FileInputStream(Main.rootPath + "data/rhyme_data/" + name + "-scored-" + size + ".ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			result = (ScoreDataset) in.readObject();
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

	private static void serializeRhymes(String name, SimpleDataset rhymes, int size) {
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

	private static void serializeRhymes(String name, ScoreDataset rhymes, int size) {
		System.out.print("Serializing " + name + " rhymes...");
		try {
			FileOutputStream fileOut = new FileOutputStream(Main.rootPath + "data/rhyme_data/" + name + "-scored-" + size + ".ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(rhymes);
			out.close();
			fileOut.close();
			System.out.println("Serialized " + name + " rhymes saved in data/rhyme_data/" + name + "-scored-" + size + ".ser");
		}
		catch(IOException i) {
			i.printStackTrace();
		}
	}

	public static List<JSONObject> parseJson(String jsonData) throws JSONException {
		List<JSONObject> result = new ArrayList<>();
		final JSONArray array;
		try {
			array = new JSONArray(jsonData);
		}
		catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		final int n = array.length();
		for (int i = 0; i < n; ++i) {
			final JSONObject entry = array.getJSONObject(i);
			result.add(entry);
		}
		return result;
	}

}
