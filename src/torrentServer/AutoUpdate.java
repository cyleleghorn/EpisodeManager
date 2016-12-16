package torrentServer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Arrays;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;


public class AutoUpdate
{
	private static String	data			= null;
	private static String	newVersion		= "0.0.0";
	private static String[]	currentVersion	= TorrentServer.version.split("\\.");
	private static String[]	remoteVersion	= new String[] { "0", "0", "0" };
	
	
	public static void checkForUpdates(String callBackLocation)
	{
		Runnable r = new Runnable()
		{
			@Override
			public void run()
			{
				
				URL url;
				try
				{
					url = new URL("http://islandpi.noip.me:8080/torrentserver/version.html");
					InputStream html = null;
					
					html = url.openStream();
					
					int c = 0;
					StringBuffer buffer = new StringBuffer("");
					
					while (c != -1)
					{
						c = html.read();
						
						buffer.append((char) c);
					}
					
					data = buffer.toString();
					data = data.substring(data.indexOf("id=\"version\">") + 13, data.indexOf("</div>"));
					remoteVersion = data.substring(1).split("\\.");
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
				
				// Make the decision on what to do.
				if (!Arrays.equals(remoteVersion, new String[] { "0", "0", "0" })
						&& Arrays.equals(remoteVersion, TorrentServer.version.split("\\.")))
				{
					if (callBackLocation.equals("LAUNCH"))
					{
						System.out.println("You have the latest version.");
					}
					else if (callBackLocation.equals("MENU"))
					{
						JOptionPane.showMessageDialog(TorrentServer.frmEpisodeManager, "You have the latest version!",
								"Up to date", JOptionPane.INFORMATION_MESSAGE);
					}
				}
				else if (!Arrays.equals(remoteVersion, new String[] { "0", "0", "0" })
						&& !Arrays.equals(remoteVersion, TorrentServer.version.split("\\.")))
				{
					if (needsUpdate()) // Actually check to see if the
										// remoteVersion is bigger.
					{
						System.out.println("You do not have the latest version.  Please update");
						int updateOrNah = JOptionPane.showConfirmDialog(TorrentServer.frmEpisodeManager,
								"There is a new version available! Do you wish to update?\nYour version: V"
										+ TorrentServer.version + "\nLatest version: V" + remoteVersion[0] + "."
										+ remoteVersion[1] + "." + remoteVersion[2],
								"Please Update", JOptionPane.YES_NO_OPTION);
						
						if (updateOrNah == 0)
						{
							// true; update
							newVersion = remoteVersion[0] + "." + remoteVersion[1] + "." + remoteVersion[2];
							update();
						}
						else
						{
							// They don't want to update. Fuck em.
						}
					}
					else
					{
						System.out.println("Your version is higher than the server version.");
					}
				}
				else if (Arrays.equals(remoteVersion, new String[] { "0", "0", "0" }))
				{
					System.out.println("There was an error contacting the update server.");
					JOptionPane.showMessageDialog(TorrentServer.frmEpisodeManager,
							"Could not check for latest version!\nThe update server may be offline. Check your connection and try again.",
							"Update Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		};
		
		new Thread(r).start();
	}
	
	
	private static void update()
	{
		createBatch();
		
		// Download the new jar file
		try
		{
			FileUtils.copyURLToFile(new URL("http://islandpi.noip.me:8080/torrentserver/update.jar"),
					new File("update.jar"));
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(TorrentServer.frmEpisodeManager, "Download failed!", "Error",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		
		// Run the batch file
		
		ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/C", "start", "update.bat");
		try
		{
			builder.start();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		// close it all out now.
		System.exit(0);
	}
	
	
	private static boolean needsUpdate()
	{
		for (int i = 0; i < 3; i++)
		{
			if (Integer.parseInt(remoteVersion[i]) > Integer.parseInt(currentVersion[i]))
			{
				return true;
			}
			else if (Integer.parseInt(remoteVersion[i]) < Integer.parseInt(currentVersion[i])) { return false; }
		}
		return false;
	}
	
	
	private static void createBatch()
	{
		File batch = new File("update.bat");
		try
		{
			PrintWriter out = new PrintWriter(new FileOutputStream(batch));
			
			out.println("@ECHO off");
			out.println("echo Updating, please wait...");
			
			// Get current file name
			// Must use main class file.
			File dir = new File(TorrentServer.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			String jar = System.getProperty("java.class.path");
			File f = new File(dir, jar);
			// JOptionPane.showMessageDialog(null, f.getName());
			// System.out.println(f.getName());
			String jarName = f.getName();
			
			out.println("del \"" + jarName + "\"");
			
			out.println("rename update.jar \"Torrent Server V" + newVersion + ".jar\"");
			
			// This line deletes itself.
			
			out.println("start javaw -jar \"Torrent Server V" + newVersion + ".jar\"");
			out.println("echo Done updating!  You can close this window now");
			out.println("(goto) 2>nul & del \"%~f0\" & exit");
			
			out.flush();
			out.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
