package com.kalafche.model.revision;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MissingRevisionItem {
	
	private Integer itemId;
	private Integer revisionId;
	private Long revisionDate;
	private Integer missingCount;

}
