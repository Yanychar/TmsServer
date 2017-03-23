package com.c2point.tms.web.ui.projectsview;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.c2point.tms.tools.ExportFileIF;
import com.c2point.tms.tools.ImportFileIF;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.FinishedListener;
import com.vaadin.ui.Upload.ProgressListener;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.StartedListener;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

public class ProjectViewToolbar extends HorizontalLayout {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( ProjectViewToolbar.class.getName());

	private static 	String FILENAME_PREFIX = "dataexport";
	private static 	String FILENAME_SUFFIX = ".txt";
	
	private Button 				expButton;

	private ExportFileIF		exportHandler = null;
	private ImportFileIF		importHandler = null;

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

		addComponent( getExportButton());
		addComponent( getImportButton());
		
		
	}

	private Button getExportButton() {

		expButton = new Button( "Export" );
	
//		logger.debug( "Temp file for data export created: " + StringUtils.defaultString( tmpFile.getAbsolutePath()));		

		if ( null == null ) {
			
			Resource res = new FileResource( createTmpFile());
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
							File exportFile = exportHandler.exportFile();
							
							if ( exportFile != null ) {

								logger.debug( "Temp file for data export created: " + StringUtils.defaultString( exportFile.getAbsolutePath()));		
								
								Resource res = new FileResource( exportFile );
								this.setFileDownloadResource( res );
							} else {
								logger.debug( "Failed to export projects into file!" );		
							}
						}
						logger.debug( "Path for downloading specified: " + path );		
						
		            	return super.handleConnectorRequest( request, response, path );
		            }
	
		        };
		        fileDownloader.extend( expButton );
	
		        expButton.addClickListener(  new Button.ClickListener() {
					private static final long serialVersionUID = 1L;

					@Override
					public void buttonClick(ClickEvent event) {
	
						logger.debug( "Export button clicked!" );
						
					}
				});
		}
	        
			return expButton;
	}

//	private Button getImportButton() {
		private Upload getImportButton() {
/*
	       final UploadField uploadField = new UploadField();

	        Button impButton = new Button( "Import" );
	        impButton.addClickListener(new Button.ClickListener() {
	            public void buttonClick(ClickEvent event) {
	                Object value = uploadField.getValue();
//		                mainWindow.showNotification("Value:" + value);
	            	logger.debug( "Import button has been pressed" );
	            }
	        });		
*/		
			final Upload up = new Upload();		
			up.setButtonCaption( "Import" );
			up.setImmediate( true );
			up.setReceiver( new Receiver() {
				private static final long serialVersionUID = 1L;

				@Override
				public OutputStream receiveUpload( String filename, String mimeType ) {
					
					File file;  // File where data will be upload
					FileOutputStream fos = null; // Stream to write to
					
			        try {
			            // Open the file for writing.
			            file = new File( filename );
			            fos = new FileOutputStream( file );
			        } catch (final java.io.FileNotFoundException e) {
			            new Notification("Could not open file<br/>",
			                             e.getMessage(),
			                             Notification.Type.ERROR_MESSAGE)
			                .show(Page.getCurrent());
			            return null;
			        }
			        
			        return fos; // Return the output stream to write to
				}
				
			});
			
			UploadListener listener = new UploadListener();
			up.addFailedListener( listener );
			up.addFinishedListener( listener );
			up.addProgressListener( listener );
			up.addStartedListener( listener );
			up.addSucceededListener( listener );
		
//			return impButton;
			return up;
	}

		
		
	private File createTmpFile() {
		// Create temporal file
		File tmpFile = null;
		try {
			tmpFile = File.createTempFile( FILENAME_PREFIX, FILENAME_SUFFIX );

		} catch (IOException e) {
			logger.error( e.getMessage());
			logger.error( e.getStackTrace());
			
			tmpFile = null;
		}
		
		return tmpFile; 
	}

	class UploadListener implements SucceededListener, FailedListener, FinishedListener, ProgressListener, StartedListener {
		private static final long serialVersionUID = 1L;

		@Override
		public void uploadStarted(StartedEvent event) {

	    	logger.debug( "File '" + event.getFilename() + "' was started to be uploaded into the TMS server" );
			
		}

		@Override
		public void updateProgress(long readBytes, long contentLength) {

	    	logger.debug( "Upload progressing ..." );
			
		}

		@Override
		public void uploadFinished(FinishedEvent event) {

	    	logger.debug( "File '" + event.getFilename() + "' was finished upload into the TMS server" );
			
		}

		@Override
		public void uploadFailed(FailedEvent event) {

	    	logger.debug( "File '" + event.getFilename() + "' was failed to be imported into the TMS server" );
	    	
			
		}

		@Override
		public void uploadSucceeded(SucceededEvent event) {

	    	logger.debug( "File '" + event.getFilename() + "' was successfully uploaded for Import into the TMS server" );

			if ( importHandler != null ) {
				importHandler.importFile( new File( event.getFilename()));
//				logger.debug( "Temp file for data import created: " + StringUtils.defaultString( tmpFile.getAbsolutePath()));		
				
			}
	    	
		}
		
	}

}
