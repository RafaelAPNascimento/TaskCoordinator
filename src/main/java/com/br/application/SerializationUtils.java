package com.br.application;

import java.io.*;

public class SerializationUtils {

    public static byte[] serialize(Object object) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try (ObjectOutput objectOutput = new ObjectOutputStream(byteArrayOutputStream)) {

            objectOutput.writeObject(object);
            objectOutput.flush();
            return byteArrayOutputStream.toByteArray();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }

    public static Object deserialize(byte[] data) {

        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);

        try (ObjectInput objectInput = new ObjectInputStream(inputStream)){

            Object object = objectInput.readObject();
            return object;
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new Object();
    }
}
