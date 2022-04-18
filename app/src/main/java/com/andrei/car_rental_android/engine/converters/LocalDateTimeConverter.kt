package com.andrei.car_rental_android.engine.converters

import com.andrei.car_rental_android.engine.utils.DateUtils
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.LocalDateTime
import java.time.ZoneId

class LocalDateTimeConverter : TypeAdapter<LocalDateTime?>() {

    override fun write(out: JsonWriter, value: LocalDateTime?) {
        if(value == null){
            out.nullValue()
        }else{
            val unix = value.atZone(ZoneId.systemDefault()).toEpochSecond()
            out.value(unix)
        }
    }

    override fun read(input: JsonReader): LocalDateTime? {
        val unix = input.nextLong()
        return DateUtils.fromSecondsToDateTime(unix)
    }
}