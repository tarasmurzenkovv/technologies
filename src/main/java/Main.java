
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.sound.midi.Soundbank;

import com.github.badoualy.telegram.api.Kotlogram;
import com.github.badoualy.telegram.api.TelegramApp;
import com.github.badoualy.telegram.api.TelegramClient;
import com.github.badoualy.telegram.tl.api.*;
import com.github.badoualy.telegram.tl.api.auth.TLAbsSentCode;
import com.github.badoualy.telegram.tl.api.auth.TLAuthorization;
import com.github.badoualy.telegram.tl.api.auth.TLSentCode;
import com.github.badoualy.telegram.tl.api.contacts.TLResolvedPeer;
import com.github.badoualy.telegram.tl.api.messages.TLAbsDialogs;
import com.github.badoualy.telegram.tl.api.messages.TLAbsMessages;
import com.github.badoualy.telegram.tl.api.messages.TLChats;
import com.github.badoualy.telegram.tl.api.request.TLRequestChannelsCheckUsername;
import com.github.badoualy.telegram.tl.api.request.TLRequestChannelsGetChannels;
import com.github.badoualy.telegram.tl.api.request.TLRequestChannelsGetMessages;
import com.github.badoualy.telegram.tl.api.request.TLRequestContactsResolveUsername;
import com.github.badoualy.telegram.tl.api.request.TLRequestMessagesGetHistory;
import com.github.badoualy.telegram.tl.core.TLIntVector;
import com.github.badoualy.telegram.tl.core.TLObject;
import com.github.badoualy.telegram.tl.core.TLVector;
import com.github.badoualy.telegram.tl.exception.RpcErrorException;

/**
 * https://jitpack.io/p/majidasgari/kotlogram https://kotlogram.readme.io/docs/sign-in-with-an-existing-account
 */
public class Main {

    public static final int API_ID = 354221;
    public static final String API_HASH = "2793be4cd5d9d86144e7eeab916da7fd";

    // What you want to appear in the "all sessions" screen
    public static final String APP_VERSION = "AppVersion";
    public static final String MODEL = "Model";
    public static final String SYSTEM_VERSION = "SysVer";
    public static final String LANG_CODE = "en";

    public static TelegramApp application = new TelegramApp(API_ID, API_HASH, MODEL, SYSTEM_VERSION,
            APP_VERSION, LANG_CODE);

    // Phone number used for tests
    public static final String PHONE_NUMBER = "+********"; // International format


    public static void main(String[] args) throws IOException, RpcErrorException {

        TelegramClient client = Kotlogram.getDefaultClient(application, new ApiStorage());
        // Send code to account
        TLAbsSentCode tlAbsSentCode = client.authSendCode(PHONE_NUMBER, 5);
        System.out.println("Authentication code: ");
        String code = new Scanner(System.in).nextLine();

        // Auth with the received code
        TLAuthorization authorization = client.authSignIn(PHONE_NUMBER, tlAbsSentCode.getPhoneCodeHash(), code);
        TLUser self = authorization.getUser().getAsUser();
        System.out.println("You are now signed in as " + self.getFirstName() + " " + self.getLastName() + " @" + self.getUsername());

        client.executeRpcQuery(new TLRequestChannelsGetMessages())

        TLAbsDialogs tlAbsDialogs = client.messagesGetDialogs(0, 0, new TLInputPeerEmpty(), 1);
        getInputPeer(tlAbsDialogs).forEach(tlAbsInputPeer -> {
            TLAbsMessages tlAbsMessages = null;
            try {
                tlAbsMessages = client.messagesGetHistory(tlAbsInputPeer, 0, 0, 0, 10, 0);
            } catch (RpcErrorException e) {
            } catch (IOException e) {
                e.printStackTrace();
            }
            tlAbsMessages.getMessages().forEach(message -> {
                if (message instanceof TLMessage)
                    System.out.println(((TLMessage) message).getMessage());
                else
                    System.out.println("Service message");
            });
        });


    }

    public static List<TLAbsInputPeer> getInputPeer(TLAbsDialogs tlAbsDialogs) {
        List<TLAbsInputPeer> inputPeers = new LinkedList<>();
        TLVector<TLAbsDialog> dialogs = tlAbsDialogs.getDialogs();
        for(TLAbsDialog tlAbsDialog : dialogs) {
            TLAbsPeer tlAbsPeer = tlAbsDialog.getPeer();
            int peerId = getId(tlAbsPeer);
            TLObject peer = tlAbsPeer instanceof TLPeerUser ?
                    tlAbsDialogs.getUsers().stream().filter(user -> user.getId() == peerId).findFirst().get()
                    : tlAbsDialogs.getChats().stream().filter(chat -> chat.getId() == peerId).findFirst().get();

            if (peer instanceof TLChannel)
                inputPeers.add(new TLInputPeerChannel(((TLChannel) peer).getId(), ((TLChannel) peer).getAccessHash()));
        }
        return inputPeers;
    }

    public static int getId(TLAbsPeer peer) {
        if (peer instanceof TLPeerUser)
            return ((TLPeerUser) peer).getUserId();
        if (peer instanceof TLPeerChat)
            return ((TLPeerChat) peer).getChatId();

        return ((TLPeerChannel) peer).getChannelId();
    }
}
