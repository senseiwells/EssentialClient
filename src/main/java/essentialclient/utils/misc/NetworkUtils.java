package essentialclient.utils.misc;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class NetworkUtils {
	public static String getStringFromUrl(String url) {
		try {
			InputStream inputStream = new URL(url).openStream();
			if (inputStream == null) {
				throw new IOException();
			}
			Writer stringWriter = new StringWriter();
			char[] charBuffer = new char[2048];
			try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
				int counter;
				while ((counter = reader.read(charBuffer)) != -1) {
					stringWriter.write(charBuffer, 0, counter);
				}
			}
			finally {
				inputStream.close();
			}
			return stringWriter.toString();
		}
		catch (IOException ioException) {
			return null;
		}
	}
}
