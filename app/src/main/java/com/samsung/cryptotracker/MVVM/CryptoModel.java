package com.samsung.cryptotracker.MVVM;
import org.json.JSONObject;

public class CryptoModel {
    private JSONObject cryptoInfo;


    public JSONObject getCryptoInfo() {
        return cryptoInfo;
    }

    public void setCryptoInfo(JSONObject cryptoInfo) {
        this.cryptoInfo = cryptoInfo;
    }
}