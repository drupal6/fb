package jsontool.json;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jsontool.common.Util;
import jsontool.json.node.TestJson;


public class JsonMain {

	private static final Logger logger = LoggerFactory.getLogger(JsonMain.class);
	private JFrame mainFrame = null;

	private ButtonActionListener[] buttons = null;

//	private JCheckBox copyPngAndTexture = null;
//	private JCheckBox RGB4444Compress = null;
//	private JCheckBox RGB5551Compress = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		JsonMain m = new JsonMain();
		if (args == null || args.length == 0) {
			m.init();
		} else {
			m.processArguments(args);
		}
	}

	protected void initConvert() {

	}

	private void init() {
		initConvert();
		mainFrame = new JFrame("物品生成工具");
		buttons = new ButtonActionListener[] { //
				new ButtonActionListener("", null), //
				new ButtonActionListener("Test", new TestJson()), //
		};
		//
		Box b = Box.createVerticalBox();

		JButton[] bs = new JButton[buttons.length];
		JPanel curr = null;
		int idx = 0;
		for (int i = 0; i < buttons.length; i++) {
			bs[i] = new JButton(buttons[i].getName());
			bs[i].setSize(40, 20);
			bs[i].setFont(Font.getFont("Verdana"));
			bs[i].addActionListener(buttons[i]);
			if (buttons[i].action == null && buttons[i].name.isEmpty()) {
				curr = new JPanel();
				b.add(curr);
				idx = 0;
			} else {
				if (idx > 0 && idx % 4 == 0) {
					curr = new JPanel();
					b.add(curr);
				}
				curr.add(bs[i]);
				idx++;
			}
		}
		//
		curr = new JPanel();
		b.add(curr);
		JScrollPane scrollPane = new JScrollPane(b);
		scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.revalidate();
		mainFrame.add(scrollPane);
		// mainFrame.pack();
		mainFrame.setSize(new Dimension(1024, 768));
		Util.setLocationCenter(mainFrame);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setVisible(true);
	}

	private class ButtonActionListener implements ActionListener {

		private final String name;
		private final ETJson action;

		public ButtonActionListener(String name, ETJson action) {
			this.name = name;
			this.action = action;
		}

		public String getName() {
			return name;
		}

		public ETJson getAction() {
			return action;
		}

		public boolean doAction(boolean showPanel) {
			if (mainFrame == null) {
				return false;
			}
			if (this.action != null) {
				try {
					this.action.init();
					waitPacker();
					if (showPanel) {
						JOptionPane.showMessageDialog(mainFrame, "【"
								+ getName() + "】 生成完毕！");
					}
					return true;
				} catch (Exception e1) {
					e1.printStackTrace();
					logger.error("", e1);
					if (showPanel) {
						final JFrame error = new JFrame("出错!");
						JButton c = new JButton("确定");
						JTextArea errTa = new JTextArea(10, 20);
						JScrollPane sp = new JScrollPane(errTa);
						errTa.append("生成错误！\n" + e1.toString());
						StringWriter sw = new StringWriter();
						e1.printStackTrace(new PrintWriter(sw));
						String exceptionAsString = sw.toString();
						errTa.append("\n" + exceptionAsString);
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
				}
			}
			return false;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Alert.reset();
			if (this.action != null) {
				doAction(true);
				Alert.showWin();
			} else {
				if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(
						null, "确定要生成全部json吗？")) {
					return;
				}
				for (int i = 0; i < buttons.length; ++i) {
					if (buttons[i].getAction() == null) {
						continue;
					}
					buttons[i].doAction(false);
				}
				//
				JOptionPane.showMessageDialog(mainFrame, "【" + getName() + "】 生成完毕！");
				Alert.showWin();
			}
		}
	}

	private void waitPacker() {
	}

	// == commands

	private void processArguments(String[] args) throws IOException {
		if(args != null && args[0].equalsIgnoreCase("all")) {
			init();
			for(ButtonActionListener listener : buttons) {
				listener.doAction(false);
			}
		}
		printHelpAndExit();
	}

	private void printHelpAndExit() {
		System.out.println("Usage: GuiMain [Options]");
		System.out.println("Options:");
		System.out.println("  --zip_gen");
		System.exit(0);
	}

}
