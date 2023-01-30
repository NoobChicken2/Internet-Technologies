package Server;

public class FileTransferClass {
    private FileTransferSession uploader;
    private FileTransferSession downloader;

    public FileTransferClass(FileTransferSession session) {
        if (session.getRole().equals("U")) {
            this.uploader = session;
        } else if (session.getRole().equals("D")) {
            this.downloader = session;
        }
    }
}
