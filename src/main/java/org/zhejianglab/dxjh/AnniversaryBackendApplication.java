package org.zhejianglab.dxjh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@SpringBootApplication
public class AnniversaryBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnniversaryBackendApplication.class, args);
    }

}
