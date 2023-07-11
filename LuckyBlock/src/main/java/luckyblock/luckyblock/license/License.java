package luckyblock.luckyblock.license;

import org.bukkit.Bukkit;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class License {
    public License(String url, String token) throws Exception {
        Get ip = new Get();
        checkLicense(ip.getIP(), token, url);
    }

    private void checkLicense(String ip, String token, String url) throws IOException, ParseException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection)obj.openConnection();
        con.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
        con.setRequestMethod("POST");
        String urlParameters = "ip=" + ip + "&urun_id=" + token;
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);
        in.close();
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject)parser.parse(response.toString());
        jsonObject = (JSONObject)parser.parse(jsonObject.get("data").toString());
        if (jsonObject.get("status").equals(jsonObject.isEmpty()) || !jsonObject.get("status").toString().equals("true")) {
            Bukkit.getServer().shutdown();
        }
    }
}
