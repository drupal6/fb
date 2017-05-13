package jsontool.json;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import jsontool.common.Util;


public class Alert {

	public static StringBuffer sbBuffer = new StringBuffer();

	public static void showWin() {

		if ("".equals(sbBuffer.toString())) {
			return;
		}
		final JFrame error = new JFrame("出错!");
		JButton c = new JButton("确定");
		JTextArea errTa = new JTextArea(40, 100);
		JScrollPane sp = new JScrollPane(errTa);
		errTa.append("生成错误！\n" + sbBuffer.toString());
		c.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				error.setVisible(false);
				// error.dispatchEvent(new WindowEvent(error,
				// WindowEvent.WINDOW_CLOSING));
			}
		});
		error.add(sp);
		error.add(new JPanel().add(c), BorderLayout.SOUTH);
		error.pack();
		error.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Util.setLocationCenter(error);
		error.setVisible(true);

	}

	public static void reset() {
		sbBuffer = new StringBuffer();
	}

	public static void showMsg(String msg) {
		sbBuffer.append(msg + "\n");

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		showMsg("111111111111111111111111");
		showMsg("111111111111111111111111");
		showMsg("111111111111111111111111");
		showMsg("111111111111111111111111");
		showWin();

	}

}
