package com.digero.common.util;

public class LotroParseException extends ParseException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4207869042694201068L;

	public LotroParseException(String message, String fileName, int line, int column)
	{
		super(message, fileName, line, column);
	}

	public LotroParseException(String message, String fileName, int line)
	{
		super(message, fileName, line);
	}

	public LotroParseException(String message, String fileName)
	{
		super(message, fileName);
	}
}
