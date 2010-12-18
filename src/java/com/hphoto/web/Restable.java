package com.hphoto.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class Restable extends javax.servlet.http.HttpServlet
implements javax.servlet.Servlet {
	  
	  protected abstract void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException;
	  protected abstract void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException;
	  protected abstract void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException;
	  protected abstract void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException;
	  
	  /*
	   * @param request
	   * @return request pathinfo split on the '/' ignoring the first '/' so first
	   * element in pathSegment is not the empty string.
	   */
	  private String [] getPathSegments(final HttpServletRequest request) {
	    return request.getPathInfo().substring(1).split("/");
	  }
	  
	  /*
	   * If we can't do the specified Accepts header type.
	   * @param response
	   * @throws IOException
	   */
	  private void doNotAcceptable(final HttpServletResponse response)
	  throws IOException {
	    response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
	  }
	  
	  /*
	   * Resource not found.
	   * @param response
	   * @throws IOException
	   */
	  private void doNotFound(final HttpServletResponse response)
	  throws IOException {
	    response.sendError(HttpServletResponse.SC_NOT_FOUND);
	  }
	  
	  /*
	   * Resource not found.
	   * @param response
	   * @param msg
	   * @throws IOException
	   */
	  private void doNotFound(final HttpServletResponse response, final String msg)
	  throws IOException {
	    response.sendError(HttpServletResponse.SC_NOT_FOUND, msg);
	  }

	  /*
	   * Unimplemented method.
	   * @param response
	   * @param message to send
	   * @throws IOException
	   */
	  private void doMethodNotAllowed(final HttpServletResponse response,
	      final String message)
	  throws IOException {
	    response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, message);
	  }
}
