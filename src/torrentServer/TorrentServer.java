package torrentServer;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.commons.io.FileUtils;
import net.miginfocom.swing.MigLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class TorrentServer {

	public static JFrame frmEpisodeManager;
	public static JLabel onlineLabel = new JLabel("Offline");
	private final JLabel label1 = new JLabel("Connections so far:");
	private static JLabel connectionsLabel = new JLabel("0");
	public static JTextArea logTextArea = new JTextArea();
	public static JScrollPane scroller = new JScrollPane(logTextArea);
	public static double version = 1.2;
	private final JMenuBar menuBar = new JMenuBar();
	private final JMenu mnOptions = new JMenu("Options");
	private final JMenu mnHelp = new JMenu("Help");
	private final JMenuItem aboutMenuItem = new JMenuItem("About");
	private final JMenuItem checkForUpdatesMenuItem = new JMenuItem("Check For Updates");
	private final JMenuItem portMenuItem = new JMenuItem("Change Listening Port");
	private final JMenuItem resetMenuItem = new JMenuItem("Reset Network Settings");
	public static Preferences prefs;
	public static int port = 25252;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			@SuppressWarnings("static-access")
			public void run() {
				try {
					TorrentServer window = new TorrentServer();
					TorrentServer.frmEpisodeManager.setVisible(true);
					AutoUpdate.checkForUpdates("LAUNCH");
					checkFirstRun();
					checkDependancies();
				} catch (Exception e) {
					System.out.println("The program failed at startup.  Something must have fucked up in the last update.");
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TorrentServer() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmEpisodeManager = new JFrame();
		frmEpisodeManager.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmEpisodeManager.setIconImage(Toolkit.getDefaultToolkit().getImage(TorrentServer.class.getResource("/res/icon.png")));
		frmEpisodeManager.setTitle("Episode Manager V" + version);

		frmEpisodeManager.setBounds(100, 100, 400, 350);
		frmEpisodeManager.getContentPane().setLayout(new MigLayout("", "[grow]", "[][grow]"));

		//lblOffline created here originally.
		onlineLabel.setForeground(Color.RED);
		onlineLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		frmEpisodeManager.getContentPane().add(onlineLabel, "flowx,cell 0 0,growx");
		
		frmEpisodeManager.getContentPane().add(label1, "cell 0 0");
		
		frmEpisodeManager.getContentPane().add(connectionsLabel, "cell 0 0");
		logTextArea.setToolTipText("Activity log");
		logTextArea.setEditable(false);
		
		frmEpisodeManager.getContentPane().add(scroller, "cell 0 1,grow");
		scroller.setEnabled(true);
		
		frmEpisodeManager.setJMenuBar(menuBar);
		
		menuBar.add(mnOptions);
		mnOptions.setToolTipText("Configure network settings.");
		
		mnOptions.add(portMenuItem);
		
		mnOptions.add(resetMenuItem);
		
		menuBar.add(mnHelp);
		
		mnHelp.add(aboutMenuItem);
		
		mnHelp.add(checkForUpdatesMenuItem);
		
		
		prefs = Preferences.userNodeForPackage(this.getClass());
		port = prefs.getInt("PORT", port);
		
		
	
		ConnectivityChecker checker = new ConnectivityChecker();
		checker.start();
		
		Runnable test = new ConnectionAccepter();
		new Thread(test).start();
		
		
		//Make ActionListeners for menu items.
		portMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String newPort = (String) JOptionPane.showInputDialog(frmEpisodeManager, "Please enter the port you want the server to listen on.", "Port", JOptionPane.QUESTION_MESSAGE, null, null, prefs.getInt("PORT", port));
				if(newPort==null){
				}
				else if(newPort.isEmpty()){
				}
				else{
					port=Integer.parseInt(newPort);
					prefs.putInt("PORT", port);
					try {
						prefs.flush();
					} catch (BackingStoreException e1) {
						JOptionPane.showMessageDialog(frmEpisodeManager, "There was an error storing the values.  You may need\nto set them again the next time you run the program!", "Options Error", JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
					}
					JOptionPane.showMessageDialog(frmEpisodeManager, "You should probably restart the program now to avoid\nit still listening for the next connection on the old port.", "Please Restart", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		
		aboutMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JOptionPane.showMessageDialog(frmEpisodeManager, 		  "This program is meant to pair with the Remote Torrent Client\n"
																		+ "program in order to allow people to remotely initiate torrent\n"
																		+ "downloads into your media server.  It will also eventually manage\n"
																		+ "your TV shows and automatically download the newest episodes the\n"
																		+ "day they come out on TV automatically!", "About", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
	}
	
	public static void incrementConnectionsLabel()
	{
		connectionsLabel.setText(Integer.toString(Integer.parseInt(connectionsLabel.getText())+1));
	}
	
	public static boolean checkConnection()
	{
		boolean online = false;		
		URL url;
		try 
		{
			url = new URL("http://www.google.com");
			final URLConnection conn = url.openConnection();
			conn.setConnectTimeout(750);
			conn.setReadTimeout(750);
			conn.connect();
			onlineLabel.setText("Online");
			onlineLabel.setForeground(Color.BLUE);
			online=true;
		} 
		catch (Exception e) 
		{
			//e.printStackTrace();
			onlineLabel.setText("Offline");
			onlineLabel.setForeground(Color.RED);
			online=false;
			return online;
		}
		return online;
	}

	private static void checkFirstRun() throws Exception
	{
		if(prefs.getBoolean("FIRST RUN", true))
		{
			// First run, put installation and configuration here.
			prefs.putBoolean("FIRST RUN", false);
		}
		else if(!prefs.getBoolean("FIRST RUN",  true))
		{
			// This runs on subsequent launches of the program.
		}
	}

	private static void checkDependancies()
	{
		try
		{
			if(!new File("aria2c.exe").exists())
			{
				System.out.println("Needs aria2c.exe");
				URL inputUrl = TorrentServer.class.getResource("/res/aria2c.exe");
				File dest = new File("aria2c.exe");
				FileUtils.copyURLToFile(inputUrl, dest);
			}
			if(!new File("downloader-script.bat").exists())
			{
				System.out.println("Needs downloader-script.bat");
				URL inputUrl = TorrentServer.class.getResource("/res/downloader-script.bat");
				File dest = new File("downloader-script.bat");
				FileUtils.copyURLToFile(inputUrl, dest);
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

}

class ConnectivityChecker extends Thread
{
	@Override public void run()
	{
		while(true)
		{
			//boolean internetAccess = MainWindow.checkConnection();
			//if(internetAccess) System.out.println("Internet Status: Connected");
			//else System.out.println("Internet Status: Not Connected");
			TorrentServer.checkConnection();
			try{
				Thread.sleep(1000);}
			catch(InterruptedException e){
				e.printStackTrace();
			}
		}
	}
}