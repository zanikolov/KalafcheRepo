package com.kalafche.model;

public class StoreDto {

	private Integer id;
	private String city;
	private String name;
	private String code;
	private Integer openingHoursFromHr;
	private Integer openingHoursFromMin;
	private Integer openingHoursToHr;
	private Integer openingHoursToMin;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getOpeningHoursFromHr() {
		return openingHoursFromHr;
	}

	public void setOpeningHoursFromHr(Integer openingHoursFromHr) {
		this.openingHoursFromHr = openingHoursFromHr;
	}

	public Integer getOpeningHoursFromMin() {
		return openingHoursFromMin;
	}

	public void setOpeningHoursFromMin(Integer openingHoursFromMin) {
		this.openingHoursFromMin = openingHoursFromMin;
	}

	public Integer getOpeningHoursToHr() {
		return openingHoursToHr;
	}

	public void setOpeningHoursToHr(Integer openingHoursToHr) {
		this.openingHoursToHr = openingHoursToHr;
	}

	public Integer getOpeningHoursToMin() {
		return openingHoursToMin;
	}

	public void setOpeningHoursToMin(Integer openingHoursToMin) {
		this.openingHoursToMin = openingHoursToMin;
	}

	public StoreDto() {
	}

	public StoreDto(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StoreDto other = (StoreDto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
