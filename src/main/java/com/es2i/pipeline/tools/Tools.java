package com.es2i.pipeline.tools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Properties;

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

	public static Properties findPropertyFile(String fileName) throws IOException {
		
		try (InputStream is = Tools.class.getClassLoader().getResourceAsStream(fileName)) {
			
			if (is == null)
				throw new FileNotFoundException("Le fichier '" + fileName + "' n'est pas retrouvé.");
	
			Properties prop = new Properties();
			try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8) ) {
				prop.load(reader);
			}
			is.close();
				
			return prop;
		}
	}
	
}
