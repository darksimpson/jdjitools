package com.darksimpson.jdjitools;

import asg.cliche.Shell;

import java.io.File;
import java.io.PrintStream;

interface TextOutputter {
	void outputWriteLine(String text);
	void outputFinish();

	class ShellOutputter implements TextOutputter {
		private Shell shell;

		ShellOutputter(Shell shell) {
			this.shell = shell;
		}

		public void outputFinish() {
		}

		public void outputWriteLine(String text) {
			shell.outputSimple(text);
		}
	}

	class FileOutputter implements TextOutputter {
		private PrintStream outputStream;

		FileOutputter(File file) throws JDTException {
			try {
				outputStream = new PrintStream(file);
			} catch (Exception e) {
				throw new JDTException("Output file must must be accessible: " + e.getMessage());
			}
		}

		public void outputFinish() {
			try {
				outputStream.close();
			} catch (Exception e) {
				// Do nothing
			}
		}

		public void outputWriteLine(String text) {
			outputStream.println(text);
		}
	}

	public static TextOutputter createOutputter(Shell shell, String fileName) throws JDTException {
		if (fileName != null) {
			File outFile = new File(fileName);
			shell.outputSimple("Outputting to '" + outFile.getAbsolutePath() + "' file.");
			return new TextOutputter.FileOutputter(outFile);
		} else {
			return new TextOutputter.ShellOutputter(shell);
		}
	}
}
