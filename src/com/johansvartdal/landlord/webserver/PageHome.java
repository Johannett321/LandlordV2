package com.johansvartdal.landlord.webserver;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bukkit.Bukkit;

import java.io.IOException;

public class PageHome extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int onlinePlayersCount = Bukkit.getServer().getOnlinePlayers().size();

        resp.setContentType("text/html");
        resp.getWriter().println(
                "<html>" +
                        "<head>" +
                        "<style>" +
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
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "<h1>Number of online players: " + onlinePlayersCount + "</h1>" +
                        "</body>" +
                        "</html>");
    }
}
