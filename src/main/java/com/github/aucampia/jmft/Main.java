package com.github.aucampia.jmft;
// vim: set ts=4 sw=4 noexpandtab:

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ExampleMode;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes;

import java.util.List;
import java.util.ArrayList;

import java.util.Map;

public class Main
{
	@Option( name = "--query", aliases = { "-q" }, usage = "Query string ..." )
	private String query;
	
	@Argument( required = true, metaVar="files...", usage = "Files to query ..." )
	private List<String> fileNames = new ArrayList<String>();

	private final String jarName = ( new java.io.File( Main.class.getProtectionDomain().getCodeSource().getLocation().getPath() ) ).getName();

	private static final Main main = new Main();

    public static void main( String[] args )
		throws Exception
    {
        Integer status = main.doMain( args );
        if ( status.intValue() != 0 )
            System.exit( status.intValue() );
    }

	private Integer doMain( String[] args )
		throws Exception
	{
		CmdLineParser parser = new CmdLineParser( this );
		parser.setUsageWidth( 72 );
		try
		{
			parser.parseArgument( args );
		}
        catch( CmdLineException e )
        {
            System.err.println( e.getMessage() );
            System.err.println( "java " + this.jarName + " [options...] files..." );
            parser.printUsage( System.err );
            System.err.println();
            System.err.println( "  Example: java " + this.jarName + parser.printExample( ExampleMode.ALL ) );
            return new Integer( 1 );
        }
		for ( String fileName: this.fileNames )
		{
			System.err.println( "Processnig " + fileName );
			try( JarFile jarFile = new JarFile( fileName ) )
			{
				Manifest manifest = jarFile.getManifest();
				Attributes mainAttributes = manifest.getMainAttributes();
				for ( Map.Entry<Object,Object> attribute: mainAttributes.entrySet() )
				{
					System.out.println( attribute.getKey() + " -> ( " + attribute.getValue().getClass() + " ) " + attribute.getValue() );
				}
				Map< String, Attributes > entries = manifest.getEntries();
				for ( Map.Entry< String, Attributes > entry: entries.entrySet() )
				{
					System.out.println( entry.getKey() + " = " + entry.getValue() );
				}
				/*
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure( SerializationConfig.Feature.INDENT_OUTPUT, false );
                String output = objectMapper.writeValueAsString( objectMapper );
				System.out.print( output );
				*/
			}
		}
		return new Integer( 0 );
	}

}
