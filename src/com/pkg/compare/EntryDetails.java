package com.pkg.compare;

import java.util.Date;

public class EntryDetails {

	/** Name of entry, including full path */
	private String _name = null;
	/** Flag to show if it's present or not (might be zero length) */
	private boolean[] _present = new boolean[2];
	/** Sizes of this file, in bytes, in archives */
	private long[] _sizes = new long[2];
	/** Sizes of this file, in bytes, in archives */
	private Date[] _modifiedDate = new Date[2];
	/** SizeChange */
	private SizeChange _sizeChange = new SizeChange();

	/** Constants for entry status */
	public enum EntryStatus
	{
		/** File not in first but in second    */ ADDED,
		/** File found in first, not in second */ REMOVED,
		/** File size different in two files   */ CHANGED_SIZE,
		/** File size same (md5 not checked)   */ SAME_SIZE,
		/** File checksum different            */ CHANGED_SUM,
		/** Files really equal                 */ EQUAL,
		/** Files really equal                 */ DATEMODIFIED,
		/** Files really equal                 */ DATENOTMODIFIED
	};

	/**
	 * @return name of entry
	 */
	public String getName() {
		return _name;
	}

	/**
	 * @param inName name to set
	 */
	public void setName(String inName) {
		_name = inName;
	}

	/**
	 * @param inIndex index, either 0 or 1
	 * @return size of this file in corresponding archive
	 */
	public long getSize(int inIndex)
	{
		if (inIndex < 0 || inIndex > 1) {return 0L;}
		return _sizes[inIndex];
	}

	/**
	 * @param inIndex index, either 0 or 1
	 * @param inSize size of file in bytes
	 */
	public void setSize(int inIndex, long inSize)
	{
		if (inIndex==0 || inIndex==1)
		{
			_sizes[inIndex] = inSize;
			_present[inIndex] = true;
			_sizeChange.update(_sizes[1] - _sizes[0], isChanged());
		}
	}

	/**
	 * @return status of entry
	 */
	public EntryStatus getStatus()
	{
		if (!_present[0] && _present[1]) {return EntryStatus.ADDED;}
		if (_present[0] && !_present[1]) {return EntryStatus.REMOVED;}
		if (_sizes[0] != _sizes[1]) {return EntryStatus.CHANGED_SIZE;}
		if (_sizes[0] == _sizes[1]) {return EntryStatus.SAME_SIZE;}
		return EntryStatus.EQUAL;
	}

	public EntryStatus isDateModified(){
		if (_modifiedDate[0].compareTo(_modifiedDate[1]) != 0) { 

            return EntryStatus.DATEMODIFIED;
        } 
		return EntryStatus.DATENOTMODIFIED;
		
	}
     

	/**
	 * @return size change object
	 */
	public SizeChange getSizeChange()
	{
		return _sizeChange;
	}

	
	/**
	 * @param inIndex index, either 0 or 1
	 * @return last modified date  of this file in corresponding archive
	 */
	public Date get_modifiedDate(int inIndex)
	{
		return _modifiedDate[inIndex];
	}

	/**
	 * @param inIndex index, either 0 or 1
	 * @param ilast modified date file
	 */
	public void set_modifiedDate(int inIndex, Date modifiedDate)
	{
		if (inIndex==0 || inIndex==1)
		{
			_modifiedDate[inIndex] = modifiedDate;
			_present[inIndex] = true;
		}
	}


	/**
	 * @return true if the row represents a change
	 */
	public boolean isChanged()
	{
		EntryStatus status = getStatus();
		return status != EntryStatus.SAME_SIZE && status != EntryStatus.EQUAL;
	}
}
