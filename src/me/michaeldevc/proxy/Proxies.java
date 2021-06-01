package me.michaeldevc.proxy;


import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Proxies {
    private final List<Proxy> proxies = new ArrayList();
    private int index = 0;

    public Proxies() {
    }

    public void init(String fileName, Type type) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }

        Scanner scanner = new Scanner(file);

        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.contains(" ")) {
                line = line.replace(" ", "");
            }

            if (!line.isEmpty() && !line.startsWith("##") && line.contains(":")) {
                String[] ip = line.split(":");
                if (ip.length < 1) {
                    return;
                }

                String hostname = ip[0];
                int port = 8080;

                try {
                    port = Integer.parseInt(ip[1]);
                } catch (NumberFormatException var10) {
                }

                Proxy proxy = new Proxy(type, new InetSocketAddress(hostname, port));
                this.proxies.add(proxy);
            }
        }

    }

    public int size() {
        return this.proxies.size();
    }

    public Proxy nextProxy() {
        if (this.proxies.size() == 0) {
            return Proxy.NO_PROXY;
        } else {
            if (this.index >= this.proxies.size()) {
                this.index = 0;
            }

            return (Proxy)Objects.requireNonNull((Proxy)this.proxies.get(this.index++));
        }
    }

    public void removeProxy(Proxy proxy) {
        this.proxies.remove(proxy);
    }
}
