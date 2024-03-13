package com.kalafche.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WasteReport extends DataReport {

	private List<Waste> wastes;
	
}
