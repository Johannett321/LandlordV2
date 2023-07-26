package com.johansvartdal.landlord.webserver;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class WebServerManager {

    private Server server;

    public WebServerManager() {
        server = new Server(25566); // replace with your preferred port

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Add your servlets here
        context.addServlet(new ServletHolder(new PageHome()),"/");
        context.addServlet(new ServletHolder(new HomeDataSupplier()), "/homedata/");

        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static StringBuilder getSiteHeader() {
        StringBuilder siteHeader = new StringBuilder();
        siteHeader.append("<html>");
        siteHeader.append("<head>");
        siteHeader.append("<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/css/bootstrap.min.css\" integrity=\"sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T\" crossorigin=\"anonymous\">\n");
        siteHeader.append("<style>" +
                "body { " +
                "font-family: Arial, sans-serif; " +
                "background-color: #282c34; " +
                "color: #61dafb; " +
                "margin: 0; " +
                "}" +
                "</style>");
        siteHeader.append("</head>");
        siteHeader.append("<body>");
        return siteHeader;
    }

    protected static StringBuilder getSiteEnding() {
        StringBuilder siteEnding = new StringBuilder();
        siteEnding.append("<script src=\"https://code.jquery.com/jquery-3.3.1.slim.min.js\" integrity=\"sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo\" crossorigin=\"anonymous\"></script>");
        siteEnding.append("<script src=\"https://cdn.jsdelivr.net/npm/popper.js@1.14.7/dist/umd/popper.min.js\" integrity=\"sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1\" crossorigin=\"anonymous\"></script>\n");
        siteEnding.append("<script src=\"https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/js/bootstrap.min.js\" integrity=\"sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM\" crossorigin=\"anonymous\"></script>\n");
        siteEnding.append("</body>");
        siteEnding.append("</html>");
        return siteEnding;
    }
}
