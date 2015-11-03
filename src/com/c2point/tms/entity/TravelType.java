package com.c2point.tms.entity;

import javax.xml.bind.annotation.XmlEnum;

import org.eclipse.persistence.annotations.ObjectTypeConverter;
import org.eclipse.persistence.annotations.ConversionValue;

@ObjectTypeConverter(name = "TravelTypeConverter", objectType = TravelType.class, dataType = int.class, conversionValues = {
	@ConversionValue(objectValue = "HOME", dataValue = "1"),
	@ConversionValue(objectValue = "WORK", dataValue = "2"), 
	@ConversionValue(objectValue = "UNKNOWN", dataValue = "3") 
})
	
@XmlEnum
public enum TravelType {
	HOME, 
	WORK, 
	UNKNOWN;

}
