package com.johansvartdal.landlord.webserver;

import com.johansvartdal.landlord.LangDict;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FaviconSupplier extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Get the real path to the image

        // Read the image into a byte array
        InputStream in = LangDict.class.getResourceAsStream("/web/favicon.ico");
        byte[] bytes = new byte[in.available()];
        in.read(bytes);
        in.close();

        // Set the content type based on file name and send the image
        resp.setContentType("image/x-icon");
        resp.setContentLength(bytes.length);

        // Write the image data to the response output stream
        OutputStream out = resp.getOutputStream();
        out.write(bytes);
        out.flush();
        out.close();
    }
}
