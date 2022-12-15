package com.kalafche.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//import javax.xml.bind.annotation.XmlRootElement;


//@XmlRootElement (name="products")
public class PhoneHomeProductList implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private List<PhoneHomeProduct> product = new ArrayList<PhoneHomeProduct>();
	 
	    public List<PhoneHomeProduct> getProduct() {
	        return product;
	    }
	 
	    public void setProduct(List<PhoneHomeProduct> product) {
	        this.product = product;
	    }
}
