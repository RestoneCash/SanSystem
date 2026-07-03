package com.restonecash.sansystem.api.config;

import java.util.function.Consumer;

public class DefaultConfig {
    public int pollution=0;
    public int mentalRecover=0;
    public int mentalResilience=0;
    public int maxSan=-1;
    public int minSan=0;
    public boolean ifSanKill=true;

    public DefaultConfig(Consumer<DefaultConfig> intialize) throws RuntimeException {
        intialize.accept(this);
        build();
    }

    public DefaultConfig() {
    }

    public DefaultConfig setPollution(int i) {
        this.pollution = i;
        return this;
    }

    public DefaultConfig setMentalRecover(int i) {
        this.mentalRecover = i;
        return this;
    }

    public DefaultConfig setMentalResilience(int i) {
        this.mentalResilience = i;
        return this;
    }

    public DefaultConfig setMaxSan(int i) {
        this.maxSan = i;
        return this;
    }

    public DefaultConfig setMinSan(int i) {
        this.minSan = i;
        return this;
    }

   public  DefaultConfig setIfSanKill(boolean ifSanKill) {
        this.ifSanKill = ifSanKill;
        return this;
   }

    public DefaultConfig build() throws RuntimeException {
        if (!this.validate())
            throw new RuntimeException("你未正确定义所有需定义的项");
        return this;
    }

    private boolean validate() {
        return maxSan >= 0;
    }
}
