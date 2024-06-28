package ru.javawebinar.topjava.repository.inmemory;

import ru.javawebinar.topjava.model.AbstractBaseEntity;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryRepository<T extends AbstractBaseEntity> {
    private static final AtomicInteger counter = new AtomicInteger(0);
    private final Map<Integer, T> repository = new ConcurrentHashMap<>();

    public T save(T entity) {
        if (entity.isNew()) {
            entity.setId(counter.incrementAndGet());
            repository.put(entity.getId(), entity);
            return entity;
        }
        return repository.computeIfPresent(entity.getId(), (currentId, oldEntity) -> entity);
    }

    public boolean delete(int id) {
        return repository.remove(id) != null;
    }

    public T get(int id) {
        return repository.get(id);
    }

    Collection<T> getCollection() {
        return repository.values();
    }

}
