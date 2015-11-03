package com.c2point.tms.web.reporting.checkinout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.CheckInOutRecord;
import com.c2point.tms.entity.TmsUser;

public abstract class AggregateItem {

	private static Logger logger = LogManager.getLogger( AggregateItem.class.getName());

	protected  long		minutes;

	private AggregateItem	parent;

	private SortedMap<String, AggregateItem>	childsList;

	protected AggregateItem( AggregateItem	parent ) {
		
		this.setParent(parent);
		
		childsList = null;

		this.minutes = 0;
		
	}

	protected AggregateItem() {
		
		this( null );
		
	}

	public AggregateItem getParent() { return parent; }
	public void setParent( AggregateItem parent ) { this.parent = parent;}
	
	public boolean hasChilds() { return ( childsList != null ); }	
	public void setHasChilds() {
		
		if ( childsList == null ) {
			childsList = new TreeMap<String, AggregateItem>();
		}
		
	}
	
	public Collection<AggregateItem> getChildsList() {
		
		List<AggregateItem> list = new ArrayList<AggregateItem>( childsList.values());
		
		Collections.sort( list, new ItemComparator());
		
		return list; 
	}
	
	public SortedMap<String, AggregateItem>	getChilds() {
		
		return childsList; 
	}
	
	public boolean handleRecord( TmsUser user, CheckInOutRecord record ) {
		
		boolean bRes = false;
		
		if ( isValid() && isRecordValid( record )) {
		
			if ( hasChilds()) {
			
				AggregateItem child = findChild( user, record );
					
				if ( child == null ) {
					
					child = createChild( user, record );
					
					if ( child != null ) {
						
						childsList.put( child.getKey(), child );
					}
				}
					
					
				if ( child != null ) {
					
					bRes = child.handleRecord( user, record );
					
					if ( bRes ) {
						
						handleRecordInternally( record );
							
					}
						
				}
			} else {
				// No childs. Last leaf in the the branch
				
				handleRecordInternally( record );
				
				bRes = true;
			}
		} else {
			
			if ( logger.isDebugEnabled())
				logger.debug( "CheckInOutRecord cannot be handled. Invalid =>" + record );
		}
		
		
		return bRes;
	}
	
	
	protected abstract AggregateItem createChild( TmsUser user, CheckInOutRecord record ); 
	protected abstract boolean isValid();
	protected abstract String getKey(); 
	protected abstract String getKey( TmsUser user, CheckInOutRecord record );

	
	protected void handleRecordInternally( CheckInOutRecord record ) {
		
		this.minutes = this.minutes + getPeriodInMinutes( record );
	}
	
	
	protected long getPeriodInMinutes( CheckInOutRecord record ) {
		
		return ( record.getDateCheckedOut().getTime() - record.getDateCheckedIn().getTime()) / 60000;
		
	}
	
	public long getMinutes() { return this.minutes; }
	public float getHours() { return getMinutes() / 60.0f; }
	
	@Override
	public String toString() {
		return "hours=" + getHours();
	}

	protected String getToCompare() {
		
		return getKey();
		
	}

	protected AggregateItem findChild( TmsUser user, CheckInOutRecord record ) {

		String key = null;

		// Find any chils
		if ( getChilds() != null && getChilds().size() > 0 ) {
			
			AggregateItem tmpChild = getChilds().values().iterator().next();
			
			key = tmpChild.getKey( user, record );
			
		}
		
		
		
		if ( key != null ) {
			
			return getChilds().get( key );
		}
		
		return null;
	}
	
	protected boolean isRecordValid( CheckInOutRecord record ) {

		// Check on Top most level only!!!
		boolean bRes = false;
		
		
		if ( getParent() == null ) {
		
			if ( record != null &&
				 record.getDateCheckedIn() != null &&
				 record.getDateCheckedOut() != null &&
				 record.isCheckOutByClient()
			) {
				bRes = true;
			} else {
				
				if ( logger.isDebugEnabled()) {
					
					logger.debug( "Record is not valid because: " 
							+ ( record == null ? "Record == NULL, " : "" )
							+ ( record != null && record.getDateCheckedIn() == null ? "CheckedInDate == NULL, " : "" )
							+ ( record != null && record.getDateCheckedOut() == null ? "CheckedOutDate == NULL, " : "" )
							+ ( record != null && !record.isCheckOutByClient() ? "CheckedOut by Server. Time is not valid" : "" )
					);
					
				}
			}
			
		} else {
			bRes = true;
		}
		
		
		
		return bRes;
		
	}
	
	
	class ItemComparator implements Comparator<AggregateItem> {

	    public int compare( AggregateItem a, AggregateItem b ) {
	    	
	        if ( a.getToCompare() == null ) {
	        	return -1;
	        } else if ( b.getToCompare() == null ) {
	        	return 1;
	        } else {
	        	return a.getToCompare().compareToIgnoreCase( b.getToCompare());
	        }
	    }
	}	
}
