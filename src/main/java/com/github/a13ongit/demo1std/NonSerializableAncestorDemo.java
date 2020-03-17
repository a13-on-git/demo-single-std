package com.github.a13ongit.demo1std;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * A runnable demo of squid:S2055
 * "The non-serializable super class of a "Serializable" class should have a no-argument constructor".
 * 
 * <blockquote>
 * "When a Serializable object has a non-serializable ancestor in its inheritance chain, 
 * object deserialization (re-instantiating the object from file) starts at the first non-serializable class, 
 * and proceeds down the chain, adding the properties of each subsequent child class, 
 * until the final object has been instantiated.
 * 
 * In order to create the non-serializable ancestor, its no-argument constructor is called. 
 * Therefore the non-serializable ancestor of a Serializable class must have a no-arg constructor. 
 * Otherwise the class is Serializable but not deserializable."
 * </blockquote>
 * 
 * @since 2020-03-17 12:18
 */
public class NonSerializableAncestorDemo {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
        
        Raspberry raspberry = new Raspberry(Season.FALL, "Fall Gold");
        objOut.writeObject(raspberry);
        objOut.close();
        byte[] serializedFruitBytes = byteOut.toByteArray();
        System.out.format("Serialized %d bytes.\n", serializedFruitBytes.length);
        
        System.out.println("Deserializing...");
        ByteArrayInputStream bis = new ByteArrayInputStream(serializedFruitBytes);
        ObjectInputStream ois = new ObjectInputStream(bis);
        ois.readObject(); // fails with java.io.InvalidClassException: [...] Raspberry; no valid constructor
    }
}

enum Season { WINTER, SPRING, SUMMER, FALL };

class Fruit {

    private Season ripe;

    public Fruit(Season ripe) {
        this.ripe = ripe;
    }
}

// Noncompliant; nonserializable ancestor doesn't have no-arg constructor.
class Raspberry extends Fruit implements java.io.Serializable {  

    private String variety;

    public Raspberry(Season ripe, String variety) {
        super(ripe);
        this.variety = variety;
    }
}