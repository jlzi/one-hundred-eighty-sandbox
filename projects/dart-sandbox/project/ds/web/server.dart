import 'dart:io';

main() {
  HttpServer.bind(InternetAddress.LOOPBACK_IP_V4, 8080).then((server) {
    print("Serving at ${server.address}:${server.port}");
    server.listen((HttpRequest request) {
      request.response
        ..headers.contentType = new ContentType("text", "plain", charset: "utf-8")
        ..write('Hello, world')
        ..close();
    });
  });
}
