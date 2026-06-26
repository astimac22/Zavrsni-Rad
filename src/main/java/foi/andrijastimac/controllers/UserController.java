package foi.andrijastimac.controllers;

import foi.andrijastimac.models.User;
import foi.andrijastimac.server.HttpResponse;
import foi.andrijastimac.services.TemplateService;
import foi.andrijastimac.services.UserService;

public class UserController {

    private final UserService userService =
            new UserService();

    private final TemplateService templateService =
            new TemplateService();

    public HttpResponse loginForm() {
        return loginForm("");
    }

    public HttpResponse loginForm(String error) {

        String template =
                templateService.loadTemplate("login.html");

        template =
                templateService.replace(template, "error", error);

        return HttpResponse.ok(template);
    }

    public HttpResponse login(String email, String password) {

        if (email == null || password == null
                || email.isBlank() || password.isBlank()) {
            return loginForm("Unesite e-poštu i lozinku.");
        }

        User user =
                userService.findByEmailAndPassword(email, password);

        if (user == null) {
            return loginForm("Pogrešna e-pošta ili lozinka.");
        }

        return HttpResponse.redirectWithCookie(
                "/",
                "user_email=" + user.getEmail() + "; Path=/; HttpOnly"
        );
    }

    public HttpResponse registerForm() {
        return registerForm("");
    }

    public HttpResponse registerForm(String error) {

        String template =
                templateService.loadTemplate("register.html");

        template =
                templateService.replace(template, "error", error);

        return HttpResponse.ok(template);
    }

    public HttpResponse register(String email, String password, String name) {

        if (email == null || password == null || name == null
                || email.isBlank() || password.isBlank() || name.isBlank()) {
            return registerForm("Sva polja su obavezna.");
        }

        if (userService.emailExists(email)) {
            return registerForm("Korisnik s tom e-poštom već postoji.");
        }

        boolean success =
                userService.register(email, password, name);

        if (!success) {
            return registerForm("Greška pri registraciji, pokušajte ponovo.");
        }

        return HttpResponse.redirectWithCookie(
                "/",
                "user_email=" + email + "; Path=/; HttpOnly"
        );
    }

    public HttpResponse logout() {
        return HttpResponse.redirectWithCookie(
                "/login",
                "user_email=; Path=/; Max-Age=0"
        );
    }
}
