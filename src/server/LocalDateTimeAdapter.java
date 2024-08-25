package server;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;

import static model.Task.DATE_TIME_FORMATTER;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    @Override
    public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
        if (localDateTime != null) {
            jsonWriter.value(localDateTime.format(DATE_TIME_FORMATTER));
        } else {
            jsonWriter.value("null");
        }
    }

    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
        String time = jsonReader.nextString();
        if (!time.equals("null")) {
            return LocalDateTime.parse(time, DATE_TIME_FORMATTER);
        } else {
            return null;
        }
    }
}