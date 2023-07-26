package com.johansvartdal.landlord.webserver;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.commands.Upgrade;
import com.johansvartdal.landlord.levels.Level;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;

public class PageHome extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        if (!Main.properties.gameHasStarted()) {
            resp.getWriter().println(WebServerManager.getSiteHeader() + "<h1>" + LangDict.getString("webserver.preparationsPageNotAvailable") + "</h1>" + WebServerManager.getSiteEnding());
            return;
        }

        resp.getWriter().println(Tools.readInternal("web/home.html"));
    }
}
