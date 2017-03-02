package com.kalafche.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="employee")
public class Employee {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "name", nullable = false)	
	private String name;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kalafche_store_id", nullable = false)
	private KalafcheStore kalafcheStore;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "job_responsibility_id", nullable = false)	
	private  JobResponsibility jobResponsibility;
	
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "user_id", cascade = CascadeType.ALL)
	private User user;


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public KalafcheStore getKalafcheStore() {
		return kalafcheStore;
	}

	public void setKalafcheStore(KalafcheStore kalafcheStore) {
		this.kalafcheStore = kalafcheStore;
	}

	public JobResponsibility getJobResponsibility() {
		return jobResponsibility;
	}

	public void setJobResponsibility(JobResponsibility jobResponsibility) {
		this.jobResponsibility = jobResponsibility;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
