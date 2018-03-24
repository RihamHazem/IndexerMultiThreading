package com.company;

import javafx.util.Pair;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import org.tartarus.snowball.ext.spanishStemmer;

public class Indexer extends Thread {
    public ArrayList<Pair<String, Long>> urls = new ArrayList<>();
    public ArrayList<String> addresses = new ArrayList<>();
    private MyDB mydb = new MyDB();

    private Map<String, Object> page = new HashMap<>();

    public void run() {
        int i = 0;
        for (Pair<String, Long> url : urls) {
            try {
                work(url.getKey(), url.getValue(), addresses.get(i++));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void work(String currentDoc, float docLen, String address) throws IOException {
        //EXTRACTING THE WORDS FROM HTML DOC
        ListLinks l = new ListLinks();
        ArrayList<String> originalWords = new ArrayList<>();
        originalWords = l.work(currentDoc);

        //STOP WORDS PROCESSING
        stopWords sw = new stopWords();
        ArrayList<String> nonStopWords = sw.work(originalWords);

        //STEMMING
        Stemmer stm = new Stemmer();
        ArrayList<String> stemmedWords = stm.work(nonStopWords);
        //////////////////////////////////////////////
       String originalWord, stopWord;
       for(int i = 0; i < stemmedWords.size(); i++) {
           originalWord = nonStopWords.get(i);
           stopWord = stemmedWords.get(i);

            int k = originalWord.indexOf('-');
            String position = originalWord.substring(k + 1);

            String[] arr = stopWord.split("-");//stemmed
            String[] arr2 = originalWord.split("-");//original

            if(arr[0].length() == 0) {
                continue;
            }
            Map<String, Object> elem = (HashMap<String, Object>) page.get(arr[0]);
            if (elem == null) {
                //INSERTION
                Map<String, Object> myMap = new HashMap<>();
                ArrayList<String> pos = new ArrayList<>();
                pos.add(position);

                myMap.put(arr2[0], pos);
                page.put(arr[0], myMap);

            } else {

                ArrayList<String> pos = (ArrayList<String>) elem.get(arr2[0]);
                if (pos == null) {
                    pos = new ArrayList<>();
                    pos.add(position);
                    elem.put(arr2[0], pos);
                } else {
                    pos.add(position);
                    elem.put(arr2[0], pos);
                }

            }
        }
        printPage();
        Map<String, Object> finalMap = new HashMap<String, Object>();
        finalMap.put("URL", "index.html");
        finalMap.put("words", page);
        mydb.collection.insertOne(new Document(finalMap));
    }

    void updatePage(String address) throws IOException {
        mydb.removeURL(address);
//        work(address, 1000, address);
    }

    void printPage() {
//        System.out.println(page);
    }
}