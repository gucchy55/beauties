package model;

import java.util.Calendar;
import java.util.Date;

public class RecordTableItemForMove {
	
	private final int mFromBookId;
	private final RecordTableItem mFromRecordTableItem;
	private final RecordTableItem mToRecordTableItem;
	
	// 新規追加用（ActIdあり）
	public RecordTableItemForMove(int pFromBookId, RecordTableItem pToRecord) {
		this.mFromBookId = pFromBookId;
		this.mToRecordTableItem = pToRecord;
		this.mFromRecordTableItem = null;
	}
	
	// 変更用（ActIdなし）
	public RecordTableItemForMove(RecordTableItem pFromRecord, RecordTableItem pToRecord) {
		this.mFromRecordTableItem = pFromRecord;
		this.mToRecordTableItem = pToRecord;
		this.mFromBookId = pFromRecord.getBookId();
	}
	
	public int getFromBookId() {
		return mFromBookId;
	}
	public int getToBookId() {
		return mToRecordTableItem.getBookId();
	}
	public Date getDate() {
		return mToRecordTableItem.getDate();
	}
	public int getValue() {
		return mToRecordTableItem.getIncome();
	}
	public int getFrequency() {
		return mToRecordTableItem.getFrequency();
	}
	public String getNote() {
		return mToRecordTableItem.getNote();
	}
	public int getYear() {
		return mToRecordTableItem.getYear();
	}
	public int getMonth() {
		return mToRecordTableItem.getMonth();
	}
	public Calendar getCal() {
		return mToRecordTableItem.getCal();
	}
	public int getFromActId() {
		return mFromRecordTableItem.getId();
	}
	public int getToActId() {
		return mToRecordTableItem.getId();
	}
	public RecordTableItem getToRecord() {
		return mToRecordTableItem;
	}
	public int getGroupId() {
		return mToRecordTableItem.getGroupId();
	}

}
