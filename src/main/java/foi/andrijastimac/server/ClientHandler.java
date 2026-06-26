package foi.andrijastimac.server;

import foi.andrijastimac.routes.Router;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler {

    private final Socket client;

    public ClientHandler(Socket client) {
        this.client = client;
    }

    public void handle() {

        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                client.getInputStream()
                        )
                );

                PrintWriter out = new PrintWriter(
                        client.getOutputStream()
                )
        ) {

            String requestLine = in.readLine();

            if (requestLine == null) {
                client.close();
                return;
            }

            String[] requestParts =
                    requestLine.split(" ");

            String method =
                    requestParts[0];

            String fullPath =
                    requestParts[1];

            String path =
                    fullPath;

            String query =
                    "";

            if (fullPath.contains("?")) {

                String[] split =
                        fullPath.split("\\?", 2);

                path =
                        split[0];

                query =
                        split[1];
            }

            String header;
            int contentLength = 0;
            String sessionEmail = null;

            while (
                    (header = in.readLine()) != null
                            &&
                            !header.isEmpty()
            ) {

                if (
                        header.startsWith(
                                "Content-Length:"
                        )
                ) {

                    contentLength =
                            Integer.parseInt(
                                    header.substring(15)
                                            .trim()
                            );
                }

                if (header.startsWith("Cookie:")) {

                    String cookieString =
                            header.substring(7).trim();

                    for (String part : cookieString.split(";")) {

                        String[] kv =
                                part.trim().split("=", 2);

                        if (kv.length == 2
                                && kv[0].trim().equals("user_email")) {
                            sessionEmail = kv[1].trim();
                        }
                    }
                }
            }

            String body = "";

            if (contentLength > 0) {

                char[] buffer =
                        new char[contentLength];

                int bytesRead =
                        in.read(
                                buffer,
                                0,
                                contentLength
                        );

                body =
                        new String(buffer, 0, bytesRead);
            }

            Integer movieId = null;
            Integer screeningId = null;
            List<String> seatNumbers = new ArrayList<>();
            String name = null;
            String email = null;
            String password = null;
            Integer reservationId = null;

            if (!query.isBlank()) {

                String[] parameters =
                        query.split("&");

                for (String parameter : parameters) {

                    String[] pair =
                            parameter.split("=", 2);

                    if (pair.length != 2) {
                        continue;
                    }

                    String key = pair[0];
                    String value = decode(pair[1]);

                    if (key.equals("movie")) {
                        movieId = Integer.parseInt(value);
                    }

                    if (key.equals("screening")) {
                        screeningId = Integer.parseInt(value);
                    }

                    if (key.equals("id")) {
                        reservationId = Integer.parseInt(value);
                    }
                }
            }

            if (!body.isBlank()) {

                String[] parameters =
                        body.split("&");

                for (String parameter : parameters) {

                    String[] pair =
                            parameter.split("=", 2);

                    if (pair.length != 2) {
                        continue;
                    }

                    String key = pair[0];
                    String value = decode(pair[1]);

                    if (key.equals("seat")) {
                        seatNumbers.add(value);
                    }

                    if (key.equals("screening")) {
                        screeningId = Integer.parseInt(value);
                    }

                    if (key.equals("name")) {
                        name = value;
                    }

                    if (key.equals("email")) {
                        email = value;
                    }

                    if (key.equals("password")) {
                        password = value;
                    }

                    if (key.equals("id")) {
                        reservationId = Integer.parseInt(value);
                    }

                    if (key.equals("_method")) {
                        method = value;
                    }
                }
            }

            Router router =
                    new Router();

            HttpResponse response =
                    router.route(
                            method,
                            path,
                            movieId,
                            screeningId,
                            seatNumbers,
                            name,
                            email,
                            password,
                            reservationId,
                            sessionEmail
                    );

            if (response.isRedirect()) {

                out.println("HTTP/1.1 302 Found");

                if (response.getCookie() != null) {
                    out.println("Set-Cookie: " + response.getCookie());
                }

                out.println("Location: " + response.getRedirect());
                out.println();

            } else if (response.getStatus() == 404) {

                out.println("HTTP/1.1 404 Not Found");
                out.println("Content-Type: text/html");
                out.println();
                out.println("<h1>404 Not Found</h1>");

            } else {

                String contentType =
                        path.endsWith(".css") ? "text/css" : "text/html";

                out.println("HTTP/1.1 200 OK");

                if (response.getCookie() != null) {
                    out.println("Set-Cookie: " + response.getCookie());
                }

                out.println(
                        "Content-Type: "
                                + contentType
                                + "; charset=utf-8"
                );

                out.println();
                out.println(response.getBody());
            }

            out.flush();
            client.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String decode(String value) {

        try {
            return URLDecoder.decode(
                    value,
                    StandardCharsets.UTF_8
            );
        } catch (Exception e) {
            return value;
        }
    }
}
