package com.xhx.sync;


/**
 * @author 谢洪鑫
 * @date 2019年3月13日
 */
public class Ticket{
	private int station;	//ticket所在出售窗口
	
	
	public Ticket(int staion){
		this.station = station;
	}
	
	public Ticket(){
		super();
	}
	
	public int getStation() {
		return station;
	}

	public void setStation(int station) {
		this.station = station;
	}

}

