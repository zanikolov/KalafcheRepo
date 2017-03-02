package com.kalafche.model.revision;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.kalafche.model.KalafcheStore;

//@Entity
//@Table(name="revision")
public class Revision {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//	private int id;
//    
//    @Column(name = "start_timestamp", nullable = false)
//	private long startTimestamp;
//    
//    @Column(name = "end_timestamp", nullable = false)
//	private long endTimestamp;
//	
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "kalafche_store_id", nullable = false)
//	private KalafcheStore kalafcheStore;
//	
//	@Column(name = "is_completed", nullable = false)
//	private boolean isCompleted;
//
//	private List<String> revisers;
//
//	public int getId() {
//		return id;
//	}
//
//	public long getStartTimestamp() {
//		return startTimestamp;
//	}
//
//	public void setStartTimestamp(long startTimestamp) {
//		this.startTimestamp = startTimestamp;
//	}
//
//	public long getEndTimestamp() {
//		return endTimestamp;
//	}
//
//	public void setEndTimestamp(long endTimestamp) {
//		this.endTimestamp = endTimestamp;
//	}
//
//	public int getKalafcheStoreId() {
//		return kalafcheStoreId;
//	}
//
//	public void setKalafcheStoreId(int kalafcheStoreId) {
//		this.kalafcheStoreId = kalafcheStoreId;
//	}
//
//	public String getKalafcheStoreName() {
//		return kalafcheStoreName;
//	}
//
//	public void setKalafcheStoreName(String kalafcheStoreName) {
//		this.kalafcheStoreName = kalafcheStoreName;
//	}
//
//	public boolean isCompleted() {
//		return isCompleted;
//	}
//
//	public void setCompleted(boolean isCompleted) {
//		this.isCompleted = isCompleted;
//	}
//
//	public List<String> getRevisers() {
//		return revisers;
//	}
//
//	public void setRevisers(List<String> revisers) {
//		this.revisers = revisers;
//	}

}
