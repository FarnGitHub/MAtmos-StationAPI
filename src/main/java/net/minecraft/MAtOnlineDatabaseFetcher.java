package net.minecraft;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class MAtOnlineDatabaseFetcher extends Thread {
	MAtmos mod;
	URL url;

	public MAtOnlineDatabaseFetcher(MAtmos var1) {
		this.setName("MAtmos Online Database fetcher");
		this.setDaemon(true);
		this.mod = var1;
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
			URLConnection var1 = this.url.openConnection();
			InputStream var2 = var1.getInputStream();
			Scanner var3 = new Scanner(var2);
			var3.useDelimiter("\\Z");
			String var4 = "UTF-8";
			String var5 = var3.next();
			ByteArrayInputStream var6 = new ByteArrayInputStream(var5.getBytes(var4));
			this.mod.takeOnlineDatabase(var6);
		} catch (UnsupportedEncodingException var7) {
			this.mod.printMessageSilent("Encoding exception.");
			this.mod.failOnlineDatabase();
		} catch (IOException var8) {
			this.mod.printMessageSilent("I/O exception.");
			this.mod.failOnlineDatabase();
		}

	}
}
