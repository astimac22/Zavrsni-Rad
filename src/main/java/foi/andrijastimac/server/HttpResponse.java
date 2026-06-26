package foi.andrijastimac.server;

public class HttpResponse {

    private final int status;
    private final String body;
    private final String cookie;
    private final String redirect;

    private HttpResponse(int status, String body, String cookie, String redirect) {
        this.status = status;
        this.body = body;
        this.cookie = cookie;
        this.redirect = redirect;
    }

    public static HttpResponse ok(String body) {
        return new HttpResponse(200, body, null, null);
    }

    public static HttpResponse redirect(String path) {
        return new HttpResponse(302, null, null, path);
    }

    public static HttpResponse redirectWithCookie(String path, String cookie) {
        return new HttpResponse(302, null, cookie, path);
    }

    public static HttpResponse notFound() {
        return new HttpResponse(404, null, null, null);
    }

    public int getStatus() { return status; }
    public String getBody() { return body; }
    public String getCookie() { return cookie; }
    public String getRedirect() { return redirect; }
    public boolean isRedirect() { return status == 302; }
}
