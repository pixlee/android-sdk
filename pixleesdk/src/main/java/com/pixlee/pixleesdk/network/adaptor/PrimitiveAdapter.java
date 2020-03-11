package com.pixlee.pixleesdk.network.adaptor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pixlee.pixleesdk.network.annotation.NullableBoolean;
import com.pixlee.pixleesdk.network.annotation.NullableDouble;
import com.pixlee.pixleesdk.network.annotation.NullableInt;
import com.pixlee.pixleesdk.network.annotation.NullableLong;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonReader;

import java.io.IOException;

public class PrimitiveAdapter {
    @FromJson
    @NullableInt
    public int intFromJson(@NonNull final JsonReader reader) throws IOException {
        if (reader.peek() == JsonReader.Token.NUMBER || reader.peek() == JsonReader.Token.STRING) {
            return reader.nextInt();
        } else if (reader.peek() == JsonReader.Token.NULL) {
            reader.nextNull();
        }

        return NullableInt.NONE;
    }

    @FromJson
    @NullableBoolean
    public boolean booleanFromJson(@NonNull final JsonReader reader) throws IOException {
        if (reader.peek() == JsonReader.Token.BOOLEAN || reader.peek() == JsonReader.Token.STRING) {
            return reader.nextBoolean();
        } else if (reader.peek() == JsonReader.Token.NULL) {
            reader.nextNull();
        }

        return NullableBoolean.NONE;
    }

    @FromJson
    @NullableDouble
    public double doubleFromJson(@NonNull final JsonReader reader) throws IOException {
        if (reader.peek() == JsonReader.Token.NUMBER || reader.peek() == JsonReader.Token.STRING) {
            return reader.nextDouble();
        } else if (reader.peek() == JsonReader.Token.NULL) {
            reader.nextNull();
        }

        return NullableDouble.NONE;
    }

    @FromJson
    @NullableLong
    public long longFromJson(@NonNull final JsonReader reader) throws IOException {
        if (reader.peek() == JsonReader.Token.NUMBER || reader.peek() == JsonReader.Token.STRING) {
            return reader.nextLong();
        } else if (reader.peek() == JsonReader.Token.NULL) {
            reader.nextNull();
        }

        return NullableLong.NONE;
    }
}
