package com.swisspost.swisscrypto;

import org.springframework.boot.SpringApplication;

public class TestSwissCryptoApplication {

  public static void main(String[] args) {
    SpringApplication.from(SwissCryptoApplication::main).with(TestcontainersConfiguration.class).run(args);
  }

}
