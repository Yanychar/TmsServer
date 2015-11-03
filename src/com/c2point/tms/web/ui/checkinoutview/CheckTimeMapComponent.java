package com.c2point.tms.web.ui.checkinoutview;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.CheckInOutRecord;
import com.c2point.tms.entity.GeoCoordinates;
import com.c2point.tms.util.DateUtil;
import com.c2point.tms.util.location.GeoDistanceValidator;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.BaseTheme;

public class CheckTimeMapComponent extends CustomComponent {

	@SuppressWarnings("unused")
	private static Logger 		logger = LogManager.getLogger( CheckTimeMapComponent.class.getName());

	private static final long 	serialVersionUID = 1L;

	private Label			checkTimeLabel;
	private Button 			mapButton;

	private GeoDistanceValidator  geoValidator;

	private GeoDistanceValidator.ValidationResult	geoValidationResult;


	public enum ShowType { NONE, IN, OUT };
	private ShowType 		checkInFlag;

	private CheckInOutRecord record;

	public CheckTimeMapComponent( CheckInOutRecord record, ShowType checkInFlag ) {

		this( record );

		this.checkInFlag = checkInFlag;
	}

	private CheckTimeMapComponent( CheckInOutRecord record ) {
		super();

		this.record = record;
		this.geoValidator = new GeoDistanceValidator( -1 );
		

		this.checkInFlag = ShowType.NONE;

		initView();

	}

	private void initView() {

		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing( true );
		setCompositionRoot( layout );

		checkTimeLabel = new Label();
		checkTimeLabel.setContentMode( ContentMode.HTML );

		mapButton = new Button();
		mapButton.setStyleName( BaseTheme.BUTTON_LINK );

		validateWarningFlag();
		showTime();
		showIcon();

		layout.addComponent( checkTimeLabel );
		layout.addComponent( mapButton );
		layout.setComponentAlignment( checkTimeLabel, Alignment.MIDDLE_LEFT );
	}

	public CheckInOutRecord getCheckInOutRecord() { return record; }

	private String getTimeString() {

		String	timeString = null;
		String	shiftString = null;

		if ( checkInFlag == ShowType.IN ) {

			try {
				timeString = DateUtil.timeToString( this.record.getDateCheckedIn());
			} catch ( Exception e ) {
				timeString = null;
			}

		} else if ( checkInFlag == ShowType.OUT ) {

			try {
				timeString = DateUtil.timeToString( this.record.getDateCheckedOut());
			} catch ( Exception e ) {
				timeString = null;
			}

			try {
				int tmp = DateUtil.differenceInDays( record.getDateCheckedIn(), record.getDateCheckedOut());
				if ( tmp > 0 ) {
					shiftString = Integer.toString( tmp );
				}

			} catch ( Exception e ) {
				shiftString = null;
			}
		}

		return ( timeString != null ? timeString : "" ) + ( shiftString != null ? "(" + shiftString + ")" : "" );
	}

	private void showTime() {

		String	timeString = getTimeString();

		if ( getWarning() == GeoDistanceValidator.ValidationResult.TOO_FAR) {
			checkTimeLabel.setValue( "<b><font color=\"red\">" + timeString + "</font></b>" );
		} else {
			checkTimeLabel.setValue( timeString );
		}
	}


	private void showIcon() {

		GeoCoordinates baseC = record.getProject().getGeo();
		GeoCoordinates inOutC;
		String timeValue;

		boolean bShow = false;

		bShow = ( baseC != null && baseC.isValid());

		if ( checkInFlag == ShowType.IN  ) {

			timeValue = checkTimeLabel.getValue();
			inOutC = record.getCheckInGeo();

			bShow =    timeValue != null
					&& timeValue.length() > 0
					&& ( bShow || inOutC != null && inOutC.isValid());

		} else if ( checkInFlag == ShowType.OUT  ) {

			timeValue = checkTimeLabel.getValue();
			inOutC = record.getCheckOutGeo();

			bShow =    timeValue != null
					&& timeValue.length() > 0
					&& ( bShow || inOutC != null && inOutC.isValid());

		}

		if ( bShow ) {
			mapButton.setEnabled( true );
			mapButton.setIcon( new ThemeResource( "icons/16/map16.png" ));
		} else {
			mapButton.setIcon( null );
			mapButton.setEnabled( false );
		}
	}


	public void addListener( ClickListener listener ) {
		mapButton.addClickListener( listener );
	}

	public void setupWarningDistance( double distance ) {

		geoValidator.setWarningDistance( distance );

		validateWarningFlag();
		showTime();
		showIcon();
	}

	private void validateWarningFlag() {

		GeoCoordinates baseC = record.getProject().getGeo();
		GeoCoordinates inC = record.getCheckInGeo();
		GeoCoordinates outC = record.getCheckOutGeo();

		if ( checkInFlag == ShowType.IN  ) {

			this.geoValidationResult = geoValidator.validate( inC, baseC );

		} else if ( checkInFlag == ShowType.OUT  ) {

			this.geoValidationResult = geoValidator.validate( outC, baseC );

		}

	}
	public GeoDistanceValidator.ValidationResult getWarning() {
		return geoValidationResult;
	}

}
