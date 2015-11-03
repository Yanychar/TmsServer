package com.c2point.tms.tools.exprt;

import com.c2point.tms.util.StringUtils;

public class TestTmp {

	public static void main(String[] args) {


		System.out.println( "User Code = '" + StringUtils.padLeftZero( "", 5 ) + "'" );
		System.out.println( "User Code = '" + StringUtils.padLeftZero( "2", 5 ) + "'" );
		System.out.println( "User Code = '" + StringUtils.padLeftZero( "21", 5 ) + "'" );
		System.out.println( "User Code = '" + StringUtils.padLeftZero( "218", 5 ) + "'" );
		System.out.println( "User Code = '" + StringUtils.padLeftZero( "0218", 5 ) + "'" );
		System.out.println( "User Code = '" + StringUtils.padLeftZero( "00218", 5 ) + "'" );
		System.out.println( "User Code = '" + StringUtils.padLeftZero( "000218", 5 ) + "'" );
		
	}

}
