package org.ys.finder;

import org.apache.commons.io.FileUtils;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 */
public class Main {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static void main(String[] args) {
        StringGenerator generator = new StringGenerator();
        generator.start();

        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpHost target = new HttpHost("sel.i5.5cm.ru", 80, "http");
//            HttpHost proxy = new HttpHost("proxy.t-systems.ru", 3128, "http");

//            RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
            RequestConfig config = RequestConfig.custom().build();

            while (true) { // TODO
                String id = generator.next();
                System.out.println(id);
                HttpGet request = new HttpGet("/i/" + id + ".png");
                request.setConfig(config);

                //            System.out.println("Executing request " + request.getRequestLine() + " to " + target + " via " + proxy);

                try (CloseableHttpResponse response = httpClient.execute(target, request)) {
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode != 404) {
                        String url = "" + target + request.getURI();
                        System.out.println("SUCCESS: " + url);
                        FileUtils.writeStringToFile(new File("d:/outcome.txt"), url + LINE_SEPARATOR, true);
                    }
//                    else {
//                        System.out.println("tried: " + request.getURI() + ", got " + statusCode);
//                    }
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
    }


    public void test1() {
        HttpProcessor httpproc = HttpProcessorBuilder.create()
                .add(new RequestContent())
                .add(new RequestTargetHost())
                .add(new RequestConnControl())
                .add(new RequestUserAgent("Test/1.1"))
                .add(new RequestExpectContinue(true)).build();

        HttpRequestExecutor requestExecutor = new HttpRequestExecutor();

        HttpCoreContext coreContext = HttpCoreContext.create();
        HttpHost host = new HttpHost("sel.i5.5cm.ru", 80);
        coreContext.setTargetHost(host);

        DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(8 * 1024);
        ConnectionReuseStrategy connStrategy = DefaultConnectionReuseStrategy.INSTANCE;

        try {

            String[] targets = {"/i/kQns.png"};

            for (String target : targets) {
                if (!conn.isOpen()) {
                    Socket socket = new Socket(host.getHostName(), host.getPort());
                    conn.bind(socket);
                }
                BasicHttpRequest request = new BasicHttpRequest("GET", target);
                System.out.println(">> Request URI: " + request.getRequestLine().getUri());

                requestExecutor.preProcess(request, httpproc, coreContext);
                HttpResponse response = requestExecutor.execute(request, conn, coreContext);
                requestExecutor.postProcess(response, httpproc, coreContext);

                System.out.println("<< Response: " + response.getStatusLine());
                System.out.println(EntityUtils.toString(response.getEntity()));
                System.out.println("==============");
                if (!connStrategy.keepAlive(response, coreContext)) {
                    conn.close();
                } else {
                    System.out.println("Connection kept alive...");
                }
            }
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
