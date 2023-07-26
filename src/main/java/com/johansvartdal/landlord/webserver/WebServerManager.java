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
        context.addServlet(new ServletHolder(new PageHome()),"/*");

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
        siteHeader.append("<style>" +
                "body { " +
                "font-family: Arial, sans-serif; " +
                "background-color: #282c34; " +
                "color: #61dafb; " +
                "display: flex; " +
                "justify-content: center; " +
                "align-items: center; " +
                "height: 100vh; " +
                "margin: 0; " +
                "}" +
                "</style>");
        siteHeader.append("</head>");
        siteHeader.append("<body>");
        return siteHeader;
    }

    protected static StringBuilder getSiteEnding() {
        StringBuilder siteEnding = new StringBuilder();
        siteEnding.append("</body>");
        siteEnding.append("</html>");
        return siteEnding;
    }
}
