package com.c2point.tms.entity;

import javax.xml.bind.annotation.XmlEnum;

import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.ObjectTypeConverter;

@ObjectTypeConverter(name = "ApprovalFlagConverter", objectType = ApprovalFlagType.class, dataType = int.class, conversionValues = {
	@ConversionValue(objectValue = "TO_CHECK", dataValue = "0" ), 
	@ConversionValue(objectValue = "REJECTED", dataValue = "1" ),
	@ConversionValue(objectValue = "APPROVED", dataValue = "2" ),
	@ConversionValue(objectValue = "PROCESSED", dataValue = "3" )
})

@XmlEnum
public enum ApprovalFlagType {
	TO_CHECK, 
	REJECTED,
	APPROVED,
	PROCESSED;

	public boolean allowToBeChanged() {
		return ( this != APPROVED && this != PROCESSED );
	}
	
}
