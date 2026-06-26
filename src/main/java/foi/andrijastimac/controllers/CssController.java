package foi.andrijastimac.controllers;

import foi.andrijastimac.server.HttpResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CssController {

    public HttpResponse style() {

        try {
            return HttpResponse.ok(
                    Files.readString(
                            Path.of("src/main/resources/css/style.css")
                    )
            );
        } catch (IOException e) {
            return HttpResponse.ok("");
        }
    }
}
