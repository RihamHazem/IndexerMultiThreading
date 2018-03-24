package com.company;
import org.tartarus.snowball.ext.englishStemmer;
import java.util.ArrayList;



public class Stemmer {
    public ArrayList<String> work(ArrayList<String> nonStopWords) {
        englishStemmer stemmer = new englishStemmer();
        ArrayList<String> stemmedWords = new ArrayList<>();
        for(String nsw: nonStopWords) {
            String[] arr = nsw.split("-");
            stemmer.setCurrent(arr[0]);
            if (stemmer.stem()){
                stemmedWords.add(stemmer.getCurrent() + "-" + arr[1] + "-" + arr[2]);
            }
        }
        return stemmedWords;
    }

}
