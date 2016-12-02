package com.zt;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Get extends javax.servlet.http.HttpServlet {

  PrintWriter writer;

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) {
    try {
      // Thread.sleep is here just to slow down the request on purpose
      Thread.sleep(500);
      resp.setHeader("Content-Type", "application/json");
      writer = resp.getWriter();
      String json = fromCouchbase();
      writer.write(json);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      writer.flush();
      writer.close();
    }

  }

  private String fromCouchbase() throws UnknownHostException, InterruptedException {
    System.out.println("Pulling data from Couchbase");

    final CouchbaseCluster cluster = CouchbaseCluster.create("localhost");
    final Bucket supplements = cluster.openBucket("supplements");

    N1qlQueryResult supplementsResult = supplements.query(
        N1qlQuery.simple("select name, price_id from supplements where `type`=\"food\""));

    List<String> items = new ArrayList<>();
    for (N1qlQueryRow supplementRow : supplementsResult) {
      JsonObject jo = supplementRow.value();

      System.out.println(jo);

      String name = jo.getString("name");
      String priceId = jo.getString("price_id");

      N1qlQueryResult priceResult = supplements.query(
          N1qlQuery.simple(
              "select `value` from supplements where `type`=\"price\" and id =\"" + priceId + "\""));

      N1qlQueryRow priceRow = priceResult.allRows().get(0);
      String value = priceRow.value().getString("value");

      String item = "{\"name\": \"" + name + "\", \"price\": \"" + value + "\"}";
      items.add(item);
    }
    cluster.disconnect();

    System.out.println(items);

    String json = "[" + String.join(",", items) + "]";

    return json;
  }


}
