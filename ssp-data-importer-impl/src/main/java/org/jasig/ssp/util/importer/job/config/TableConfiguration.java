package org.jasig.ssp.util.importer.job.config;

import java.util.List;
import javax.validation.ConstraintViolation;
import javax.validation.ValidatorFactory;
import javax.validation.Validation;
import javax.validation.Validator;

public class TableConfiguration {
	
	private String tableName;
	private String fileName;
	private List<String> naturalKeys;
	private String fullyQualifiedClassName;
	private Validator validator;
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public List<String> getNaturalKeys() {
		return naturalKeys;
	}
	public void setNaturalKeys(List<String> keyColumnNames) {
		this.naturalKeys = keyColumnNames;
	}
	public String getFullyQualifiedClassName() {
		return fullyQualifiedClassName;
	}
	public void setFullyQualifiedClassName(String fullyQualifiedClassName) {
		this.fullyQualifiedClassName = fullyQualifiedClassName;
	}
	
	public Class getTableClass() throws ClassNotFoundException{
		return Class.forName(this.fullyQualifiedClassName);
	}
	
	public Validator getValidator(){
		return validator;
	}

}
