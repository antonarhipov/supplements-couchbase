package com.zt;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import rx.Observable;
import rx.functions.Func1;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Arrays;

public class Init implements ServletContextListener {


  @Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {
    try {


      System.out.println(">>>>>>>>>>>>>>>>>>>>>>>");
      System.out.println("Init.contextInitialized");
      System.out.println(">>>>>>>>>>>>>>>>>>>>>>>");

      System.out.println("Connecting to Couchbase");
      final CouchbaseCluster cluster = CouchbaseCluster.create("localhost");

      System.out.println("Getting supplements bucket from Couchbase");
      final Bucket supplements = cluster.openBucket("supplements");

      System.out.println("Clean supplements bucket");
      supplements.bucketManager().flush();

      JsonDocument apple = JsonDocument.create("apple", JsonObject.create().put("name", "apple").put("price_id", "1"));
      JsonDocument banana = JsonDocument.create("banana", JsonObject.create()
                                                                    .put("name", "banana")
                                                                    .put("price_id", "2"));
      JsonDocument carrot = JsonDocument.create("carrot", JsonObject.create()
                                                                    .put("name", "carrot")
                                                                    .put("price_id", "3"));
      JsonDocument potato = JsonDocument.create("potato", JsonObject.create()
                                                                    .put("name", "potato")
                                                                    .put("price_id", "4"));
      JsonDocument onion = JsonDocument.create("onion", JsonObject.create().put("name", "onion").put("price_id", "5"));
      JsonDocument melon = JsonDocument.create("melon", JsonObject.create().put("name", "melon").put("price_id", "6"));

      System.out.println("Insert some JSON documents to supplements bucket");

      Observable
          .from(Arrays.asList(apple, banana, carrot, potato, onion, melon))
          .flatMap(new Func1<JsonDocument, Observable<JsonDocument>>() {
            @Override
            public Observable<JsonDocument> call(final JsonDocument docToInsert) {
              return supplements.async().insert(docToInsert);
            }
          })
          .last()
          .toBlocking()
          .single();

      System.out.println("Getting prices bucket from Couchbase");
      final Bucket prices = cluster.openBucket("prices");

      System.out.println("Clean prices bucket");
      prices.bucketManager().flush();

      JsonDocument p1 = JsonDocument.create("1", JsonObject.create().put("id", "1").put("value", "123"));
      JsonDocument p2 = JsonDocument.create("2", JsonObject.create().put("id", "2").put("value", "321"));
      JsonDocument p3 = JsonDocument.create("3", JsonObject.create().put("id", "3").put("value", "333"));
      JsonDocument p4 = JsonDocument.create("4", JsonObject.create().put("id", "4").put("value", "433"));
      JsonDocument p5 = JsonDocument.create("5", JsonObject.create().put("id", "5").put("value", "115"));
      JsonDocument p6 = JsonDocument.create("6", JsonObject.create().put("id", "6").put("value", "16"));

      System.out.println("Insert some JSON documents to prices bucket");

      Observable
          .from(Arrays.asList(p1, p2, p3, p4, p5, p6))
          .flatMap(new Func1<JsonDocument, Observable<JsonDocument>>() {
            @Override
            public Observable<JsonDocument> call(final JsonDocument docToInsert) {
              return prices.async().insert(docToInsert);
            }
          })
          .last()
          .toBlocking()
          .single();

      System.out.println("Disconnect!");
      cluster.disconnect();

      System.out.println("Done!");
    } catch (Exception e) {
      System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>");
      System.out.println("!!!!!!!!!!Dafuq!!!!!!!!!");
      System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>");
      e.printStackTrace();
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent servletContextEvent) {

  }
}
