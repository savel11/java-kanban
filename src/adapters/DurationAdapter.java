package adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
        if (duration != null) {
            jsonWriter.value(duration.toHours() + ":" + duration.toMinutesPart());
        } else {
            jsonWriter.value("null");
        }
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        String time = jsonReader.nextString();
        if (!time.equals("null")) {
            String[] timeParse = time.split(":");
            return Duration.ofHours(Long.parseLong(timeParse[0])).plus(Duration.ofMinutes(Long.parseLong(timeParse[1])));
        } else {
            return null;
        }
    }
}