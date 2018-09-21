import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.github.badoualy.telegram.api.TelegramApiStorage;
import com.github.badoualy.telegram.mtproto.DataCenter;
import com.github.badoualy.telegram.mtproto.auth.AuthKey;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ApiStorage implements TelegramApiStorage {

  public static final File AUTH_KEY_FILE = new File("Properties/auth.key");
  public static final File NEAREST_DC_FILE = new File("Properties/dc.save");


  @Override
  public void deleteAuthKey() {
    try {
      FileUtils.forceDelete(AUTH_KEY_FILE);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void deleteDc() {
    try {
      FileUtils.forceDelete(NEAREST_DC_FILE);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Nullable
  @Override
  public AuthKey loadAuthKey() {
    try {
      return new AuthKey(FileUtils.readFileToByteArray(AUTH_KEY_FILE));
    } catch (IOException e) {
      if (!(e instanceof FileNotFoundException))
        e.printStackTrace();
    }

    return null;
  }

  @Nullable
  @Override
  public DataCenter loadDc() {
    try {
      String[] infos = FileUtils.readFileToString(NEAREST_DC_FILE).split(":");
      return new DataCenter(infos[0], Integer.parseInt(infos[1]));
    } catch (IOException e) {
      if (!(e instanceof FileNotFoundException))
        e.printStackTrace();
    }

    return null;
  }

  @Nullable
  @Override
  public Long loadServerSalt() {
    return null;
  }

  @Override
  public void saveAuthKey(@NotNull AuthKey authKey) {
    try {
      FileUtils.writeByteArrayToFile(AUTH_KEY_FILE, authKey.getKey());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void saveDc(@NotNull DataCenter dataCenter) {

  }

  @Override
  public void saveServerSalt(long l) {

  }
}
