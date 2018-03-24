package com.company;

import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
	// write your code here
        Indexer[] indexers = new Indexer[26];
        MyDB DB = new MyDB();
        ArrayList<Pair<String, Pair<String, Long> >> urls = DB.retrieveURLs();
        int part = urls.size() / 25, cnt = 0;
        for (Indexer indexer: indexers) {
            indexer = new Indexer();
            for (int i = 0; i < part; i++) {
                indexer.urls.add(urls.get(cnt).getValue());
                indexer.addresses.add(urls.get(cnt++).getKey());
            }
            indexer.run();
        }

//        for (Indexer indexer: indexers) {
//            try {
//                if(indexer != null) {
//                    indexer.join();
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
