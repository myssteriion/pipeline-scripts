package com.es2i.pipeline.job.entities.enums;


/**
 * Enum représentant les clés obligatoires et facultatives (tableau ou non) pour un stage project (voir 'project_environment.properties').
 */
public enum ProjectKeyEnum {

	GIT_ROOT("gitRoot", true, false), PROJECT_ROOT("projectRoot", true, false), 
	JDK_COMPILATION("jdkCompilation", true, false), MVN_VERSION("mvnVersion", true, false), 
	
	TARGET_DIRECTORY("targetDirectory", false, false),
	
	SOURCE_APP_DIRECTORY("sourceAppDirectory", false, true), SOURCE_EXTENSION("sourceExtension", false, false),
	SOURCE_CONF_DIRECTORY("sourceConfDirectory", false, true);
	
	private String name;
	
	private boolean isMandatory;

	private boolean isList;
	
	
	private ProjectKeyEnum(String name, boolean isMandatory, boolean isList) {
		this.name = name;
		this.isMandatory = isMandatory;
		this.isList = isList;
	}
	
	
	
	public String getName() {
		return this.name;
	}
	
	public boolean isMandatory() {
		return this.isMandatory;
	}
	
	public boolean isList() {
		return this.isList;
	}
	
	
	/**
	 * Retourne un tableau (l'ordre du tableau impose l'ordre dans le script pipeline).
	 */
	public static ProjectKeyEnum[] getKeys() {
		ProjectKeyEnum[] tab = { GIT_ROOT, PROJECT_ROOT, JDK_COMPILATION, MVN_VERSION, TARGET_DIRECTORY, SOURCE_APP_DIRECTORY, SOURCE_EXTENSION, SOURCE_CONF_DIRECTORY };
		return tab;
	}

}
