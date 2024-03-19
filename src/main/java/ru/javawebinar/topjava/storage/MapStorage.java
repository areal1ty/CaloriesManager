package ru.javawebinar.topjava.storage;
import ru.javawebinar.topjava.model.Meal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class MapStorage implements MealStorage {
    private final Map<Integer, Meal> storage = new ConcurrentHashMap<>();
    private static final Logger log = Logger.getLogger(MapStorage.class.getName());

    @Override
    public void create(Meal m) {
        storage.put(m.getId(), m);
        log.info(m + "successfully saved");
    }

    @Override
    public Meal read(Integer uuid) {
        log.info("read");
        return storage.get(uuid);
    }

    @Override
    public List<Meal> readAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void update(Meal m) {
        storage.put(m.getId(), m);
        log.info(m + "updated");
    }

    @Override
    public void delete(Integer uuid) {
        storage.remove(uuid);
        log.info("removed");
    }

    @Override
    public void clear() {
        storage.clear();
        log.info("database cleared");
    }

    @Override
    public int size() {
        return storage.size();
    }
}
