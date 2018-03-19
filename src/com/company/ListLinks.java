package com.company;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class ListLinks {
    public void work(String html) throws IOException {
        Document doc = Jsoup.parse(html);
        Elements links = doc.select("a[href]");
        Elements paragraphs = doc.select("p");
        Elements media = doc.select("[src]");
        Elements imports = doc.select("link[href]");
        Elements headers = doc.select("h2,h1");
        int counter=0, spaceCnt=0;
//        print(doc.toString());
        print("\nParagraphs: (%d)", paragraphs.size());

        PrintWriter writer = new PrintWriter("in.txt", "UTF-8");
        System.out.print("printing the words in each paragraph\n");
        for (Element p1 : paragraphs) {
            print("* p: %s ", trim(p1.text(), 100));
            String PText = p1.text();


            for(int i=0;i<PText.length();i++)
            {
                if(((PText.charAt(i)>='a' && PText.charAt(i)<='z') || (PText.charAt(i)>='A' && PText.charAt(i)<='Z')))
                {
                    System.out.print(PText.charAt(i));
                    writer.print(PText.charAt(i));
                    spaceCnt=0;
                }
                else if(spaceCnt == 0)
                {
                    writer.println("-"+counter+"-p");
                    System.out.println("-"+counter+"-p");
                    spaceCnt++;
                    counter++;
                }
            }
            writer.println("-"+counter+"-p");
            System.out.println("-"+counter+"-p");
            counter++;
        }

        print("\nheaders: (%d)", headers.size());

        System.out.print("printing the words in each header\n");
        for (Element h1 : headers) {
            print("* H: %s ", trim(h1.text(), 100));
            String HText = h1.text();


            for(int i=0;i<HText.length();i++)
            {
                if(((HText.charAt(i)>='a' && HText.charAt(i)<='z') || (HText.charAt(i)>='A' && HText.charAt(i)<='Z')))
                {
                    System.out.print(HText.charAt(i));
                    writer.print(HText.charAt(i));
                    spaceCnt=0;
                }
                else if(spaceCnt == 0)
                {
                    writer.println("-"+counter+"-h");
                    System.out.println("-"+counter+"-h");
                    spaceCnt++;
                    counter++;
                }
            }
            writer.println("-"+counter+"-h");
            System.out.println("-"+counter+"-h");
            counter++;
        }
        writer.close();
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