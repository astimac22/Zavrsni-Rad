package foi.andrijastimac.controllers;

import foi.andrijastimac.models.Reservation;
import foi.andrijastimac.models.Seat;
import foi.andrijastimac.models.User;
import foi.andrijastimac.server.HttpResponse;
import foi.andrijastimac.services.HallService;
import foi.andrijastimac.services.ReservationService;
import foi.andrijastimac.services.TemplateService;
import foi.andrijastimac.services.UserService;

import java.util.List;

public class ReservationController {

    private final ReservationService reservationService =
            new ReservationService();

    private final HallService hallService =
            new HallService();

    private final TemplateService templateService =
            new TemplateService();

    private final UserService userService =
            new UserService();

    public HttpResponse reserve(
            List<String> seatNumbers,
            int screeningId,
            String sessionEmail
    ) {

        if (seatNumbers == null || seatNumbers.isEmpty()) {
            return HttpResponse.ok(
                    "<h1>Greška: nije odabrano nijedno sjedalo</h1>"
            );
        }

        User user =
                userService.findByEmail(sessionEmail);

        if (user == null) {
            return HttpResponse.redirect("/login");
        }

        List<Reservation> reservations =
                reservationService.reserve(
                        seatNumbers,
                        screeningId,
                        user.getName(),
                        user.getEmail()
                );

        if (reservations.isEmpty()) {
            return HttpResponse.ok(
                    "<h1>Greška pri rezervaciji</h1>"
            );
        }

        return buildConfirmation(reservations);
    }

    public HttpResponse myReservations(String sessionEmail) {

        String template =
                templateService.loadTemplate("reservations.html");

        List<Reservation> reservations =
                reservationService.findByEmail(sessionEmail);

        if (reservations.isEmpty()) {

            template = templateService.replace(
                    template,
                    "reservations",
                    "<p class=\"no-reservations\">Nema rezervacija za ovu adresu e-pošte.</p>"
            );

            return HttpResponse.ok(template);
        }

        StringBuilder html = new StringBuilder();

        for (Reservation r : reservations) {

            html.append("<div class=\"reservation-card\">");

            html.append("<div class=\"reservation-info\">");
            html.append("<p class=\"reservation-movie\">")
                    .append(r.getMovieTitle())
                    .append("</p>");
            html.append("<p>Termin: ").append(r.getScreeningTime()).append("</p>");
            html.append("<p>Sjedalo: <strong>")
                    .append(r.getSeatNumber())
                    .append("</strong></p>");
            html.append("<p class=\"reservation-id\">Rezervacija #")
                    .append(r.getId())
                    .append("</p>");
            html.append("</div>");

            html.append("<div class=\"reservation-actions\">");

            html.append("<a class=\"button button-secondary\" href=\"/reservation/change?id=")
                    .append(r.getId())
                    .append("\">Promijeni sjedalo</a>");

            html.append("<form method=\"POST\" action=\"/reservation\">");
            html.append("<input type=\"hidden\" name=\"_method\" value=\"DELETE\">");
            html.append("<input type=\"hidden\" name=\"id\" value=\"")
                    .append(r.getId()).append("\">");
            html.append("<button type=\"submit\" class=\"button button-danger\">Otkaži</button>");
            html.append("</form>");

            html.append("</div>");
            html.append("</div>");
        }

        template = templateService.replace(template, "reservations", html.toString());

        return HttpResponse.ok(template);
    }

    public HttpResponse cancel(int reservationId, String sessionEmail) {
        reservationService.cancel(reservationId);
        return myReservations(sessionEmail);
    }

    public HttpResponse changeForm(int reservationId) {

        Reservation reservation =
                reservationService.findById(reservationId);

        if (reservation == null) {
            return HttpResponse.notFound();
        }

        List<Seat> seats =
                hallService.getSeatsByScreening(
                        reservation.getScreeningId()
                );

        StringBuilder seatsHtml = new StringBuilder();

        for (Seat seat : seats) {

            if (seat.getNumber().equals(reservation.getSeatNumber())) {

                seatsHtml.append(
                        "<div class=\"seat current-seat\" title=\"Vaše trenutno sjedalo\">"
                );
                seatsHtml.append(seat.getNumber());
                seatsHtml.append("</div>");

            } else if (!seat.isReserved()) {

                seatsHtml.append("<form method=\"POST\" action=\"/reservation\">");
                seatsHtml.append("<input type=\"hidden\" name=\"_method\" value=\"PUT\">");
                seatsHtml.append("<input type=\"hidden\" name=\"id\" value=\"")
                        .append(reservationId).append("\">");
                seatsHtml.append("<input type=\"hidden\" name=\"seat\" value=\"")
                        .append(seat.getNumber()).append("\">");
                seatsHtml.append("<button class=\"seat\">")
                        .append(seat.getNumber())
                        .append("</button>");
                seatsHtml.append("</form>");

            } else {

                seatsHtml.append("<div class=\"seat reserved\">")
                        .append(seat.getNumber())
                        .append("</div>");
            }
        }

        String template =
                templateService.loadTemplate("change-seat.html");

        template = templateService.replace(
                template, "reservationId", String.valueOf(reservationId)
        );
        template = templateService.replace(
                template, "currentSeat", reservation.getSeatNumber()
        );
        template = templateService.replace(
                template, "screeningTime", reservation.getScreeningTime()
        );
        template = templateService.replace(
                template, "movieTitle", reservation.getMovieTitle()
        );
        template = templateService.replace(
                template, "seats", seatsHtml.toString()
        );

        return HttpResponse.ok(template);
    }

    public HttpResponse changeSeat(int reservationId, String newSeatNumber) {

        boolean success =
                reservationService.changeSeat(reservationId, newSeatNumber);

        if (!success) {
            return HttpResponse.ok(
                    "<div style=\"text-align:center;margin:40px\">"
                            + "<h1>Sjedalo nije dostupno</h1>"
                            + "<a href=\"/reservation/change?id=" + reservationId
                            + "\">Pokušaj ponovo</a></div>"
            );
        }

        Reservation reservation =
                reservationService.findById(reservationId);

        if (reservation == null) {
            return HttpResponse.ok("<h1>Greška</h1>");
        }

        return buildConfirmation(List.of(reservation));
    }

    private HttpResponse buildConfirmation(List<Reservation> reservations) {

        Reservation first = reservations.get(0);

        StringBuilder ids = new StringBuilder();
        StringBuilder seats = new StringBuilder();

        for (int i = 0; i < reservations.size(); i++) {
            if (i > 0) {
                ids.append(", ");
                seats.append(", ");
            }
            ids.append("#").append(reservations.get(i).getId());
            seats.append(reservations.get(i).getSeatNumber());
        }

        String template =
                templateService.loadTemplate("confirmation.html");

        template = templateService.replace(
                template, "id", ids.toString()
        );
        template = templateService.replace(
                template, "movieTitle", first.getMovieTitle()
        );
        template = templateService.replace(
                template, "seats", seats.toString()
        );
        template = templateService.replace(
                template, "screeningTime", first.getScreeningTime()
        );
        template = templateService.replace(
                template, "name", first.getCustomerName()
        );
        template = templateService.replace(
                template, "email", first.getCustomerEmail()
        );

        return HttpResponse.ok(template);
    }
}
