package com.digero.maestro.view;

import javax.sound.midi.Receiver;
import javax.swing.JDialog;
import javax.swing.JFrame;

public class DrumSelectionDialog extends JDialog
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2866988170060242713L;

	public DrumSelectionDialog(JFrame owner, Receiver midiReceiver, Receiver lotroReceiver)
	{
		super(owner);
	}
}
