package beauties.record.model;

import java.util.Calendar;
import java.util.Date;

import beauties.common.model.Book;


public class RecordTableItemForMove {
	
	private final Book mFromBook;
	private final RecordTableItem mFromRecordTableItem;
	private final RecordTableItem mToRecordTableItem;
	
	// 新規追加用（ActIdあり）
	public RecordTableItemForMove(Book pFromBook, RecordTableItem pToRecord) {
		this.mFromBook = pFromBook;
		this.mToRecordTableItem = pToRecord;
		this.mFromRecordTableItem = null;
	}
	
	// 変更用（ActIdなし）
	public RecordTableItemForMove(RecordTableItem pFromRecord, RecordTableItem pToRecord) {
		this.mFromRecordTableItem = pFromRecord;
		this.mToRecordTableItem = pToRecord;
		this.mFromBook = pFromRecord.getBook();
	}
	
	public Book getFromBook() {
		return mFromBook;
	}
	public Book getToBook() {
		return mToRecordTableItem.getBook();
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
	public int getGroupId() {
		return mToRecordTableItem.getGroupId();
	}

}
