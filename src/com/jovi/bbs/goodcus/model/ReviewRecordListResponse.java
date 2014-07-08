package com.jovi.bbs.goodcus.model;

import java.util.Vector;

public class ReviewRecordListResponse
{
	Vector<ReviewRecord> recordList;
	
	public Vector<ReviewRecord> getRecordList()
	{
		return recordList;
	}
	
	public void setRecordList(Vector list)
	{
		this.recordList = list;
	}
}
