package matmos.minecraft;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class MAtOnlineDatabaseFetcher extends Thread {
	URL url;

	public MAtOnlineDatabaseFetcher() {
		this.setName("MAtmos Online Database fetcher");
		this.setDaemon(true);
	}

	public void getDatabase(URL var1) {
		if(this.isAlive()) {
			this.interrupt();
		}

		this.url = var1;
		this.start();
	}

	public void run() {
		try {
			URLConnection url = this.url.openConnection();
			InputStream input = url.getInputStream();
			Scanner scan = new Scanner(input);
			scan.useDelimiter("\\Z");
			String var4 = "UTF-8";
			String var5 = scan.next();
			ByteArrayInputStream var6 = new ByteArrayInputStream(var5.getBytes(var4));
			MAtmos.takeOnlineDatabase(var6);
		} catch (UnsupportedEncodingException var7) {
			MAtmos.printMessageSilent("Encoding exception.");
			MAtmos.failOnlineDatabase();
		} catch (IOException var8) {
			MAtmos.printMessageSilent("I/O exception.");
			MAtmos.failOnlineDatabase();
		}

	}
}
