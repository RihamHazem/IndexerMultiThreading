package com.company;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Indexer extends Thread {
    public ArrayList<Pair<String, Long>> urls = new ArrayList<>();
    public ArrayList<String> addresses = new ArrayList<>();
    public void run() {
        int i = 0;
        for (Pair<String, Long> url : urls) {
            work(url.getKey(), url.getValue(), addresses.get(i++));
        }
    }

    private void work(String currentDoc, float docLen, String address) {
        //EXTRACTING THE WORDS FROM HTML DOC
        ListLinks l = new ListLinks();
        try {
            l.work(currentDoc);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }


        //STOP WORDS PROCESSING
        stopWords sw = new stopWords();
        sw.work("in.txt", "in2.txt");

        System.out.println("\n\n");

        //STEMMING
        Stemmer stm = new Stemmer();
        stm.work("in2.txt", "out.txt");
        MyDB mydb = new MyDB();
        //DB PART
        try {
            BufferedReader in = new BufferedReader(new FileReader("out.txt"));//stemmed words
            BufferedReader in2 = new BufferedReader(new FileReader("in2.txt"));//original words

            //////////////////////////////////////////////
            String s, s2;
            while ((s = in.readLine()) != null) {
                s2 = in2.readLine();//original

                int i = s2.indexOf('-');
                String position = s2.substring(i + 1);

                String[] arr = s.split("-");//stemmed
                String[] arr2 = s2.split("-");//original
                System.out.println(arr[0]);
                if (!mydb.checkWord(arr[0])) {
                    System.out.println("WORD not FOUND IN DB! call insertNewWord");
                    //INSERTION
                    mydb.insertNewWord(arr[0], arr2[0], address, position, docLen);
                } else {
                    System.out.println("WORD FOUND IN DB! continue checking");
                    //CHECK WORD AND URL EXISTANCE
                    if (!mydb.checkWordDoc(arr[0], address)) {
                        System.out.println("WORD FOUND and URL not FOUND IN DB!");
                        //CALL APPEND ARRAY OF DOCS
                        mydb.insertNewDoc(arr[0], arr2[0], address, position, docLen);
                    } else {
                        System.out.println("WORD FOUND and URL FOUND IN DB! continue checking");
                        //CHECK WORD AND URL AND SYNO EXIST
                        if (!mydb.checkWordDocSyno(arr[0], address, arr2[0])) {

                            System.out.println("WORD FOUND and URL FOUND and SYNO not FOUND IN DB!");
                            //APPEDN SYNOS
                            mydb.insertNewSyno(arr[0], arr2[0], address, position, docLen);
                        } else {
                            System.out.println("WORD FOUND and URL FOUND and SYNO FOUND IN DB!");
                            //APPEND POSITIONS, INCREASE COUNT
                            mydb.insertNewPos(arr[0], arr2[0], address, position, docLen);
                        }

                    }

                }
            }
            in.close();
            in2.close();
        } catch (IOException e) {
            System.out.println("Error!");
        }
        System.out.println("ALL DONE!");
        //mydb.removeURL("www.fb.com");
    }
}