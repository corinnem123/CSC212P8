package edu.smith.cs.csc212.p8;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class CheckSpelling {
	/**
	 * Read all lines from the UNIX dictionary.
	 * @return a list of words!
	 */
	public static List<String> loadDictionary() {
		long start = System.nanoTime();
		List<String> words;
		try {
			words = Files.readAllLines(new File("src/main/resources/words").toPath());
		} catch (IOException e) {
			throw new RuntimeException("Couldn't find dictionary.", e);
		}
		long end = System.nanoTime();
		double time = (end - start) / 1e9;
		System.out.println("Loaded " + words.size() + " entries in " + time +" seconds.");
		return words;
	}
	
	//read the book to spell check
	public static List<String> readText() {
		List<String> book;
		try {
			book = Files.readAllLines(new File("src/main/resources/wutheringHeights.txt").toPath());
		} catch (IOException e) {
			throw new RuntimeException("Couldn't find dictionary.", e);
		}
		return book;
	}
	
	/*
	 * Construct a dataset that has Strings that are both in and not in the dictionary.
	 * run through the dictionary, adding "xyz" to the end of some words 
	 * so they're not in the dictionary
	 * 
	 * @param dict - the passed dictionary from structure
	 * @param numSamples - size of the list to return
	 * @param fractionYes - number of words in return list that are also in original list
	 */
	public static List<String> fakeDictionary(List<String> dict, int numSamples, double fractionYes) {
		List<String> og = loadDictionary();
		
		int numYes = (int)(fractionYes*numSamples);
		int size = 0;
		
		//build mixedData list of some copies from original some that aren't
		List<String> mixedData = new ArrayList<>();
		
			for (String o : og) {
				//if the string is smaller than correct words needed, add correct words
				if (size < numYes) {
					mixedData.add(o);
				}
				if (size >= numYes && size < numSamples) {
						mixedData.add(o+"xyz");
					}
				size ++;
			}
			
		return mixedData;
			
		}
	
	
	/**
	 * This method looks for all the words in a dictionary.
	 * @param words - the "queries"
	 * @param dictionary - the data structure.
	 */
	public static void timeLookup(List<String> words, Collection<String> dictionary) {
		long startLookup = System.nanoTime();
		
		int found = 0;
		for (String w : words) {
			if (dictionary.contains(w)) {
				found++;
			} else {
				System.out.println(w);
			}
		}
		
		long endLookup = System.nanoTime();
		double fractionFound = found / (double) words.size();
		double timeSpentPerItem = (endLookup - startLookup) / ((double) words.size());
		int nsPerItem = (int) timeSpentPerItem;
		System.out.println(dictionary.getClass().getSimpleName()+": Lookup of items found="+fractionFound+" time="+nsPerItem+" ns/item");
	}
	
	
	public static void main(String[] args) {
		// --- Load the dictionary.
		List<String> listOfWords = loadDictionary();
		
		// --- Create a bunch of data structures for testing:
		TreeSet<String> treeOfWords = new TreeSet<>(listOfWords);
		HashSet<String> hashOfWords = new HashSet<>(listOfWords);
		SortedStringListSet bsl = new SortedStringListSet(listOfWords);
		CharTrie trie = new CharTrie();
		for (String w : listOfWords) {
			trie.insert(w);
		}
		LLHash hm100k = new LLHash(100000);
		for (String w : listOfWords) {
			hm100k.add(w);
		}
		
		/*
		 * time the creation of each data structure
		 */
		
		//TreeSet
		long startTreeTime = System.nanoTime();
		TreeSet<String> timeTree = new TreeSet<>(listOfWords);
		long endTreeTime = System.nanoTime();
		long TTime = endTreeTime - startTreeTime;
		System.out.println("TreeSetTime: "+TTime+" ns");
		
		//HashSet
		long startHashTime = System.nanoTime();
		HashSet<String> timeHash = new HashSet<>(listOfWords);
		long endHashTime = System.nanoTime();
		long HTime = endHashTime-startHashTime;
		System.out.println("HashSetTime: "+HTime+" ns");
		
		//SortedString
		long startStringTime = System.nanoTime();
		HashSet<String> timeString = new HashSet<>(listOfWords);
		long endStringTime = System.nanoTime();
		long STime = endStringTime-startStringTime;
		System.out.println("SortedStringListTime: "+STime+" ns");
		
		//CharTrie
		long startCharTime = System.nanoTime();
		HashSet<String> timeChar = new HashSet<>(listOfWords);
		long endCharTime = System.nanoTime();
		long CTime = endCharTime-startCharTime;
		System.out.println("CharTrieTime: "+CTime+" ns");
		
		//LLHash
		long startLLHashTime = System.nanoTime();
		HashSet<String> timeLLHash = new HashSet<>(listOfWords);
		long endLLHashTime = System.nanoTime();
		long LLHTime = endLLHashTime-startLLHashTime;
		System.out.println("LLHashSetTime: "+LLHTime+" ns");
		
		
		// --- Make sure that every word in the dictionary is in the dictionary:
		timeLookup(listOfWords, treeOfWords);
		timeLookup(listOfWords, hashOfWords);
		timeLookup(listOfWords, bsl);
		timeLookup(listOfWords, trie);
		timeLookup(listOfWords, hm100k);
		
		/*
		 * spellcheck Wuthering Heights
		 */
		
		//Read the book, covert it to a list of words to spellcheck
		List<String> wutheringHieghts = readText();
		
		String wHActual = new String();
		for (int i = 0; i < wutheringHieghts.size(); i++) {
			wHActual += wutheringHieghts.get(i);
		}
		
		WordSplitter book = new WordSplitter();
		List<String> text = WordSplitter.splitTextToWords(wHActual);
		
		System.out.println("book check:");
		timeLookup(text, treeOfWords);
		timeLookup(text, hashOfWords);
		timeLookup(text, bsl);
		timeLookup(text, trie);
		timeLookup(text, hm100k);
		
		// --- Create a dataset of mixed hits and misses:
		
		
		//give the list from the fakeDictionary method
		List<String> hitsAndMisses = fakeDictionary(listOfWords, 100, 1);
		timeLookup(hitsAndMisses, treeOfWords);
		timeLookup(hitsAndMisses, hashOfWords);
		timeLookup(hitsAndMisses, bsl);
		timeLookup(hitsAndMisses, trie);
		timeLookup(hitsAndMisses, hm100k);
		
		
		// --- linear list timing:
		// Looking up in a list is so slow, we need to sample:
		System.out.println("Start of list: ");
		timeLookup(listOfWords.subList(0, 1000), listOfWords);
		System.out.println("End of list: ");
		timeLookup(listOfWords.subList(listOfWords.size()-100, listOfWords.size()), listOfWords);
		
	
		// --- print statistics about the data structures:
		System.out.println("Count-Nodes: "+trie.countNodes());
		System.out.println("Count-Items: "+hm100k.size());

		System.out.println("Count-Collisions[100k]: "+hm100k.countCollisions());
		System.out.println("Count-Used-Buckets[100k]: "+hm100k.countUsedBuckets());
		System.out.println("Load-Factor[100k]: "+hm100k.countUsedBuckets() / 100000.0);

		
		System.out.println("log_2 of listOfWords.size(): "+listOfWords.size());
		
		System.out.println("Done!");
	}
}
