package torrentServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ConnectionAccepter implements Runnable
{
	private ServerSocket socket;
	
	
	public ConnectionAccepter()
	{
		
		try
		{
			socket = new ServerSocket(TorrentServer.prefs.getInt("PORT", TorrentServer.port));
			System.out.println("New ConnectionAccepter created and socket opened successfully.");
		}
		catch (IOException e)
		{
			System.out.println("There was an error opening a new socket in ConnectionAccepter.");
			e.printStackTrace();
		}
		
	}
	
	
	@Override
	public void run()
	{
		while (true)
		{
			System.out.println("Waiting for connection to socket....");
			try
			{
				Socket acceptedSocket = socket.accept();
				TorrentServer.incrementConnectionsLabel();
				Runnable workerThread = new WorkerThread(acceptedSocket);
				new Thread(workerThread).start();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			System.out.println("The acceptedSocket should have been handed off to the workerThread now.");
		}
	}
}
