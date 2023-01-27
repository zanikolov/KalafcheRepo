package com.kalafche.model;

import java.util.List;

public class PastPeriodSaleReport {

	Integer selectedMonthDay;
	Integer selectedMonthMonth;
	Integer selectedMonthYear;
	Integer prevMonthDay;
	Integer prevMonthMonth;
	Integer prevMonthYear;
	Integer prevYearDay;
	Integer prevYearMonth;
	Integer prevYearYear;
	List<PastPeriodTurnover> pastPeriodTurnovers;

	public Integer getSelectedMonthDay() {
		return selectedMonthDay;
	}

	public void setSelectedMonthDay(Integer selectedMonthDay) {
		this.selectedMonthDay = selectedMonthDay;
	}

	public Integer getSelectedMonthMonth() {
		return selectedMonthMonth;
	}

	public void setSelectedMonthMonth(Integer selectedMonthMonth) {
		this.selectedMonthMonth = selectedMonthMonth;
	}

	public Integer getSelectedMonthYear() {
		return selectedMonthYear;
	}

	public void setSelectedMonthYear(Integer selectedMonthYear) {
		this.selectedMonthYear = selectedMonthYear;
	}

	public Integer getPrevMonthDay() {
		return prevMonthDay;
	}

	public void setPrevMonthDay(Integer prevMonthDay) {
		this.prevMonthDay = prevMonthDay;
	}

	public Integer getPrevMonthMonth() {
		return prevMonthMonth;
	}

	public void setPrevMonthMonth(Integer prevMonthMonth) {
		this.prevMonthMonth = prevMonthMonth;
	}

	public Integer getPrevMonthYear() {
		return prevMonthYear;
	}

	public void setPrevMonthYear(Integer prevMonthYear) {
		this.prevMonthYear = prevMonthYear;
	}

	public Integer getPrevYearDay() {
		return prevYearDay;
	}

	public void setPrevYearDay(Integer prevYearDay) {
		this.prevYearDay = prevYearDay;
	}

	public Integer getPrevYearMonth() {
		return prevYearMonth;
	}

	public void setPrevYearMonth(Integer prevYearMonth) {
		this.prevYearMonth = prevYearMonth;
	}

	public Integer getPrevYearYear() {
		return prevYearYear;
	}

	public void setPrevYearYear(Integer prevYearYear) {
		this.prevYearYear = prevYearYear;
	}

	public List<PastPeriodTurnover> getPastPeriodTurnovers() {
		return pastPeriodTurnovers;
	}

	public void setPastPeriodTurnovers(List<PastPeriodTurnover> pastPeriodTurnovers) {
		this.pastPeriodTurnovers = pastPeriodTurnovers;
	}

}
