package com.samleighton.sethomestwo.datatypes;

import org.apache.commons.lang.SerializationUtils;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Arrays;

public class PersistentString implements PersistentDataType<byte[], String> {
    @Override
    public Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public Class<String> getComplexType() {
        return String.class;
    }

    @Override
    public byte[] toPrimitive(String s, PersistentDataAdapterContext persistentDataAdapterContext) {
        return SerializationUtils.serialize(s);
    }

    @Override
    public String fromPrimitive(byte[] bytes, PersistentDataAdapterContext persistentDataAdapterContext) {
        try {
            InputStream is = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(is);

            return (String) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return Arrays.toString(new byte[0]);
    }
}
