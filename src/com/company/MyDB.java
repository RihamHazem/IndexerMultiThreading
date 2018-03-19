package com.company;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import javafx.util.Pair;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MyDB {
    private MongoClient mongo;
    private MongoDatabase database;
    private MongoCollection<Document> collection;
    private MongoCollection<Document> URLcollection;

    MyDB() {
        //Creating a Mongo client
        mongo = new MongoClient();
        database = mongo.getDatabase("apt");
        // Accessing the database
        //getting the collection
        collection = database.getCollection("indexer");

        URLcollection = database.getCollection("URLs");
    }

    public ArrayList<Pair<String, Pair<String, Long> >> retrieveURLs() {
        ArrayList<Pair<String, Pair<String, Long> >> arr = new ArrayList<>();
        int i = 0;
        List<Document> URLs = URLcollection.find().into(new ArrayList<Document>());
        for (Document URL : URLs) {
            arr.add( new Pair<>(URL.getString("URL"), new Pair<>( URL.getString("html"), URL.getLong("len") ) ) );
        }
        return arr;
    }

    ////////////////////////////////////////
    public Integer getIndexOfDoc(String word, String URL) {
        int i;
        List<Document> words = (List<Document>) collection.find().into(new ArrayList<Document>());

        for (Document oneword : words) {
            i = 0;
            List<Document> docs = (List<Document>) oneword.get("docs");
            for (Document doc : docs) {
				
				/*System.out.println("word = " + oneword.getString("word")
						+ " course details below");
				System.out.println("URL = " + doc.getString("URL"));*/
                if (doc.getString("URL").equals(URL))
                    return i;
                i++;
            }

        }
        //knowing the word, find the index that contains the given URL URL
		/*System.out.println("nemttttttttttttt");
			for(int i=0;i<50;i++)
			{
				BasicDBObject fields = new BasicDBObject();
				fields.put("word", word);
				fields.put("docs."+i+".URL",URL);
				
				MongoCursor<Document> iterDoc = collection.find(fields).iterator(); 
				if(iterDoc.hasNext())
				{
					//System.out.println("yes");
					return i;
				}
			}*/

        return -1;
    }

    //////////////////////////////////////
    public Integer getIndexOfSyno(String word, String URL, String syno, int j) {
        //Integer j=getIndexOfDoc(word,URL);
        for (int i = 0; i < 10; i++) {
            BasicDBObject fields = new BasicDBObject();
            fields.put("word", word);
            fields.put("docs." + j + ".synos." + i + ".syn", syno);

            MongoCursor<Document> iterDoc = collection.find(fields).iterator();
            if (iterDoc.hasNext()) {
                //System.out.println("yes");
                return i;
            }
        }

        return -1;
    }

    ////////////////////////////////////////
    public void insertNewURL(String URL) {

        collection.insertOne(new Document("URL", URL));
    }

    ///////////////////////////////////////
    public void insertNewWord(String stem, String syn, String URL, String pos, float docLen) {
        float TF = 1 / docLen;
        ArrayList<String> arrayPos = new ArrayList<String>();
        arrayPos.add(pos);
        System.out.println(TF);
        ArrayList<DBObject> arraySynos = new ArrayList<DBObject>();
        BasicDBObject o1 = new BasicDBObject("syn", syn).append("count", 1).append("positions", arrayPos).append("TF", TF);
        arraySynos.add(o1);

        ArrayList<DBObject> arrayURLs = new ArrayList<DBObject>();
        BasicDBObject o2 = new BasicDBObject("URL", URL).append("synos", arraySynos);
        arrayURLs.add(o2);

        ArrayList<Document> arrayDocs = new ArrayList<Document>();
        arrayDocs.add(new Document("word", stem).append("docs", arrayURLs));

        collection.insertMany(arrayDocs);
    }

    /////////////////////////////////////
    public void insertNewDoc(String stem, String syn, String URL, String pos, float docLen) {

        float TF = 1 / docLen;
        ArrayList<DBObject> arraySynos = new ArrayList<DBObject>();
        ArrayList<String> arrayPos = new ArrayList<String>();
        ArrayList<DBObject> arrayDocs = new ArrayList<DBObject>();

        arrayPos.add(pos);
        BasicDBObject obj1 = new BasicDBObject("syn", syn).append("count", 1).append("positions", arrayPos).append("TF", TF);
        arraySynos.add(obj1);


        BasicDBObject doc = new BasicDBObject();
        doc.put("URL", URL);
        doc.put("synos", arraySynos);
        arrayDocs.add(doc);
        BasicDBObject update = new BasicDBObject();

        BasicDBObject match = new BasicDBObject("word", stem);

        update.put("$push", new BasicDBObject("docs", doc));
        //match is choosing criteria and update is the chosen fieled
        System.out.println("newdoc");
        collection.updateMany(match, update);
    }

    /////////////////////////////////////////////////////
    public void insertNewSyno(String stem, String syn, String URL, String pos, float docLen) {
        float TF = 1 / docLen;
        ArrayList<String> arrayPos = new ArrayList<String>();
        arrayPos.add(pos);

        BasicDBObject o1 = new BasicDBObject("syn", syn).append("count", 1).append("positions", arrayPos).append("TF", TF);
        //MATCH
        BasicDBObject match = new BasicDBObject();
        match.put("word", stem);
        match.put("docs.URL", URL);

        //UPDATE
        BasicDBObject update = new BasicDBObject();
        update.put("$push", new BasicDBObject().append("docs." + getIndexOfDoc(stem, URL) + ".synos", o1));

        collection.updateMany(match, update);

    }

    //////////////////////////////////////////////
    public void insertNewPos(String stem, String syn, String URL, String pos, float docLen) {

        Integer i = getIndexOfDoc(stem, URL);
        Integer j = getIndexOfSyno(stem, URL, syn, i);
        //MATCHING PARAMETERS
        BasicDBObject match0 = new BasicDBObject();
        match0.put("word", stem);
        match0.put("docs." + i + ".URL", URL);
        match0.put("docs." + i + ".synos." + j + ".syn", syn);
        //UPDATE
        BasicDBObject update0 = new BasicDBObject();

        update0.put("$inc", new BasicDBObject("docs." + i + ".synos." + j + ".count", 1));
        collection.updateMany(match0, update0);


        //MATCHING PARAMETERS
        BasicDBObject match = new BasicDBObject();
        match.put("word", stem);

        //UPDATE
        BasicDBObject update = new BasicDBObject();

        update.put("$push", new BasicDBObject("docs." + i + ".synos." + j + ".positions", pos));

        collection.updateMany(match, update);

    }

    /////////////////////////////////////
    public void removeURL(String URL) {

        //MATCHING PARAMETERS
        BasicDBObject match = new BasicDBObject();//match is empty
        BasicDBObject obj = new BasicDBObject();
        obj.put("URL", URL);
        //UPDATE
        BasicDBObject update = new BasicDBObject();
        update.put("$pull", new BasicDBObject("docs", obj));

        collection.updateMany(match, update);
    }

    /////////////////////////////////////
    public boolean checkWord(String stem) {
        BasicDBObject match = new BasicDBObject();
        match.put("word", stem);
        // Getting the iterable object
        FindIterable<Document> iterDoc = collection.find(match);
        int i = 0;

        // Getting the iterator
        Iterator it = iterDoc.iterator();
        if (it.hasNext()) {
            i++;
        }
        if (i >= 1)
            return true;
        return false;
    }

    ////////////////////////////////////////
    public boolean checkWordDoc(String stem, String URL) {
        Integer i = getIndexOfDoc(stem, URL);
        BasicDBObject match = new BasicDBObject();
        match.put("word", stem);
        match.put("docs." + i + ".URL", URL);
        // Getting the iterable object
        FindIterable<Document> iterDoc = collection.find(match);
        int j = 0;

        // Getting the iterator
        Iterator it = iterDoc.iterator();

        if (it.hasNext()) {
            j++;
        }
        if (j >= 1)
            return true;
        return false;
    }

    //////////////////////////////////////////////////
    public boolean checkWordDocSyno(String stem, String URL, String syno) {
        Integer i = getIndexOfDoc(stem, URL);
        Integer j = getIndexOfSyno(stem, URL, syno, i);
        BasicDBObject match = new BasicDBObject();
        match.put("word", stem);
        match.put("docs." + i + ".URL", URL);
        match.put("docs." + i + ".synos." + j + ".syn", syno);
        // Getting the iterable object
        FindIterable<Document> iterDoc = collection.find(match);
        int k = 0;

        // Getting the iterator
        Iterator it = iterDoc.iterator();

        if (it.hasNext()) {
            k++;
        }
        if (k >= 1)
            return true;
        return false;
    }
}
