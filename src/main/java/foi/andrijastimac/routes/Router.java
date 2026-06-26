package foi.andrijastimac.routes;

import foi.andrijastimac.controllers.*;
import foi.andrijastimac.server.HttpResponse;

import java.util.List;

public class Router {

    public HttpResponse route(
            String method,
            String path,
            Integer movieId,
            Integer screeningId,
            List<String> seatNumbers,
            String name,
            String email,
            String password,
            Integer reservationId,
            String sessionEmail
    ) {

        if (method.equals("GET") && path.equals("/style.css")) {
            return new CssController().style();
        }

        if (method.equals("GET") && path.equals("/login")) {
            return new UserController().loginForm();
        }

        if (method.equals("POST") && path.equals("/login")) {
            return new UserController().login(email, password);
        }

        if (method.equals("GET") && path.equals("/register")) {
            return new UserController().registerForm();
        }

        if (method.equals("POST") && path.equals("/register")) {
            return new UserController().register(email, password, name);
        }

        if (sessionEmail == null || sessionEmail.isBlank()) {
            return HttpResponse.redirect("/login");
        }

        if (method.equals("GET") && path.equals("/logout")) {
            return new UserController().logout();
        }

        if (method.equals("GET") && (path.equals("/") || path.equals("/movies"))) {
            return new HomeController().index();
        }

        if (method.equals("GET") && path.equals("/screenings")) {
            return new ScreeningController().index(movieId);
        }

        if (method.equals("GET") && path.equals("/seats")) {

            if (screeningId == null) {
                return HttpResponse.notFound();
            }

            return new SeatController().index(screeningId);
        }

        if (method.equals("POST") && path.equals("/reserve")) {
            return new ReservationController()
                    .reserve(seatNumbers, screeningId, sessionEmail);
        }

        if (method.equals("GET") && path.equals("/reservations")) {
            return new ReservationController().myReservations(sessionEmail);
        }

        if (method.equals("GET") && path.equals("/reservation/change")) {

            if (reservationId == null) {
                return HttpResponse.notFound();
            }

            return new ReservationController().changeForm(reservationId);
        }

        if (method.equals("DELETE") && path.equals("/reservation")) {

            if (reservationId == null) {
                return HttpResponse.notFound();
            }

            return new ReservationController().cancel(reservationId, sessionEmail);
        }

        if (method.equals("PUT") && path.equals("/reservation")) {

            if (reservationId == null || seatNumbers == null || seatNumbers.isEmpty()) {
                return HttpResponse.notFound();
            }

            return new ReservationController().changeSeat(reservationId, seatNumbers.get(0));
        }

        return HttpResponse.notFound();
    }
}
