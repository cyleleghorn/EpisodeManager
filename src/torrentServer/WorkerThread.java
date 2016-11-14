package torrentServer;

import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;

import javax.swing.SwingUtilities;

public class WorkerThread implements Runnable
{
	Socket workerSocket = null;
	BufferedReader br = null;
	XMLEncoder encoder = null;

	public WorkerThread(Socket acceptedSocket)
	{
		workerSocket=acceptedSocket;
	}

	@Override
	public void run()
	{
		try 
		{
			br = new BufferedReader(new InputStreamReader(workerSocket.getInputStream()));
			String incomingMessage = br.readLine();
			handleMessage(incomingMessage);
			
			/*
			while((incomingMessage=br.readLine())!=null)
			{
				JOptionPane.showMessageDialog(null, "A transmission was received: " + incomingMessage);
				handleMessage(incomingMessage);
			}
			*/
			
			br.close();
			workerSocket.close();
			//JOptionPane.showMessageDialog(null, "workerSocket was closed.");
			
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					TorrentServer.logTextArea.repaint();
					//MainWindow.scroller.setSize(MainWindow.scroller.getPreferredSize().width+50, MainWindow.scroller.getPreferredSize().height);
					TorrentServer.scroller.repaint();
					TorrentServer.frmEpisodeManager.getContentPane().repaint();
					//MainWindow.frmEpisodeManager.pack();
					TorrentServer.frmEpisodeManager.repaint();
				}
			});
			
		} 
		catch (IOException e) 
		{
			System.out.println("There was an error with workerSocket and/or receiving a message.");
			e.printStackTrace();
		}
	}

	public void handleMessage(String message)
	{
		String[] messageArray = message.split(",");
		
		//JOptionPane.showMessageDialog(null, "Handle message got called.\n\nPart 1: " + messageArray[0] + "\nPart 2: " + messageArray[1]);

		if(messageArray[0].equals("TV") || messageArray[0].equals("MOVIE"))
		{
			//All this shit runs if it's actually a download.
			
			TorrentServer.logTextArea.append("New download initiating!!\n");
			TorrentServer.logTextArea.append("Type: " + messageArray[0] + "\n");
			TorrentServer.logTextArea.append("Title: " + messageArray[1] + "\n\n\n");
			

			//String command = "cmd D:/Media/aria2/downloader-script.bat " + messageArray[0] + " " + "\"" + messageArray[1] + "\"";
			//System.out.println(command);
			messageArray[2] = "\"" + messageArray[2] + "\"";

			ProcessBuilder builder = new ProcessBuilder("cmd.exe","/C","start","D:/Media/aria2/downloader-script.bat",messageArray[0],messageArray[2]);
			try 
			{
				builder.start();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		else if(messageArray[0].equals("QUERY"))
		{
			//This will run if there is a query.
			//JOptionPane.showMessageDialog(null, "It was a Query.");
			TorrentServer.logTextArea.append("New query received!!\n");
			TorrentServer.logTextArea.append("Query: \"" + messageArray[1] + "\"\n");
			
			TorrentSearcher searcher = new TorrentSearcher();
			List<List<String>> results = searcher.searchTPB(messageArray[1]);
			
			try {
				//JOptionPane.showMessageDialog(null, "About to try and send the ArrayList.");
								
				
				encoder = new XMLEncoder(new BufferedOutputStream(workerSocket.getOutputStream()));
				encoder.writeObject(results);
				encoder.flush();
				encoder.close();
				
				TorrentServer.logTextArea.append("Results were sent back to the client.\n\n");
			} catch (IOException e) {
				//JOptionPane.showMessageDialog(null, "There was an error sending the results back to the client.", "Error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
		
	}

}
