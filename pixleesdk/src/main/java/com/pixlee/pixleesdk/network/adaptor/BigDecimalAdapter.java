package com.pixlee.pixleesdk.network.adaptor;

import androidx.annotation.NonNull;

import com.pixlee.pixleesdk.network.annotation.FieldBigDecimal;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.ToJson;

import java.io.IOException;
import java.math.BigDecimal;

public class BigDecimalAdapter {
    @ToJson
    String toJson(@FieldBigDecimal BigDecimal value) {
        if (value == null)
            return null;
        else
            return value.toString();
    }

    /*@FromJson
    @FieldBigDecimal
    BigDecimal fromJson(String value) {
        if (value == null)
            return null;
        else
            return new BigDecimal(value);
    }*/

    @FromJson
    @FieldBigDecimal
    public BigDecimal fromJson(@NonNull final JsonReader reader) throws IOException {
        if(reader.peek() == JsonReader.Token.STRING){
            return new BigDecimal(reader.nextString());
        }
        if (reader.peek() == JsonReader.Token.NUMBER) {
            return new BigDecimal(String.valueOf(reader.nextDouble()));
        } else if (reader.peek() == JsonReader.Token.NULL) {
            reader.nextNull();
        }

        return FieldBigDecimal.NONE;
    }
}
