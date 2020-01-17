package com.pixlee.pixleesdk.network.adaptor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pixlee.pixleesdk.network.annotation.NullableDouble;
import com.pixlee.pixleesdk.network.annotation.NullableLong;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonReader;

import java.io.IOException;

public class PrimitiveAdapter {
    @FromJson
    public int intFromJson(@Nullable Integer value) {
        if (value == null) {
            return 0;
        }

        return value;
    }

    @FromJson
    public boolean booleanFromJson(@Nullable Boolean value) {
        if (value == null) {
            return false;
        }

        return value;
    }

    @FromJson
    @NullableDouble
    public double doubleFromJson(@NonNull final JsonReader reader) throws IOException {
        if (reader.peek() == JsonReader.Token.NUMBER) {
            return reader.nextDouble();
        } else if (reader.peek() == JsonReader.Token.NULL) {
            reader.nextNull();
        }

        return NullableDouble.NONE;
    }

    @FromJson
    @NullableLong
    public long longFromJson(@NonNull final JsonReader reader) throws IOException {
        if (reader.peek() == JsonReader.Token.NUMBER) {
            return reader.nextLong();
        } else if (reader.peek() == JsonReader.Token.NULL) {
            reader.nextNull();
        }

        return NullableLong.NONE;
    }

}
