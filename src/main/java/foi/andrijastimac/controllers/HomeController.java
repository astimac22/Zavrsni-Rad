package foi.andrijastimac.controllers;

import foi.andrijastimac.models.Movie;
import foi.andrijastimac.server.HttpResponse;
import foi.andrijastimac.services.MovieService;
import foi.andrijastimac.services.TemplateService;

import java.util.List;

public class HomeController {

    private final MovieService movieService =
            new MovieService();

    private final TemplateService templateService =
            new TemplateService();

    public HttpResponse index() {

        List<Movie> movies =
                movieService.getMovies();

        StringBuilder moviesHtml =
                new StringBuilder();

        for (Movie movie : movies) {

            moviesHtml.append("<div class=\"movie-card\">");

            moviesHtml.append("<h2>");
            moviesHtml.append(movie.getTitle());
            moviesHtml.append("</h2>");

            moviesHtml.append("<a href=\"/screenings?movie=");
            moviesHtml.append(movie.getId());
            moviesHtml.append("\">");
            moviesHtml.append("Pogledaj termine");
            moviesHtml.append("</a>");

            moviesHtml.append("</div>");
        }

        String template =
                templateService.loadTemplate("index.html");

        return HttpResponse.ok(
                templateService.replace(
                        template,
                        "movies",
                        moviesHtml.toString()
                )
        );
    }
}
