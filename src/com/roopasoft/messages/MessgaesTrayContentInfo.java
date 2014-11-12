package com.roopasoft.messages;

public class MessgaesTrayContentInfo 
{
	private String subject;
	private String date;
	private String message;
	private String oppname;
	private String thread_id;
	private String last_post_id;
	private String unread;
	
	
	public String getUnread() {
		return unread;
	}
	public void setUnread(String unread) {
		this.unread = unread;
	}
	public String getThread_id() {
		return thread_id;
	}
	public void setThread_id(String thread_id) {
		this.thread_id = thread_id;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getOppname() {
		return oppname;
	}
	public void setOppname(String oppname) {
		this.oppname = oppname;
	}
	/**
	 * @return the last_post_id
	 */
	public String getLast_post_id() {
		return last_post_id;
	}
	/**
	 * @param last_post_id the last_post_id to set
	 */
	public void setLast_post_id(String last_post_id) {
		this.last_post_id = last_post_id;
	}	

	
}
