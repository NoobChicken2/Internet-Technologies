package Server.Heartbeat;

import Server.MessageProcessor;
import Server.ServerResponse.ServerResponseQuit;

import java.util.Timer;
import java.util.TimerTask;

public class Heartbeat implements Runnable{

    private static final boolean SHOULD_PING = true;
    private static final int PING_FREQ_MS = 10000;
    private static final int PING_TIMEOUT_MS = 3000;
    private boolean isPongReceived = false;
    private boolean isPingSent = false;

    private MessageProcessor messageProcessor;

    public Heartbeat (MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    public void setPongTrue() {
        this.isPongReceived = true;
    }

    public boolean getIsPingSet() {
        return isPingSent;
    }

    @Override
    public void run() {
        if (SHOULD_PING) {
            Timer pingTimer = new Timer();
            // Ping Task
            TimerTask ping = new TimerTask() {
                @Override
                public void run() {
                    isPongReceived = false;
                    messageProcessor.sendMessage("PING");
                    isPingSent = true;
                    //Have the Heartbeat thread wait for 3 seconds
                    try {
                        Thread.sleep(PING_TIMEOUT_MS);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    // Check if the pong is received after 3 seconds
                    if (!isPongReceived){
                        new ServerResponseQuit(messageProcessor).respond("DSCN Pong timeout");
                        pingTimer.cancel();
                        pingTimer.purge();
                    }
                }
            };

            // Ping timer starter
            pingTimer.schedule(ping, PING_FREQ_MS, PING_FREQ_MS);
        }

    }
}
