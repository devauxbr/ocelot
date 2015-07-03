/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. 
 */
package fr.hhdev.ocelot.web;

import fr.hhdev.ocelot.Constants;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet to serve ocelot-core.js
 * @author hhfrancois
 */
@WebServlet(urlPatterns = {Constants.SLASH + Constants.OCELOT_CORE+Constants.JS})
public class CoreJSServlet extends HttpServlet {

	/**
	 * This string will be replaced by the contextPath in ocelot-core.js
	 */
	private static final String CTXPATH = "%CTXPATH%";
	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType(Constants.JSTYPE);
		String minify = request.getParameter(Constants.MINIFY_PARAMETER);
		try (Writer out = response.getWriter()) {
			String ctxPath = request.getContextPath();
			URL js = this.getClass().getResource(Constants.SLASH + Constants.OCELOT_CORE+Constants.JS);
			if (Constants.FALSE.equalsIgnoreCase(minify)) {
				try (BufferedReader in = new BufferedReader(new InputStreamReader(js.openStream()))) {
					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						out.write(inputLine.replaceAll(CTXPATH, ctxPath));
						out.write(Constants.BACKSLASH_N);
					}
				}
			} else { // TODO implement minification
				try (BufferedReader in = new BufferedReader(new InputStreamReader(js.openStream()))) {
					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						out.write(inputLine.replaceAll(CTXPATH, ctxPath));
						out.write(Constants.BACKSLASH_N);
					}
				}
			}
		}
	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			  throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			  throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>

}
