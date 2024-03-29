package com.kalafche.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kalafche.exceptions.CommonException;
import com.kalafche.model.revision.Revision;
import com.kalafche.model.revision.RevisionItem;
import com.kalafche.model.revision.RevisionReport;
import com.kalafche.model.revision.RevisionType;
import com.kalafche.service.RevisionService;

@CrossOrigin
@RestController
@RequestMapping({ "/revision" })
public class RevisionController {
	
	@Autowired
	private RevisionService revisionService;
	
	@PutMapping
	public Revision initiateRevision(@RequestBody Revision revision) throws SQLException, CommonException, InterruptedException {
		return revisionService.initiateRevision(revision);
	}
	
	@GetMapping("/{revisionId}")
	public Revision getRevision(@PathVariable(value = "revisionId") Integer revisionId) {
		return revisionService.getRevision(revisionId);
	}
	
	@GetMapping("/current")
	public Revision getCurrentRevision(@RequestParam(value = "storeId", required = false) Integer storeId) {
		return revisionService.getCurrentRevision(storeId);
	}
	
	@GetMapping("/type")
	public List<RevisionType> getRevisionTypes() {
		return revisionService.getRevisionTypes();
	}
	
	@GetMapping("{revisionId}/item/{barcode}")
	public RevisionItem getRevisionItemByBarcode(@PathVariable (value = "revisionId") Integer revisionId, @PathVariable (value = "barcode") String barcode) {
		return revisionService.getRevisionItemByBarcode(revisionId, barcode);
	}
	
	@GetMapping("/item/{revisionId}")
	public List<RevisionItem> getRevisionItems(@PathVariable (value = "revisionId") Integer revisionId) throws SQLException {
		return revisionService.getRevisionItemsByRevisionId(revisionId);
	}
	
	@PostMapping("/item/plus")
	public Integer plusRevisionItem(@RequestBody RevisionItem revisionItem) throws SQLException {
		return revisionService.updateRevisionItem(revisionItem, 1);
	}
	
	@PostMapping("/item/minus")
	public Integer minusRevisionItem(@RequestBody RevisionItem revisionItem) throws SQLException {
		return revisionService.updateRevisionItem(revisionItem, -1);
	}
	
	@PostMapping("/sync")
	public Integer correctionOfItemQuantityAfterRevision(@RequestBody RevisionItem revisionItem) throws SQLException {
		return revisionService.correctionOfItemQuantityAfterRevision(revisionItem);
	}
	
	@PostMapping
	public Revision finalizeRevision(@RequestBody Revision revision) throws SQLException {
		return revisionService.finalizeRevision(revision);
	}
	
	@GetMapping
	public RevisionReport searchRevisions(@RequestParam(value = "startDateMilliseconds") Long startDateMilliseconds, 
			@RequestParam(value = "endDateMilliseconds") Long endDateMilliseconds, @RequestParam(value = "storeId") Integer storeId, @RequestParam(value = "typeId", required = false) Integer typeId) {
		return revisionService.searchRevisions(startDateMilliseconds, endDateMilliseconds, storeId, typeId);
	}
	
}