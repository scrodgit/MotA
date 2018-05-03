/* Copyright (c) 2010 Ben Howell
 * This software is licensed under the MIT License
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE.
 */

package com.digero.common.abc;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.digero.common.midi.Note;
import com.digero.common.util.Pair;

// @formatter:off
public enum LotroInstrument
{
	                       //friendlyName                          sustain  midi  octave  percussion  dBAdjust
	PIANO                    ( "Piano",                             false,    1,     -1,      false,   -19.0f),
	DRUMS                    ( "Drums",                             false,  118,      0,       true,     0.0f),
	LUTE_OF_AGES             ( "Lute",                      false,   24,      0,      false,     0.0f),
	//BASIC_LUTE               ( "Basic Lute",                        false,   25,      0,      false,   -19.0f),
	HARP                     ( "Harp",                              false,   46,      0,      false,     6.0f),
	//MISTY_MOUNTAIN_HARP      ( "Misty Mountain Harp",               false,   27,      0,      false,   -12.5f),
	//BARDIC_FIDDLE            ( "Bardic Fiddle",                      true,   40,      1,      false,     0.0f),
	//BASIC_FIDDLE             ( "Basic Fiddle",                       true,   41,      1,      false,     0.0f),
	//LONELY_MOUNTAIN_FIDDLE   ( "Lonely Mountain Fiddle",             true,   51,      1,      false,     0.0f),
	//SPRIGHTLY_FIDDLE         ( "Sprightly Fiddle",                  false,  110,      1,      false,     0.0f),
	//STUDENT_FIDDLE           ( "Student's Fiddle",                   true,  120,      1,      false,     0.0f),
	//TRAVELLERS_TRUSTY_FIDDLE ( "Traveller's Trusty Fiddle",         false,   45,      1,      false,     0.0f),
	//THEORBO                  ( "Theorbo",                           false,   32,     -1,      false,   -12.0f), // -12.2f
	FLUTE                    ( "Flute",                              true,   73,      2,      false,    -0.5f), //  -4.2f
	//CLARINET                 ( "Clarinet",                           true,   71,      1,      false,    -2.5f), //  -2.9f
	//HORN                     ( "Horn",                               true,   69,      0,      false,     0.0f), //  -1.7f
	BAGPIPE                  ( "Bagpipe",                            true,  109,      1,      false,    -3.2f),
	//PIBGORN                  ( "Pibgorn",                            true,   84,      2,      false,    -3.5f),
	COWBELL                  ( "Cowbell",                           false,  115,      0,       true,     0.0f);
	//MOOR_COWBELL             ( "Moor Cowbell",                      false,  114,      0,       true,     0.0f);
// @formatter:on

	//public static final LotroInstrument DEFAULT_FIDDLE = BARDIC_FIDDLE;
	//public static final LotroInstrument DEFAULT_INSTRUMENT = LUTE_OF_AGES;
    public static final LotroInstrument DEFAULT_LUTE = LUTE_OF_AGES;
	//public static final LotroInstrument DEFAULT_FIDDLE = BARDIC_FIDDLE;
	public static final LotroInstrument DEFAULT_INSTRUMENT = PIANO;
	
	public final Note lowestPlayable;
	public final Note highestPlayable;
	public final String friendlyName;
	public final boolean sustainable;
	public final boolean isPercussion;
	public final int midiProgramId;
	public final int octaveDelta;
	public final float dBVolumeAdjust;

	private LotroInstrument(String friendlyName, boolean sustainable, int midiProgramId, int octaveDelta,
			boolean isPercussion, float dBVolumeAdjust)
	{
		this(friendlyName, sustainable, midiProgramId, octaveDelta, isPercussion, dBVolumeAdjust, Note.MIN_PLAYABLE,
				Note.MAX_PLAYABLE);
	}

	private LotroInstrument(String friendlyName, boolean sustainable, int midiProgramId, int octaveDelta,
			boolean isPercussion, float dBVolumeAdjust, Note lowestPlayable, Note highestPlayable)
	{
		this.lowestPlayable = lowestPlayable;
		this.highestPlayable = highestPlayable;
		this.friendlyName = friendlyName;
		this.sustainable = sustainable;
		this.midiProgramId = midiProgramId;
		this.octaveDelta = octaveDelta;
		this.isPercussion = isPercussion;
		this.dBVolumeAdjust = dBVolumeAdjust;
	}

	public boolean isSustainable(int noteId)
	{
		return sustainable && isPlayable(noteId);
	}

	public boolean isPlayable(int noteId)
	{
		return noteId >= lowestPlayable.id && noteId <= highestPlayable.id;
	}

	@Override public String toString()
	{
		return friendlyName;
	}

	public static LotroInstrument parseInstrument(String string) throws IllegalArgumentException
	{
		string = string.toUpperCase().replaceAll("[\\s_']", "");

		if (string.equals("LUTE"))
			return DEFAULT_LUTE;

		if (string.equals("FIDDLE"))
			return DEFAULT_INSTRUMENT;

		if (string.equals("MISTYHARP"))
			//return MISTY_MOUNTAIN_HARP;
			return DEFAULT_INSTRUMENT;
			
		if (string.equals("LONELYFIDDLE"))
			//return LONELY_MOUNTAIN_FIDDLE;
			return DEFAULT_INSTRUMENT;
		if (string.equals("TRAVELLERSFIDDLE"))
			//return TRAVELLERS_TRUSTY_FIDDLE;
			return DEFAULT_INSTRUMENT;
		for (LotroInstrument i : values())
		{
			if (i.name().replace("_", "").equals(string))
				return i;
		}
		return LotroInstrument.valueOf(string);
	}

	private static Pattern instrumentsRegex = null;
	private static List<Pair<Pattern, LotroInstrument>> instrumentNicknames = new ArrayList<Pair<Pattern, LotroInstrument>>();

	@SafeVarargs private static <T> Pattern makeInstrumentRegex(T... names)
	{
		String regex = "";
		for (T name : names)
		{
			if (regex.length() > 0)
				regex += "|";
			regex += name.toString().replace(" ", "\\s*");
		}
		return Pattern.compile("\\b(" + regex + ")\\b", Pattern.CASE_INSENSITIVE);
	}

	private static void addNicknames(LotroInstrument instrument, String... nicknames)
	{
		instrumentNicknames.add(new Pair<Pattern, LotroInstrument>(makeInstrumentRegex(nicknames), instrument));
	}

	public static LotroInstrument findInstrumentName(String str, LotroInstrument defaultInstrument)
	{
		if (instrumentNicknames.size() == 0)
		{
			//addNicknames(LotroInstrument.BASIC_LUTE, "Basic Lute", "New Lute", "LuteB", "Banjo");
			//addNicknames(LotroInstrument.LUTE_OF_AGES, "Lute of Ages", "Age Lute", "LuteA", "LOA", "Guitar");
			//addNicknames(LotroInstrument.DEFAULT_LUTE, "Lute");
			//addNicknames(LotroInstrument.MISTY_MOUNTAIN_HARP, "Misty Mountain Harp", "Misty Harp", "MM Harp", "MMH");
			//addNicknames(LotroInstrument.BASIC_FIDDLE, "Basic Fiddle");
			//addNicknames(LotroInstrument.BARDIC_FIDDLE, "Bardic Fiddle", "B Fiddle", "Violin");
			//addNicknames(LotroInstrument.LONELY_MOUNTAIN_FIDDLE, "Lonely (Mountain)? Fiddle", "LM Fiddle");
			//addNicknames(LotroInstrument.SPRIGHTLY_FIDDLE, "Sprightly Fiddle");
			//addNicknames(LotroInstrument.STUDENT_FIDDLE, "Student'?s? Fiddle");
			//addNicknames(LotroInstrument.TRAVELLERS_TRUSTY_FIDDLE, "Travell?er'?s? (Trusty)? Fiddle", "Trusty Fiddle", "TT Fiddle");
			//addNicknames(LotroInstrument.DEFAULT_FIDDLE, "Fiddle");
			//addNicknames(LotroInstrument.HARP, "Basic Harp");
			//addNicknames(LotroInstrument.THEORBO, "Theo", "Bass");
			addNicknames(LotroInstrument.DRUMS, "Drum");
			//addNicknames(LotroInstrument.BAGPIPE, "Bagpipes");
			//addNicknames(LotroInstrument.MOOR_COWBELL, "Moor Cowbell", "More Cowbell");
			addNicknames(LotroInstrument.PIANO, "Basic Lute", "New Lute", "LuteB", "Banjo", "Lute of Ages", "Age Lute", "LuteA", "LOA", "Guitar", "Lute", "Misty Mountain Harp", "Misty Harp", "MM Harp", "MMH", "Basic Fiddle", "Bardic Fiddle", "B Fiddle", "Violin", "Lonely (Mountain)? Fiddle", "LM Fiddle", "Sprightly Fiddle", "Student'?s? Fiddle", "Travell?er'?s? (Trusty)? Fiddle", "Trusty Fiddle", "TT Fiddle", "Fiddle", "Basic Harp", "Theo", "Bass", "Bagpipes", "Moor Cowbell", "More Cowbell", "Piano", "Bright Piano");
		}

		for (Pair<Pattern, LotroInstrument> patternAndInstrument : instrumentNicknames)
		{
			if (patternAndInstrument.first.matcher(str).find())
				return patternAndInstrument.second;
		}

		if (instrumentsRegex == null)
			instrumentsRegex = makeInstrumentRegex(LotroInstrument.values());

		Matcher m = instrumentsRegex.matcher(str);
		if (m.find())
			return LotroInstrument.parseInstrument(m.group(1));

		return defaultInstrument;
	}
}
