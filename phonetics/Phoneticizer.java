package phonetics;

import edu.cmu.sphinx.linguist.g2p.G2PConverter;
import main.Main;
import phonetics.syllabic.Syllabifier;
import phonetics.syllabic.Syllable;
import phonetics.syllabic.WordSyllables;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Phoneticizer {

	//    private static final String cmuFilePath = TabDriver.dataDir + "/pron_dict/cmudict-0.7b.txt";
//    private static final String phonesFilePath = TabDriver.dataDir + "/pron_dict/cmudict-0.7b.phones.reordered.txt";
	private static final String cmuFilePath = Main.rootPath + "data/pron_dict/cmudict-0.7b.txt";

	public static Map<String, List<Pronunciation>> cmuDict = loadCMUDict();
	public static Map<String, List<WordSyllables>> syllableDict = loadSyllableDicts();
	//    private static Map<String, Pair<Integer, phonetics.MannerOfArticulation>> phonesDict = loadPhonesDict();
//    private static List<Pair<String, phonetics.MannerOfArticulation>> reversePhonesDict = loadReversePhonesDict();
	private static G2PConverter converter = new G2PConverter(Main.rootPath + "data/pron_dict/model.fst.ser");

	/**
	 * Loads CMU dictionary from file into a datastructure
	 */
	public static Map<String, List<Pronunciation>> loadCMUDict() {
//        loadPhonesDict();

		if (cmuDict == null) {
			cmuDict = new HashMap<>();

			try {
				BufferedReader bf = new BufferedReader(new FileReader(cmuFilePath));

				String[] lineSplit, phonesSplit;
				Pronunciation phones;
				String line, key;
				Phoneme sPhone;
				int stress, parenIdx;
				List<Pronunciation> newList = null;

				while ((line = bf.readLine()) != null) {
					if (line.startsWith(";;;"))
						continue;

					lineSplit = line.split("  ");
					phonesSplit = lineSplit[1].split(" ");
					phones = new Pronunciation();

					for (String phone : phonesSplit) {
						if (phone.length() == 3) {// we assume that any phonemeEnum with three chars, the third char is the stress
							stress = Integer.parseInt(phone.substring(2, 3));
							phone = phone.substring(0, 2);
						}
						else
							stress = -1;
						if (PhonemeEnum.valueOf(phone).isVowel()) {
							sPhone = new VowelPhoneme(PhonemeEnum.valueOf(phone), stress);
						}
						else
							sPhone = new ConsonantPhoneme(PhonemeEnum.valueOf(phone));
						phones.add(sPhone);
					}
					// System.out.println(lineSplit[0] + ":" + Arrays.toString(phones));

					key = lineSplit[0];

					parenIdx = key.indexOf('(', 1);
					if (parenIdx == -1) {
						newList = new ArrayList<>();
						cmuDict.put(key, newList);
						newList.add(phones);
					} else {
						assert(cmuDict.containsKey(key.substring(0, parenIdx)));
						newList.add(phones);
					}
					// U.promptEnterKey("");
				}

				bf.close();
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
		}

		return cmuDict;
	}

	public static Map<String, List<WordSyllables>> loadSyllableDicts() {
		Map<String, List<WordSyllables>> result = new HashMap<>();
		for (Map.Entry<String, List<Pronunciation>> entry : cmuDict.entrySet()) {
			List<WordSyllables> pronunciationSyls = new ArrayList<>();
			for (Pronunciation pronunciation : entry.getValue()) {
				WordSyllables syllables = Syllabifier.algorithmicallyParse(pronunciation);
				pronunciationSyls.add(syllables);
//				int nSyl = syllables.size();
//				if (nSyl > 0) {
//					Syllable ultimate = syllables.get(syllables.size() - 1);
////					Set<String> oldSet1 = lastSylRhymeDict.get(ultimate.getRhyme());
////					if (oldSet1 == null)
////						oldSet1 = new HashSet<>();
////					oldSet1.add(entry.getKey());
////					lastSylRhymeDict.put(ultimate.getRhyme().getPhonemeEnums(), oldSet1);
//					if (nSyl > 1) {
//						List<List<phonetics.PhonemeEnum>> list1 = new ArrayList<>();
//						Syllable penultimate = syllables.get(syllables.size() - 2);
//						list1.add(penultimate.getRhyme().getPhonemeEnums());
//						list1.add(ultimate.getAllPhonemes().getPhonemeEnums());
////						Set<String> oldSet2 = lastSylRhymeDict.get(list1);
////						if (oldSet2 == null)
////							oldSet2 = new HashSet<>();
////						oldSet2.add(entry.getKey());
////						last2SylRhymeDict.put(list1, oldSet2);
//						if (nSyl > 2) {
//							List<List<phonetics.PhonemeEnum>> list2 = new ArrayList<>();
//							Syllable antepenultimate = syllables.get(syllables.size() - 3);
//							list2.add(antepenultimate.getRhyme().getPhonemeEnums());
//							list2.add(penultimate.getAllPhonemes().getPhonemeEnums());
//							list2.add(ultimate.getAllPhonemes().getPhonemeEnums());
////							Set<String> oldSet3 = lastSylRhymeDict.get(list2);
////							if (oldSet3 == null)
////								oldSet3 = new HashSet<>();
////							oldSet3.add(entry.getKey());
////							last3SylRhymeDict.put(list2, oldSet3);
//						}
//					}
//				}
			}
			result.put(entry.getKey(), pronunciationSyls);
		}
		return result;
	}

	private static List<Syllable> getLastXSyllables(List<Syllable> syllables, int x) {
		if (syllables == null || x > syllables.size())
			return null;

		List<Syllable> result = new WordSyllables();
		for (int i = 0; i < x; i++)
			result.add(syllables.get(syllables.size() - x + i));
		return result;
	}

	public static void main(String[] args) throws IOException {
		System.out.println("Loading CMU...");
		Phoneticizer.loadCMUDict();

		String[] tests = new String[]{"Potatoes, tomatoes, windy","hey-you, i.o.u. nu__in","namaste","schtoikandikes","lichtenstein","avadacadabrax"};
		for (String test : tests) {
			System.out.println("VowelPronunciation for \"" + test + "\"");
			List<Pronunciation> phones = getPronunciations(test);
			for (List<Phoneme> phonemes : phones) {
				System.out.println("\t" + Arrays.toString(readable(phonemes)));
			}
		}
	}

	public static List<Pronunciation> getPronunciations(String s) {
		boolean contains = cmuDict.containsKey(s);
		//TOMATO    T AH0-M EY1-T OW2
		//CREATION  K R IY0-EY1-SH AH0 N
		List<Pronunciation> pronunciations = cmuDict.get(s);
		return pronunciations;
	}

	public static Pronunciation getTopPronunciation(String s) {
		List<Pronunciation> result = getPronunciations(s);
		if (result != null && !result.isEmpty())
			return getPronunciations(s).get(0);
		return null;
	}

	public static List<WordSyllables> getSyllables(String s) {
		return syllableDict.get(s.toUpperCase());
	}

	/**
	 * Returns a list of ways the input string could be pronounced. If word is in cmu dictionary, then CMU entry is returned, otherwise G2Pconverter is used to guess.
	 * @param string
	 * @return
	 */
	public static List<Pronunciation> getPronunciationsMultipleWords(String string) {
		List<Pronunciation> prevPhones = null, nextPhones, vowelPronunciationChoices;

		for (String s : string.toUpperCase().trim().split("[^A-Z0-9']+")) {
			prevPhones.add(getTopPronunciation(s));
		}

		return prevPhones == null? new ArrayList<>(): prevPhones;
	}

	public static String[] readable(List<Phoneme> word1sPs) {
		String[] returnVal = new String[word1sPs.size()];

		for (int i = 0; i < returnVal.length; i++) {
			if (word1sPs.get(i) instanceof VowelPhoneme)
				returnVal[i] = word1sPs.get(i).phonemeEnum.toString() + ":" + ((VowelPhoneme)word1sPs.get(i)).stress;
			else
				returnVal[i] = word1sPs.get(i).phonemeEnum.toString();
		}

		return returnVal;
	}

	public static VowelPhoneme[] getLastSyllable(VowelPhoneme[] phones, int offsetFromEnd) {
		int start = phones.length;

		if (start == 0) {
			return new VowelPhoneme[0];
		}

		int syllableCount = 0;
		int lastVowelPosition = start;

		while (syllableCount <= offsetFromEnd && start > 0) {
			if (phones[--start].phonemeEnum.isVowel()) {
				if (syllableCount == offsetFromEnd)
				{
					return Arrays.copyOfRange(phones, start, lastVowelPosition);
				}
				else {
					lastVowelPosition = start;
					syllableCount++;
				}
			}
		}

		return Arrays.copyOfRange(phones, 0, lastVowelPosition);
	}

	public static boolean cmuDictContains(String word) {
		return cmuDict.containsKey(word.toUpperCase());
	}

	public static Pronunciation getPronunciationForWord(String string) {
		if (string != null && string.length() > 0 && string.matches("\\w+")) {
			return getTopPronunciation(string.toUpperCase());
		}
		return null;
	}
	
	public static List<WordSyllables> getSyllablesForWord(String string) {
		if (string != null && string.length() > 0 && string.matches("\\w+")) {
			return getSyllables(string.toUpperCase());
		}
		return null;
	}
	
	public static Map<String, List<Pronunciation>> getCmuDict() {
		return cmuDict;
	}

//	public static List<WordSyllables> useG2P(String upperCase) {
//		ArrayList<Path> phoneticize = converter.phoneticize(upperCase, 1);
//
//		if (phoneticize == null || phoneticize.isEmpty())
//			return null;
//
//		Path path = phoneticize.get(0);
//		if (path == null) return null;
//		ArrayList<String> pathPhones = path.getPath();
//		if (pathPhones.size() != 1) {
//			return null;
//		}
//
//		List<Phoneme> phones = new ArrayList<Phoneme>();
//
//		String phone = pathPhones.get(0);
//		PhonemeEnum temp = PhonemeEnum.valueOf(phone);
//		if (temp.isVowel()) {
//			phones.add(new VowelPhoneme(temp, 1));
//		}
//		else {
//			phones.add(new ConsonantPhoneme(temp));
//		}
//
//		List<WordSyllables> wordSyllables = new ArrayList<WordSyllables>();
//		wordSyllables.add(Syllabifier.algorithmicallyParse(phones));
//
//		return wordSyllables;
//	}
}
