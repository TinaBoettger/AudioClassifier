package GUI;

import java.awt.BorderLayout;
import java.awt.Label;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JPanel contentPane;

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
		JPanel platzhalter = new JPanel();
		platzhalter.add(new Label("Platzhalter"));
		contentPane.add(platzhalter, BorderLayout.SOUTH);
		this.setContentPane(contentPane);
		this.setSize(contentPane.getWidth(), contentPane.getHeight());
		this.pack();
		this.setVisible(true);
	}
}
