package com.c2point.tms.resources;

import java.util.Date;

import com.c2point.tms.entity.TmsAccount;
import com.c2point.tms.util.DateUtil;

public class AuthResp {
	TmsAccount account;
	Date date = DateUtil.getDate();

	public AuthResp( TmsAccount account ) {
		this.account = account;
	}

	public TmsAccount getAccount() { return account; }
	public Date getDate() { return date; }
}
