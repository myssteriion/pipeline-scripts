package com.es2i.pipeline.job.entities;

public enum ProjectKeyEnum {

	GIT_ROOT("gitRoot", true), PROJECT_ROOT("projectRoot", true), JDK_COMPILATION("jdkCompilation", true), MVN_VERSION("mvnVersion", true),
	TARGET_DIRECTORY("targetDirectory", false), SOURCE_APP_DIRECTORY("sourceAppDirectory", false), SOURCE_EXTENSION("sourceExtension", false),
	SOURCE_CONF_DIRECTORY("sourceConfDirectory", false);
	
	private String name;
	
	private boolean isMandatory;

	
	
	private ProjectKeyEnum(String name, boolean isMandatory) {
		this.name = name;
		this.isMandatory = isMandatory;
	}
	
	
	
	public String getName() {
		return this.name;
	}
	
	public boolean isMandatory() {
		return this.isMandatory;
	}
	
	
	
	public static ProjectKeyEnum[] getKeys() {
		ProjectKeyEnum[] tab = { GIT_ROOT, PROJECT_ROOT, JDK_COMPILATION, MVN_VERSION, TARGET_DIRECTORY, SOURCE_APP_DIRECTORY, SOURCE_EXTENSION, SOURCE_CONF_DIRECTORY };
		return tab;
	}
}
