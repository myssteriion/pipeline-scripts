package com.es2i.pipeline.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

public class Tools {

	public static void deleteIfExists(Path path) throws IOException {
		FileUtils.deleteDirectory( path.toFile() );
	}
	
	public static Path createDirectoryIfNeedIt(Path path) throws IOException {
		
		if ( path.toFile().exists() )
			return path;
		else
			return Files.createDirectory(path);
	}
	
	public static String concatenateItem(Collection<String> items) {
		
		if ( items == null || items.isEmpty() )
			return "";
		
		String str = "";
		for (String item : items) {
			str += item + ", ";
		}
		return str.substring(0, str.length()-2);
	}
	
	public static boolean isNullOrEmpty(String str) {
		return str == null || str.isEmpty();
	}

	public static List<String> getKeysFilterByPrefix(Properties prop, String prefix) {
		
		return prop.stringPropertyNames().stream()
								.filter( key -> key.startsWith(prefix) )
								.sorted()
								.collect( Collectors.toList() );
	}

	public static void verifyKeys(Set<String> expectedKeys, Set<String> actualKeys, String fileName) {
		
		if ( !actualKeys.containsAll(expectedKeys) ) {
			String message = "Au moins une clé est manquante dans " + fileName;
			message += " (" + Tools.concatenateItem(expectedKeys) + ")";
			throw new IllegalArgumentException(message);
		}
	}
	
}
