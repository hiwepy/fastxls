package com.github.hiwepy.fastxls.core.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.validation.ConstraintViolation;

import org.apache.commons.collections4.CollectionUtils;

/**
 * 操作记录结果集合
 */
public class ConstraintViolationResult<M extends RowMap> {
		
	/*
	 *  数据行检查异常
	 */
	private List<RowConstraintViolation<M>> constraintViolations = new CopyOnWriteArrayList<RowConstraintViolation<M>>();
	
	public ConstraintViolationResult(){
	}
 
	public boolean accept(){
		return CollectionUtils.isEmpty(constraintViolations);
	}
	
	public List<RowConstraintViolation<M>> getRowConstraintViolations() {
		return constraintViolations;
	}
	
	public Set<ConstraintViolation<M>> getConstraintViolations() {
		Set<ConstraintViolation<M>> constraintViolations = new HashSet<>();
		for (RowConstraintViolation<M> rowConstraintViolation : getRowConstraintViolations()) {
			constraintViolations.addAll(rowConstraintViolation.getConstraintViolations());
		}
		return constraintViolations;
	}

	public void setRowConstraintViolations(List<RowConstraintViolation<M>> constraintViolations) {
		if(null != constraintViolations) {
			this.constraintViolations = constraintViolations;
		}
	}
	
	public void addConstraintViolation(RowConstraintViolation<M> constraintViolation) {
		if(null != constraintViolation && CollectionUtils.isNotEmpty(constraintViolation.getConstraintViolations())) {
			this.constraintViolations.add(constraintViolation);
		}
	}
	
	public void addConstraintViolations(List<RowConstraintViolation<M>> constraintViolations) {
		for (RowConstraintViolation<M> constraintViolation : constraintViolations) {
			this.addConstraintViolation(constraintViolation);
		}
	}
	
}