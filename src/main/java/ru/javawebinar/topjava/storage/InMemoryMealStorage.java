package ru.javawebinar.topjava.storage;
import ru.javawebinar.topjava.model.Meal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class InMemoryMealStorage implements MealStorage {
    private static final Logger log = Logger.getLogger(InMemoryMealStorage.class.getName());
    private final Map<Integer, Meal> storage = new ConcurrentHashMap<>();

    @Override
    public Meal create(Meal m) {
        storage.put(m.getId(), m);
        log.info(m + "successfully saved");
        return m;
    }

    @Override
    public Meal read(int uuid) {
        log.info("read");
        return storage.get(uuid);
    }

    @Override
    public List<Meal> readAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Meal update(Meal m) {
        storage.put(m.getId(), m);
        log.info(m + "updated");
        return m;
    }

    @Override
    public void delete(int uuid) {
        storage.remove(uuid);
        log.info("removed");
    }

}
