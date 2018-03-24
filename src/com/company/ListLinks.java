package com.company;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class ListLinks {
    public ArrayList<String> work(String html) throws IOException {
        Document doc = Jsoup.parse(html);
        Elements links = doc.select("a[href]");
        Elements paragraphs = doc.select("p");
        Elements media = doc.select("[src]");
        Elements imports = doc.select("link[href]");
        Elements headers = doc.select("h2,h1");
        int counter=0, spaceCnt=0;
        ArrayList<String> words = new ArrayList<>();
        for (Element p1 : paragraphs) {
            String PText = p1.text();

            StringBuilder word = new StringBuilder();
            for(int i=0;i<PText.length();i++)
            {
                if(((PText.charAt(i)>='a' && PText.charAt(i)<='z') || (PText.charAt(i)>='A' && PText.charAt(i)<='Z')))
                {
                    word.append(PText.charAt(i));
                    spaceCnt=0;
                }
                else if(spaceCnt == 0)
                {
                    word.append("-"+counter+"-p");
                    words.add(word.toString());
                    word.setLength(0);
                    spaceCnt++;
                    counter++;
                }
            }
            word.append("-"+counter+"-p");
            words.add(word.toString());
            word.setLength(0);
            counter++;
        }

//        print("\nheaders: (%d)", headers.size());

        for (Element h1 : headers) {
//            print("* H: %s ", trim(h1.text(), 100));
            String HText = h1.text();

            StringBuilder word = new StringBuilder();
            for(int i=0;i<HText.length();i++)
            {
                if(((HText.charAt(i)>='a' && HText.charAt(i)<='z') || (HText.charAt(i)>='A' && HText.charAt(i)<='Z')))
                {
                    word.append(HText.charAt(i));
                    spaceCnt=0;
                }
                else if(spaceCnt == 0)
                {
                    word.append("-"+counter+"-h");
                    words.add(word.toString());
                    word.setLength(0);
                    spaceCnt++;
                    counter++;
                }
            }
            word.append("-"+counter+"-h");
            words.add(word.toString());
            word.setLength(0);
            counter++;
        }
        return words;
    }





    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }
}