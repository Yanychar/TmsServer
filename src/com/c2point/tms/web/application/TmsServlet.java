package com.c2point.tms.web.application;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;

@WebServlet(value = "/*", asyncSupported = true)
@VaadinServletConfiguration(productionMode = true, ui = TmsApplication.class, widgetset = "com.c2point.tms.web.application.widgetset.TmsserverWidgetset")
public class TmsServlet extends VaadinServlet {


	private static final long serialVersionUID = -5913169856331064106L;

}
