# EpisodeManager
This is the other half of the RemoteTorrentDownloader program.  This half actually initiates the downloads,
and will eventually be able to manage your library automatically.

This program just runs in the background and constantly accepts incoming connections.
It uses an external command line torrent program called Aria2 (https://aria2.github.io/) to
download any torrents.  A batch file I created allows you to set the download locations for
different types of media.  I have not yet included the batch file or Aria2 in my repository,
I need to check the license for Aria2 and I need to get the batch file from my home computer.

When a user with the RemoteTorrentProgram initiates a search or a download, the Episode Manager
will receive the request and process it accordingly.  There is a counter and a log on the main
window to let you see the search/download history since the program has been running.
Because this half of the program actually contacts www.ThePirateBay.org, the RemoteTorrentDownloader
can be used from work and school internet connections even if website blockers are in place.
You must ensure that there is no program or router blocking access to www.ThePirateBay.org from
the server computer where EpisodeManager and the media server run.

I will make a release including everything when it is complete, and Aria2 and the batch file
will probably self extract from the jar file on first run.

This program is currently released as is. Redistribution is forbidden. Modification is permitted for personal use, but under no circumstances may any modified version of this program be published or redistributed for free or for profit by any user. This program could potentially be used for copyright infringement. If any user chooses to use this program to download copyright-protected content, they are doing so under their own free will and are responsible for their own actions as well as any repercussions that may result from those actions. By downloading and using this program, the user agrees to be responsible for their own actions and that no attempt shall be made to place any liability on the creator.
