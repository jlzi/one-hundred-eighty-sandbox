package org.ys.finder;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final int NUMBER_OF_THREADS = 100;

    public static void main(String[] args) {
        StringGenerator generator = new StringGenerator();
        generator.start();

        final ExecutorService service = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            service.submit(() -> {
                CloseableHttpClient httpClient = HttpClients.createDefault();
                try {
                    HttpHost target = new HttpHost("sel.i5.5cm.ru", 80, "http");
                    RequestConfig config = RequestConfig.custom().build();

                    while (true) { // TODO
                        String id = generator.next();
                        HttpGet request = new HttpGet("/i/" + id + ".png");
                        request.setConfig(config);

                        try (CloseableHttpResponse response = httpClient.execute(target, request)) {
                            int statusCode = response.getStatusLine().getStatusCode();
                            if (statusCode != 404) {
                                String url = "" + target + request.getURI();
                                System.out.println("SUCCESS: " + url);
                                FileUtils.writeStringToFile(new File("urls.txt"), url + LINE_SEPARATOR, true);
                            }
                            EntityUtils.consume(response.getEntity());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        httpClient.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
