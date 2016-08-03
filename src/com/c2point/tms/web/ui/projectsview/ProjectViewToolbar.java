package com.c2point.tms.web.ui.projectsview;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.easyuploads.UploadField;

import com.c2point.tms.tools.ExportFileIF;
import com.c2point.tms.tools.ImportFileIF;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class ProjectViewToolbar extends HorizontalLayout {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( ProjectViewToolbar.class.getName());

	private static 	String FILENAME_PREFIX = "dataexport";
	private static 	String FILENAME_SUFFIX = "txt";
	
	private Button 				expButton;
	private Button				impButton;

	private ExportFileIF		exportHandler = null;
	private ImportFileIF		importHandler = null;

	private File 				tmpFile = null;
	private FileDownloader 		fileDownloader = null;
	
	
	public ProjectViewToolbar() {
		this( null, null );
	}

	public ProjectViewToolbar( ClickListener impButtonListener, ClickListener expButtonListener ) {
		super();
	
		initView();

	}

	public void setExportHandler( ExportFileIF handler ) { 

		this.exportHandler = handler;			
		
	}
	
	public void setImportHandler( ImportFileIF handler ) { 

		this.importHandler = handler;			
		
	}
	
	private void initView() {

		this.setWidth("100%");
		this.setMargin( true );
		
		Label glue = new Label( "" );
		glue.setWidth("100%");
		
		addComponent( glue );
		setExpandRatio( glue, 1.0f );

//		addComponent( getExportButton());
//		addComponent( getImportButton());
		
		
	}

	private Button getExportButton() {

		expButton = new Button( "Export" );
	
//		logger.debug( "Temp file for data export created: " + StringUtils.defaultString( tmpFile.getAbsolutePath()));		
		
		if ( createTmpFile() != null ) {
			
			Resource res = new FileResource( this.tmpFile );
			FileDownloader fileDownloader = new FileDownloader( res ) {
				private static final long serialVersionUID = 1L;
	
					@Override
		            public boolean handleConnectorRequest( VaadinRequest request, VaadinResponse response, String path) throws IOException
		            {
						/*
		            	if ( ExportButton.this.getHandler() != null )
		            		ExportButton.this.getHandler().handle( tmpFile );
		            	*/
						logger.debug( "To Do: create file for download into local machine" );
						 
						if ( exportHandler != null ) {
							ProjectViewToolbar.this.tmpFile = exportHandler.export();
							logger.debug( "Temp file for data export created: " + StringUtils.defaultString( tmpFile.getAbsolutePath()));		
							
							if ( ProjectViewToolbar.this.tmpFile != null ) {
								
								Resource res = new FileResource( ProjectViewToolbar.this.tmpFile );
								this.setFileDownloadResource( res );
							}
						}
						
		            	return super.handleConnectorRequest( request, response, path );
		            }
	
		        };
		        fileDownloader.extend( expButton );
	
		        expButton.addClickListener(  new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
	
						logger.debug( "Export button clicked!" );
						
					}
				});
		}
	        
			return expButton;
	}

		private Button getImportButton() {

	       final UploadField uploadField = new UploadField();

	        Button impButton = new Button( "Import" );
	        impButton.addClickListener(new Button.ClickListener() {
	            public void buttonClick(ClickEvent event) {
	                Object value = uploadField.getValue();
//		                mainWindow.showNotification("Value:" + value);
	            	logger.debug( "Import button has been pressed" );
	            }
	        });		
		
		
		
		
		return impButton;
	}
		
	private File createTmpFile() {
		// Create temporal file
		this.tmpFile = null;
		try {
			tmpFile = File.createTempFile( FILENAME_PREFIX, FILENAME_SUFFIX );

		} catch (IOException e) {
			logger.error( e.getMessage());
			logger.error( e.getStackTrace());
			
			this.tmpFile = null;
		}
		
		return this.tmpFile; 
	}
}
