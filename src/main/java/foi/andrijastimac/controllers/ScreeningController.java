package foi.andrijastimac.controllers;

import foi.andrijastimac.models.Screening;
import foi.andrijastimac.server.HttpResponse;
import foi.andrijastimac.services.ScreeningService;
import foi.andrijastimac.services.TemplateService;

import java.util.List;

public class ScreeningController {

    private final ScreeningService screeningService =
            new ScreeningService();

    private final TemplateService templateService =
            new TemplateService();

    public HttpResponse index(int movieId) {

        List<Screening> screenings =
                screeningService.getScreenings(
                        movieId
                );

        StringBuilder screeningsHtml =
                new StringBuilder();

        for (Screening screening : screenings) {

            screeningsHtml.append(
                    "<a class=\"screening-card\" href=\"/seats?screening="
            );

            screeningsHtml.append(
                    screening.getId()
            );

            screeningsHtml.append(
                    "\">"
            );

            screeningsHtml.append(
                    screening.getScreeningTime()
            );

            screeningsHtml.append(
                    "</a>"
            );
        }

        String template =
                templateService.loadTemplate(
                        "screenings.html"
                );

        return HttpResponse.ok(
                templateService.replace(
                        template,
                        "screenings",
                        screeningsHtml.toString()
                )
        );
    }
}
