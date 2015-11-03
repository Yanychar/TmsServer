package com.c2point.tms.util.xml;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

public class XMLconverter {
	
	private static boolean formattedOutputFlag = true;
	
	@SuppressWarnings("unchecked")
	public static <T> T initFromXML( Class<T> cls, String xmlStr ) throws JAXBException {

		JAXBContext context;
		context = JAXBContext.newInstance( cls );
		Unmarshaller m = context.createUnmarshaller();
		
		StringBuffer strBuf = new StringBuffer( xmlStr );
		
		return ( T ) m.unmarshal( new StreamSource( new StringReader( strBuf.toString() ) ) );
		
	}

	public static String convertToXML( Object obj, boolean formattedFlag ) throws JAXBException {

		StringWriter sw = new StringWriter();
		JAXBContext context;
		context = JAXBContext.newInstance( obj.getClass() );
		Marshaller m = context.createMarshaller();
		m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, formattedFlag );
		m.marshal( obj, sw );
		return sw.toString();
	}

	public static String convertToXML( Object obj ) throws JAXBException {
		return convertToXML( obj, XMLconverter.formattedOutputFlag );
	}
	
	public static <T> String convertToXML( JAXBElement<T> e, boolean formattedFlag ) throws JAXBException {

		StringWriter sw = new StringWriter();
		JAXBContext context;
		context = JAXBContext.newInstance( e.getClass() );
		Marshaller m = context.createMarshaller();
		m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, formattedFlag );
		m.marshal( e, sw );
		return sw.toString();
	}
	
	public static <T> String convertToXML( JAXBElement<T> e ) throws JAXBException {
		return convertToXML( e, XMLconverter.formattedOutputFlag );
	}
	
	public static void setOutputFormatted() {
		formattedOutputFlag = true;
	}
	
	public static void clearOutputFormatted() {
		formattedOutputFlag = false;
	}
	
}
