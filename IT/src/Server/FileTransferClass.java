package Server;

public class FileTransferClass {
    private FileTransferSession uploader;
    private FileTransferSession downloader;

    public FileTransferClass(FileTransferSession session) {
        if (session.getIdentifier().equals("U")) {
            this.uploader = session;
        } else if (session.getIdentifier().equals("D")) {
            this.downloader = session;
        }
    }
}
