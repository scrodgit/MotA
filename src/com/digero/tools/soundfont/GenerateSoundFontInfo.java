package com.digero.tools.soundfont;

import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.digero.common.abc.AbcConstants;
import com.digero.common.abc.LotroInstrument;
import com.digero.common.util.ExtensionFileFilter;

public class GenerateSoundFontInfo
{
	public static void main(String[] args)
	{
		try
		{
			System.exit(run(args));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private static int getNotesPerSample(LotroInstrument lotroInstrument)
	{
		switch (lotroInstrument)
		{
			case DRUMS:
				return 1;

			case LUTE_OF_AGES:
			case BASIC_LUTE:
			case HARP:
			case BARDIC_FIDDLE:
			case PIANO:	
				return 1;

			case MISTY_MOUNTAIN_HARP:
			case THEORBO:
			case BASIC_FIDDLE:
			case LONELY_MOUNTAIN_FIDDLE:
			case SPRIGHTLY_FIDDLE:
			case TRAVELLERS_TRUSTY_FIDDLE:
			case FLUTE:
			case CLARINET:
			case HORN:
				return 2;

			case BAGPIPE:
				return 4;

			case PIBGORN:
				return 6;

			case STUDENT_FIDDLE:
			case COWBELL:
			case MOOR_COWBELL:
			default:
				throw new RuntimeException();
		}
	}

	private static int run(String[] args) throws Exception
	{
		File sampleDir = new File(args[0]);
		File outputFile = new File(args[1]);

		System.out.println("Sample Directory: " + sampleDir.getCanonicalPath());

		Map<SampleInfo.Key, SampleInfo> samples = new HashMap<SampleInfo.Key, SampleInfo>();

		SampleInfo cowbellSample = null;
		SampleInfo moorCowbellSample = null;
		for (File file : sampleDir.listFiles(new ExtensionFileFilter("", false, "wav")))
		{
			if (!SampleInfo.isSampleFile(file))
				continue;

			SampleInfo sample = new SampleInfo(file);
			samples.put(sample.key, sample);

			if (cowbellSample == null || sample.key.lotroInstrument == LotroInstrument.COWBELL)
				cowbellSample = sample;

			if (moorCowbellSample == null || sample.key.lotroInstrument == LotroInstrument.MOOR_COWBELL)
				moorCowbellSample = sample;
		}

		SortedSet<SampleInfo> usedSamples = new TreeSet<SampleInfo>();
		SortedSet<InstrumentInfo> instruments = new TreeSet<InstrumentInfo>();
		SortedSet<PresetInfo> presets = new TreeSet<PresetInfo>();
		InstrumentInfo basicFiddleInfo = null;

		for (LotroInstrument li : LotroInstrument.values())
		{
			if (li == LotroInstrument.COWBELL || li == LotroInstrument.MOOR_COWBELL)
			{
				SampleInfo sample = (li == LotroInstrument.COWBELL) ? cowbellSample : moorCowbellSample;
				CowbellInfo info = new CowbellInfo(sample);
				instruments.add(info);
				usedSamples.add(sample);

				presets.add(new PresetInfo(info));
			}
			else if (li == LotroInstrument.BAGPIPE)
			{
				StandardInstrumentInfo drones = new StandardInstrumentInfo(li, li + " Drones", li.lowestPlayable.id,
						AbcConstants.BAGPIPE_LAST_DRONE_NOTE_ID, getNotesPerSample(li), samples);
				instruments.add(drones);
				usedSamples.addAll(drones.usedSamples);

				StandardInstrumentInfo bagpipe = new StandardInstrumentInfo(li, li.toString(),
						AbcConstants.BAGPIPE_LAST_DRONE_NOTE_ID + 1, li.highestPlayable.id, getNotesPerSample(li),
						samples);
				instruments.add(bagpipe);
				usedSamples.addAll(bagpipe.usedSamples);

				presets.add(new PresetInfo(drones, bagpipe));
			}
			else if (li == LotroInstrument.STUDENT_FIDDLE)
			{
				StandardInstrumentInfo flubs = new StandardInstrumentInfo(li, li + " Flubs", li.lowestPlayable.id,
						AbcConstants.STUDENT_FIDDLE_LAST_FLUB_NOTE_ID, 1, samples);
				instruments.add(flubs);
				usedSamples.addAll(flubs.usedSamples);

				// Share the part of the Basic Fiddle instrument that's playable on the Student's Fiddle
				InstrumentInfoSubrange basicFiddleSubrange = new InstrumentInfoSubrange(li, basicFiddleInfo,
						AbcConstants.STUDENT_FIDDLE_LAST_FLUB_NOTE_ID + 1, li.highestPlayable.id);

				presets.add(new PresetInfo(flubs, basicFiddleSubrange));
			}
			else
			{
				StandardInstrumentInfo info = new StandardInstrumentInfo(li, getNotesPerSample(li), samples);
				instruments.add(info);
				usedSamples.addAll(info.usedSamples);

				presets.add(new PresetInfo(info));

				if (li == LotroInstrument.BASIC_FIDDLE)
					basicFiddleInfo = info;
			}
		}

		// OUTPUT

		System.out.println("Writing: " + outputFile.getCanonicalPath());

		try (PrintStream out = new PrintStream(outputFile))
		{
			out.println("[Samples]");
			out.println();
			for (SampleInfo sample : usedSamples)
			{
				sample.print(out);
			}

			out.println();
			out.println("[Instruments]");
			for (InstrumentInfo instrument : instruments)
			{
				instrument.print(out);
			}

			out.println();
			out.println("[Presets]");
			for (PresetInfo preset : presets)
			{
				preset.print(out);
				// Also add Lute as the default instrument (program 0)
				if (preset.lotroInstrument == LotroInstrument.LUTE_OF_AGES)
					preset.print(out, preset.lotroInstrument + "1", 0);
			}

			out.println();
			out.println("[Info]");
			out.println("Version=2.1");
			out.println("Engine=E-mu 10K1");
			out.println("Name=LotroInstruments.sf2");
			out.println("ROMName=");
			out.println("ROMVersion=0.0");
			out.println("Date=4/12/2015 12:34:56 PM");
			out.println("Designer=Digero");
			out.println("Product=Maestro");
			out.println("Copyright=Turbine Entertainment");
			out.println("Editor=Digero");
			out.println("Comments=");
		}

		return 0;
	}
}
