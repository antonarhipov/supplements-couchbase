package com.zt;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.view.ViewQuery;
import com.couchbase.client.java.view.ViewResult;
import com.couchbase.client.java.view.ViewRow;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.net.UnknownHostException;


public class Stats extends javax.servlet.http.HttpServlet {

  PrintWriter writer;

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) {
    try {
      Thread.sleep(500);
      resp.setHeader("Content-Type", "application/json");
      writer = resp.getWriter();
      String json = getData();
      writer.write(json);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      writer.flush();
      writer.close();
    }

  }

  private String getData() throws UnknownHostException, InterruptedException {
    final CouchbaseCluster cluster = CouchbaseCluster.create("localhost");
    final Bucket supplements = cluster.openBucket("supplements");

    ViewResult result = supplements.query(ViewQuery.from("dev_blah", "blah"));
    ViewRow viewRow = result.allRows().get(0);
    System.out.println("Stats: " + viewRow.value());

    return String.valueOf(viewRow.value());
  }


}
