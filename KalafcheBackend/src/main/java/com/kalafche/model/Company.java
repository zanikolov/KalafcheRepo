package com.kalafche.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Company {

	private Integer id;
	private String name;
	private String code;
	private String address;
	private String city;
	private String country;
	private String accountablePerson;
	private String number;
	private String vatNumber;
	private String bank;
	private String iban;
	private String bic;
	
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
		Company other = (Company) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
