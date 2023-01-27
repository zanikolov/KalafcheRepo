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

import com.kalafche.model.Revision;
import com.kalafche.model.RevisionItem;
import com.kalafche.model.RevisionType;
import com.kalafche.service.RevisionService;

@CrossOrigin
@RestController
@RequestMapping({ "/revision" })
public class RevisionController {
	
	@Autowired
	private RevisionService revisionService;
	
	@PutMapping
	public Revision initiateRevision(@RequestBody Revision revision) throws SQLException {
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
	
	@PostMapping("/item")
	public Integer findRevisionItem(@RequestBody RevisionItem revisionItem) throws SQLException {
		return revisionService.findRevisionItem(revisionItem);
	}
	
	@PostMapping("/sync")
	public Integer correctionOfItemQuantityAfterRevision(@RequestBody RevisionItem revisionItem) throws SQLException {
		return revisionService.correctionOfItemQuantityAfterRevision(revisionItem);
	}
	
	@PostMapping
	public Revision submitRevision(@RequestBody Revision revision) throws SQLException {
		return revisionService.submitRevision(revision);
	}
	
	@GetMapping
	public List<Revision> searchRevisions(@RequestParam(value = "startDateMilliseconds") Long startDateMilliseconds, 
			@RequestParam(value = "endDateMilliseconds") Long endDateMilliseconds, @RequestParam(value = "storeId") Integer storeId) {
		return revisionService.searchRevisions(startDateMilliseconds, endDateMilliseconds, storeId);
	}
	
}