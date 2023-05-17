package com.gulfcam.fuelcoupon.cryptage;

public interface Cryptage<E> {

    String encryptObject(String passPhrase, E plainText);
    E deryptObject(String passPhrase, String plainText);
}
