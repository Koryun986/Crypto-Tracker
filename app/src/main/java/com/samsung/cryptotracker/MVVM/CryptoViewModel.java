package com.samsung.cryptotracker.MVVM;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class CryptoViewModel extends ViewModel {
    private CryptoRepo cryptoRepo;
    private Context context;
    private MutableLiveData<List<CryptoModel>> mutableLiveData;

    public CryptoViewModel(Context context){
        cryptoRepo = new CryptoRepo();
        this.context = context;
    }

    public LiveData<List<CryptoModel>> getCrypto() {
        if(mutableLiveData==null){
            mutableLiveData = cryptoRepo.requestCrypto(context);
        }
        return mutableLiveData;
    }
}
