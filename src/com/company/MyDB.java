package com.company;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import javafx.util.Pair;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;
import java.util.logging.Filter;

public class MyDB {
    private MongoClient mongo;
    private MongoDatabase database;
    public MongoCollection<Document> collection;
    private MongoCollection<Document> URLcollection;

    MyDB() {
        //Creating a Mongo client
        mongo = new MongoClient();
        database = mongo.getDatabase("apt");
        // Accessing the database
        //getting the collection
        collection = database.getCollection("indexer");

        URLcollection = database.getCollection("URLs");
        BasicDBObject bObj = new BasicDBObject();
        bObj.put("URL", 1);
        collection.createIndex(bObj);
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

    /////////////////////////////////////
    public void removeURL(String URL) {

        // MATCHING PARAMETERS
        BasicDBObject match = new BasicDBObject();//match is empty
        BasicDBObject obj = new BasicDBObject();
        obj.put("URL", URL);
        // UPDATE
        BasicDBObject update = new BasicDBObject();
        update.put("$pull", obj);
        Bson filter = Filters.eq("URL", URL);

        collection.deleteOne(filter);
    }
}
