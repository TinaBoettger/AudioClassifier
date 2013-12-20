package GUI;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Vector;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;

public class AudioCapturePanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	final int bufSize = 16384;

	Capture capture = new Capture();
	Playback playback = new Playback();
	FeatureExtractor ex;
	AudioInputStream audioInputStream;
	AudioFormat format = null;
	SamplingGraph samplingGraph;

	JButton playB, captB, pausB, loadB, analB;
	JButton auB, aiffB, waveB;
	JTextField textField;

	String fileName = "untitled";
	String errStr;
	double duration, seconds;
	File file;
	Vector<Double> lines = new Vector<Double>();

	public AudioCapturePanel() {
		
		setLayout(new BorderLayout());
		EmptyBorder eb = new EmptyBorder(5, 5, 5, 5);
		SoftBevelBorder sbb = new SoftBevelBorder(SoftBevelBorder.LOWERED);
		setBorder(new EmptyBorder(5, 5, 5, 5));

		JPanel p2 = new JPanel();
		p2.setBorder(sbb);
		p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setBorder(new EmptyBorder(10, 0, 5, 0));
		playB = addButton("Play", buttonsPanel, false);
		captB = addButton("Record", buttonsPanel, true);
		pausB = addButton("Pause", buttonsPanel, false);
		loadB = addButton("Load...", buttonsPanel, true);
		analB = addButton("Analyse this!", buttonsPanel, true);
		p2.add(buttonsPanel);

		JPanel samplingPanel = new JPanel(new BorderLayout());
		eb = new EmptyBorder(10, 20, 20, 20);
		samplingPanel.setBorder(new CompoundBorder(eb, sbb));
		samplingPanel.add(samplingGraph = new SamplingGraph());
		p2.add(samplingPanel);

		JPanel savePanel = new JPanel();
		savePanel.setLayout(new BoxLayout(savePanel, BoxLayout.Y_AXIS));

		JPanel saveTFpanel = new JPanel();
		saveTFpanel.add(new JLabel("File to save:  "));
		saveTFpanel.add(textField = new JTextField(fileName));
		textField.setPreferredSize(new Dimension(140, 25));
		savePanel.add(saveTFpanel);

		JPanel saveBpanel = new JPanel();
		auB = addButton("Save AU", saveBpanel, false);
		aiffB = addButton("Save AIFF", saveBpanel, false);
		waveB = addButton("Save WAVE", saveBpanel, false);
		savePanel.add(saveBpanel);

		// p2.add(savePanel);

		add(p2);
		// add(p1);
		int w = 720;
		int h = 340;
		this.setSize(w, h);
		format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, (16 / 8) * 2, 44100, false);
	}

	public void open() {
	}

	public void close() {
		if (playback.thread != null) {
			playB.doClick(0);
		}
		if (capture.thread != null) {
			captB.doClick(0);
		}
	}

	private JButton addButton(String name, JPanel p, boolean state) {
		JButton b = new JButton(name);
		b.addActionListener(this);
		b.setEnabled(state);
		p.add(b);
		return b;
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj.equals(auB)) {
			saveToFile(textField.getText().trim(), AudioFileFormat.Type.AU);
		} else if (obj.equals(aiffB)) {
			saveToFile(textField.getText().trim(), AudioFileFormat.Type.AIFF);
		} else if (obj.equals(waveB)) {
			saveToFile(textField.getText().trim(), AudioFileFormat.Type.WAVE);
		} else if (obj.equals(playB)) {
			if (playB.getText().startsWith("Play")) {
				playback.start();
				samplingGraph.start();
				captB.setEnabled(false);
				pausB.setEnabled(true);
				playB.setText("Stop");
			} else {
				playback.stop();
				samplingGraph.stop();
				captB.setEnabled(true);
				pausB.setEnabled(false);
				playB.setText("Play");
			}
		} else if (obj.equals(captB)) {
			if (captB.getText().startsWith("Record")) {
				file = null;
				capture.start();
				fileName = "untitled";
				samplingGraph.start();
				loadB.setEnabled(false);
				playB.setEnabled(false);
				pausB.setEnabled(true);
				auB.setEnabled(false);
				aiffB.setEnabled(false);
				waveB.setEnabled(false);
				captB.setText("Stop");
			} else {
				lines.removeAllElements();
				capture.stop();
				samplingGraph.stop();
				loadB.setEnabled(true);
				playB.setEnabled(true);
				pausB.setEnabled(false);
				auB.setEnabled(true);
				aiffB.setEnabled(true);
				waveB.setEnabled(true);
				captB.setText("Record");
			}
		} else if (obj.equals(pausB)) {
			if (pausB.getText().startsWith("Pause")) {
				if (capture.thread != null) {
					capture.line.stop();
				} else {
					if (playback.thread != null) {
						playback.line.stop();
					}
				}
				pausB.setText("Resume");
			} else {
				if (capture.thread != null) {
					capture.line.start();
				} else {
					if (playback.thread != null) {
						playback.line.start();
					}
				}
				pausB.setText("Pause");
			}
		} else if (obj.equals(loadB)) {
			try {
				File file = new File(System.getProperty("user.dir"));
				JFileChooser fc = new JFileChooser(file);
				fc.setFileFilter(new javax.swing.filechooser.FileFilter() {
					public boolean accept(File f) {
						if (f.isDirectory()) {
							return true;
						}
						String name = f.getName();
						if (name.endsWith(".au") || name.endsWith(".wav") || name.endsWith(".aiff")
								|| name.endsWith(".aif")) {
							return true;
						}
						return false;
					}

					public String getDescription() {
						return ".au, .wav, .aif";
					}
				});

				if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					createAudioInputStream(fc.getSelectedFile(), true);
				}
			} catch (SecurityException ex) {
				ex.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void createAudioInputStream(File file, boolean updateComponents) {
		if (file != null && file.isFile()) {
			try {
				this.file = file;
				errStr = null;
				audioInputStream = AudioSystem.getAudioInputStream(file);
				playB.setEnabled(true);
				fileName = file.getName();
				long milliseconds = (long) ((audioInputStream.getFrameLength() * 1000) / audioInputStream
						.getFormat().getFrameRate());
				duration = milliseconds / 1000.0;
				auB.setEnabled(true);
				aiffB.setEnabled(true);
				waveB.setEnabled(true);
				if (updateComponents) {
					setFormat(audioInputStream.getFormat());
					samplingGraph.createWaveForm(null);
				}
			} catch (Exception ex) {
				reportStatus(ex.toString());
			}
		} else {
			reportStatus("Audio file required.");
		}
	}

	public void saveToFile(String name, AudioFileFormat.Type fileType) {

		if (audioInputStream == null) {
			reportStatus("No loaded audio to save");
			return;
		} else if (file != null) {
			createAudioInputStream(file, false);
		}

		// reset to the beginnning of the captured data
		try {
			audioInputStream.reset();
		} catch (Exception e) {
			reportStatus("Unable to reset stream " + e);
			return;
		}

		File file = new File(fileName = name);
		try {
			if (AudioSystem.write(audioInputStream, fileType, file) == -1) {
				throw new IOException("Problems writing to file");
			}
		} catch (Exception ex) {
			reportStatus(ex.toString());
		}
		samplingGraph.repaint();
	}

	private void reportStatus(String msg) {
		if ((errStr = msg) != null) {
			System.out.println(errStr);
			samplingGraph.repaint();
		}
	}

	/**
	 * Write data to the OutputChannel.
	 */
	public class Playback implements Runnable {

		SourceDataLine line;
		Thread thread;

		public void start() {
			errStr = null;
			thread = new Thread(this);
			thread.setName("Playback");
			thread.start();
		}

		public void stop() {
			thread = null;
		}

		private void shutDown(String message) {
			if ((errStr = message) != null) {
				System.err.println(errStr);
				samplingGraph.repaint();
			}
			if (thread != null) {
				thread = null;
				samplingGraph.stop();
				captB.setEnabled(true);
				pausB.setEnabled(false);
				playB.setText("Play");
			}
		}

		public void run() {

			// reload the file if loaded by file
			if (file != null) {
				createAudioInputStream(file, false);
			}

			// make sure we have something to play
			if (audioInputStream == null) {
				shutDown("No loaded audio to play back");
				return;
			}
			// reset to the beginnning of the stream
			try {
				audioInputStream.reset();
			} catch (Exception e) {
				shutDown("Unable to reset the stream\n" + e);
				return;
			}

			// get an AudioInputStream of the desired format for playback
			getFormat();
			AudioInputStream playbackInputStream = AudioSystem.getAudioInputStream(format, audioInputStream);

			if (playbackInputStream == null) {
				shutDown("Unable to convert stream of format " + audioInputStream + " to format " + format);
				return;
			}

			// define the required attributes for our line,
			// and make sure a compatible line is supported.

			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
			if (!AudioSystem.isLineSupported(info)) {
				shutDown("Line matching " + info + " not supported.");
				return;
			}

			// get and open the source data line for playback.

			try {
				line = (SourceDataLine) AudioSystem.getLine(info);
				line.open(format, bufSize);
			} catch (LineUnavailableException ex) {
				shutDown("Unable to open the line: " + ex);
				return;
			}

			// play back the captured audio data

			int frameSizeInBytes = format.getFrameSize();
			int bufferLengthInFrames = line.getBufferSize() / 8;
			int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
			byte[] data = new byte[bufferLengthInBytes];
			int numBytesRead = 0;

			// start the source data line
			line.start();

			while (thread != null) {
				try {
					if ((numBytesRead = playbackInputStream.read(data)) == -1) {
						break;
					}
					int numBytesRemaining = numBytesRead;
					while (numBytesRemaining > 0) {
						numBytesRemaining -= line.write(data, 0, numBytesRemaining);
					}
				} catch (Exception e) {
					shutDown("Error during playback: " + e);
					break;
				}
			}
			// we reached the end of the stream. let the data play out, then
			// stop and close the line.
			if (thread != null) {
				line.drain();
			}
			line.stop();
			line.close();
			line = null;
			shutDown(null);
		}
	} // End class Playback

	/**
	 * Reads data from the input channel and writes to the output stream
	 */
	class Capture implements Runnable {

		TargetDataLine line;
		Thread thread;

		public void start() {
			errStr = null;
			thread = new Thread(this);
			thread.setName("Capture");
			thread.start();
		}

		public void stop() {
			thread = null;
		}

		private void shutDown(String message) {
			if ((errStr = message) != null && thread != null) {
				thread = null;
				samplingGraph.stop();
				loadB.setEnabled(true);
				playB.setEnabled(true);
				pausB.setEnabled(false);
				auB.setEnabled(true);
				aiffB.setEnabled(true);
				waveB.setEnabled(true);
				captB.setText("Record");
				System.err.println(errStr);
				samplingGraph.repaint();
			}
		}

		public void run() {

			duration = 0;
			audioInputStream = null;

			// define the required attributes for our line,
			// and make sure a compatible line is supported.

			AudioFormat format = getFormat();
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

			if (!AudioSystem.isLineSupported(info)) {
				shutDown("Line matching " + info + " not supported.");
				return;
			}

			// get and open the target data line for capture.

			try {
				line = (TargetDataLine) AudioSystem.getLine(info);
				line.open(format, line.getBufferSize());
			} catch (LineUnavailableException ex) {
				shutDown("Unable to open the line: " + ex);
				return;
			} catch (SecurityException ex) {
				shutDown(ex.toString());
				return;
			} catch (Exception ex) {
				shutDown(ex.toString());
				return;
			}

			// play back the captured audio data
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int frameSizeInBytes = format.getFrameSize();
			int bufferLengthInFrames = line.getBufferSize() / 8;
			int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
			byte[] data = new byte[bufferLengthInBytes];
			int numBytesRead;

			line.start();

			while (thread != null) {
				if ((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1) {
					break;
				}
				out.write(data, 0, numBytesRead);
			}

			// we reached the end of the stream. stop and close the line.
			line.stop();
			line.close();
			line = null;

			// stop and close the output stream
			try {
				out.flush();
				out.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			// load bytes into the audio input stream for playback

			byte audioBytes[] = out.toByteArray();
			ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
			audioInputStream = new AudioInputStream(bais, format, audioBytes.length / frameSizeInBytes);

			long milliseconds = (long) ((audioInputStream.getFrameLength() * 1000) / format.getFrameRate());
			duration = milliseconds / 1000.0;

			try {
				audioInputStream.reset();
			} catch (Exception ex) {
				ex.printStackTrace();
				return;
			}

			samplingGraph.createWaveForm(audioBytes);
		}
	} // End class Capture

	public AudioFormat getFormat() {
		return format;
	}

	public void setFormat(AudioFormat pformat) {
		format = pformat;
	}

	/**
	 * Render a WaveForm.
	 */
	class SamplingGraph extends JPanel implements Runnable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Thread thread;
		// private Font font10 = new Font("serif", Font.PLAIN, 10);
		private Font font12 = new Font("serif", Font.PLAIN, 12);
		Color jfcBlue = new Color(204, 204, 255);
		Color pink = new Color(255, 175, 175);

		public SamplingGraph() {
			setBackground(new Color(20, 20, 20));
			setPreferredSize(new Dimension(1000, 500));
		}

		public void createWaveForm(byte[] audioBytes) {

			lines.removeAllElements(); // clear the old vector

			// AudioFormat format = audioInputStream.getFormat();
			System.out.println(format);
			if (audioBytes == null) {
				try {
					audioBytes = new byte[(int) (audioInputStream.getFrameLength() * format.getFrameSize())];
					audioInputStream.read(audioBytes);
				} catch (Exception ex) {
					reportStatus(ex.toString());
					return;
				}
			}

			Dimension d = getSize();
			int w = d.width;
			int h = d.height - 15;
			int[] audioData = null;
			int[] copyAudioData = null;
			int nlengthInSamples = audioBytes.length / 2;
			audioData = new int[nlengthInSamples];
		
			for (int i = 0; i < nlengthInSamples; i++) {
				/* First byte is LSB (low order) */
				int LSB = (int) audioBytes[2 * i];
				/* Second byte is MSB (high order) */
				int MSB = (int) audioBytes[2 * i + 1];
				audioData[i] = MSB << 8 | (255 & LSB);
			}
			copyAudioData = audioData;
		
			int frames_per_pixel = audioBytes.length / format.getFrameSize() / w;
			byte my_byte = 0;
			double y_last = 0;
			int numChannels = format.getChannels();
			for (double x = 0; x < w && audioData != null; x++) {
				int idx = (int) (frames_per_pixel * numChannels * x);
				if (format.getSampleSizeInBits() == 8) {
					my_byte = (byte) audioData[idx];
					copyAudioData[idx] = my_byte;
				} else {
					my_byte = (byte) (128 * audioData[idx] / 32768);
					copyAudioData[idx] = my_byte;
				}
				double y_new = (double) (h * (128 - my_byte) / 256);
				lines.add(new Line2D.Double(x, y_last, x, y_new));
				y_last = y_new;
			}
			ex = new FeatureExtractor(copyAudioData);
			System.out.println(copyAudioData.length);
			
			repaint();
		}

		public void paint(Graphics g) {

			Dimension d = getSize();
			int w = d.width;
			int h = d.height;
			int INFOPAD = 15;

			Graphics2D g2 = (Graphics2D) g;
			g2.setBackground(getBackground());
			g2.clearRect(0, 0, w, h);
			g2.setColor(Color.white);
			g2.fillRect(0, h - INFOPAD, w, INFOPAD);

			if (errStr != null) {
				g2.setColor(jfcBlue);
				g2.setFont(new Font("serif", Font.BOLD, 18));
				g2.drawString("ERROR", 5, 20);
				AttributedString as = new AttributedString(errStr);
				as.addAttribute(TextAttribute.FONT, font12, 0, errStr.length());
				AttributedCharacterIterator aci = as.getIterator();
				FontRenderContext frc = g2.getFontRenderContext();
				LineBreakMeasurer lbm = new LineBreakMeasurer(aci, frc);
				float x = 5, y = 25;
				lbm.setPosition(0);
				while (lbm.getPosition() < errStr.length()) {
					TextLayout tl = lbm.nextLayout(w - x - 5);
					if (!tl.isLeftToRight()) {
						x = w - tl.getAdvance();
					}
					tl.draw(g2, x, y += tl.getAscent());
					y += tl.getDescent() + tl.getLeading();
				}
			} else if (capture.thread != null) {
				g2.setColor(Color.black);
				g2.setFont(font12);
				g2.drawString("Length: " + String.valueOf(seconds), 3, h - 4);
			} else {
				g2.setColor(Color.black);
				g2.setFont(font12);
				g2.drawString("File: " + fileName + "  Length: " + String.valueOf(duration) + "  Position: "
						+ String.valueOf(seconds), 3, h - 4);

				if (audioInputStream != null) {
					// .. render sampling graph ..
					g2.setColor(jfcBlue);
					for (int i = 1; i < lines.size(); i++) {
						g2.draw((Line2D) lines.get(i));
					}

					// .. draw current position ..
					if (seconds != 0) {
						double loc = seconds / duration * w;
						g2.setColor(pink);
						g2.setStroke(new BasicStroke(3));
						g2.draw(new Line2D.Double(loc, 0, loc, h - INFOPAD - 2));
					}
				}
			}
		}

		public void start() {
			thread = new Thread(this);
			thread.setName("SamplingGraph");
			thread.start();
			seconds = 0;
		}

		public void stop() {
			if (thread != null) {
				thread.interrupt();
			}
			thread = null;
//			JOptionPane.showMessageDialog(this,
//				    "The audio signal captured most probably describes a bang.");
		}

		public void run() {
			seconds = 0;
			while (thread != null) {
				if ((playback.line != null) && (playback.line.isOpen())) {

					long milliseconds = (long) (playback.line.getMicrosecondPosition() / 1000);
					seconds = milliseconds / 1000.0;
				} else if ((capture.line != null) && (capture.line.isActive())) {

					long milliseconds = (long) (capture.line.getMicrosecondPosition() / 1000);
					seconds = milliseconds / 1000.0;
				}

				try {
					Thread.sleep(100);
				} catch (Exception e) {
					break;
				}

				repaint();

				while ((capture.line != null && !capture.line.isActive())
						|| (playback.line != null && !playback.line.isOpen())) {
					try {
						Thread.sleep(10);
					} catch (Exception e) {
						break;
					}
				}
			}
			seconds = 0;
			repaint();
		}
	} // End class SamplingGraph

	public AudioCapturePanel createAndShow() {
		AudioCapturePanel capturePlayback = new AudioCapturePanel();
		capturePlayback.open();

		return this;
	}
}
