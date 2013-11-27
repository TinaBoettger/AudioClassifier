package GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JPanel contentPane;
	boolean stopped;

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MainWindow().createAndShow();
			}
		});
	}

	private void createAndShow() {
		setTitle("Regressionsgerade");
		contentPane = new JPanel(new BorderLayout());
		contentPane.setOpaque(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel platzhalter = new AudioCapturePanel().createAndShow();

		// platzhalter.add(new Label("Platzhalter"));
		contentPane.add(platzhalter, BorderLayout.SOUTH);
        int w = 720;
        int h = 340;

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(screenSize.width / 2 - w / 2, screenSize.height / 2 - h / 2);

		this.setContentPane(contentPane);
		this.setSize(contentPane.getWidth(), contentPane.getHeight());
		this.pack();
		this.setVisible(true);

	}
}
