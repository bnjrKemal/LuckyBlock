package luckyblock.luckyblock.license;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class Get {
    public String getIP() throws IOException {
        URL url = new URL("http://checkip.amazonaws.com/");
        Scanner sc = new Scanner(url.openStream());
        StringBuilder sb = new StringBuilder();
        while (sc.hasNext())
            sb.append(sc.next());
        return sb.toString();
    }
}
