package com.yzb;

import com.yzb.common.DiscardServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NetdemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(NetdemoApplication.class, args);
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
        try {
            new DiscardServer(port).run();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("server:run()");
    }

}
